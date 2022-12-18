import Container from 'react-bootstrap/Container';
import Nav from 'react-bootstrap/Nav';
import Navbar from 'react-bootstrap/Navbar';
import Button from "react-bootstrap/Button";
import * as React from "react";
import {useEffect, useState} from "react";
import {StreamButtonsType, StreamStatus, SystemInfo, UserRole} from "../types/types";
import {useKeycloak} from "@react-keycloak/web";
import {Link} from "react-router-dom"
import {useCurrentUser} from "../hooks/CurrentUserHook";
import useWebSocket from "react-use-websocket";

export default () => {
    const [systemInfo, setSystemInfo] = useState<SystemInfo | undefined>(undefined);
    const { keycloak } = useKeycloak();
    const currentUser = useCurrentUser();
    const [socketUrl, setSocketUrl] = useState("");
    const { lastJsonMessage } = useWebSocket(socketUrl, {
        shouldReconnect: () => true
    })

    const streamButtons: StreamButtonsType = {
        STARTING: {
            variant: "primary",
            onClick: () => {},
            disabled: true,
            text: "Stream starting",
            nextStatus: StreamStatus.RUNNING
        },
        RUNNING: {
            variant: "danger",
            onClick: () => changeStreamState("stop"),
            disabled: false,
            text: "Stop stream",
            nextStatus: StreamStatus.STOPPING
        },
        STOPPING: {
            variant: "danger",
            onClick: () => {},
            disabled: true,
            text: "Stream stopping",
            nextStatus: StreamStatus.STOPPED
        },
        STOPPED: {
            variant: "primary",
            onClick: () => changeStreamState("start"),
            disabled: false,
            text: "Start stream",
            nextStatus: StreamStatus.STARTING
        }
    }

    useEffect(() => {
        const fetchData = async () => {
            if (currentUser.isAuthenticated) {
                let rawSystemData = await fetch("/api/system", {
                    headers: {
                        "Authorization": "Bearer " + keycloak.token
                    }
                })
                let systemData = await rawSystemData.json()
                setSystemInfo(systemData)
            }
        }
        fetchData()
    }, [currentUser.isAuthenticated])

    useEffect(() => {
        if (currentUser.isAuthenticated) {
            let wsRoot = `${(window.location.protocol === "https:") ? "wss://" : "ws://"}${window.location.host}`
            setSocketUrl(`${wsRoot}/api/ws?access_token=${keycloak.token}`)
        }
    }, [currentUser.isAuthenticated])

    useEffect(() => {
        if (lastJsonMessage !== null) {
            setSystemInfo(lastJsonMessage)
        }
    }, [lastJsonMessage]);

    const changeStreamState = async (mode: "start" | "stop") => {
        if (systemInfo !== undefined) {
            let newSystemInfo = {
                streamStatus: streamButtons[systemInfo.streamStatus].nextStatus
            }
            setSystemInfo(newSystemInfo)
        }
        await fetch(`/api/stream/control/${mode}`, {
            method: 'POST',
            headers: {
                "Authorization": "Bearer " + keycloak.token
            }
        })
    }

    return (
        <Navbar bg="light" expand="md">
            <Container>
                <Navbar.Brand>Surveillance</Navbar.Brand>
                <Navbar.Toggle aria-controls="basic-navbar-nav" />
                <Navbar.Collapse id="navbarNav">
                    <Nav className="mr-auto" as="ul">
                        <Nav.Item as="li">
                            <Nav.Link as={Link} to="/">Streams</Nav.Link>
                        </Nav.Item>
                        { currentUser.hasRole(UserRole.ADMIN) &&
                            <Nav.Item as="li">
                                <Nav.Link as={Link} to="/configure">Configure</Nav.Link>
                            </Nav.Item>
                        }
                        { currentUser.hasRole(UserRole.ADMIN) &&
                            <Nav.Item as="li">
                                <Nav.Link as={Link} to="/actuator">Actuator</Nav.Link>
                            </Nav.Item>
                        }
                    </Nav>
                    <Container fluid className="p-0 d-flex justify-content-between flex-row-reverse">
                        { !currentUser.isAuthenticated && (
                            <Button variant="outline-primary" onClick={() => keycloak.login()}>
                                Login
                            </Button>
                        )}
                        { currentUser.isAuthenticated && (
                            <Button variant="outline-primary" onClick={() => keycloak.logout()}>
                                Logout ({currentUser.preferredUsername})
                            </Button>
                        )}
                        { currentUser.hasRole(UserRole.ADMIN) && systemInfo?.streamStatus !== undefined &&
                            <Button
                                className="mr-2"
                                variant={streamButtons[systemInfo.streamStatus].variant}
                                onClick={streamButtons[systemInfo.streamStatus].onClick}
                                disabled={streamButtons[systemInfo.streamStatus].disabled}>
                                {streamButtons[systemInfo.streamStatus].text}
                            </Button>
                        }
                    </Container>
                </Navbar.Collapse>
            </Container>
        </Navbar>
    );
}
