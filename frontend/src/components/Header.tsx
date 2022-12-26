import Container from 'react-bootstrap/Container';
import Nav from 'react-bootstrap/Nav';
import Navbar from 'react-bootstrap/Navbar';
import Button from "react-bootstrap/Button";
import * as React from "react";
import {useContext} from "react";
import {StreamButtonsType, StreamStatus, UserRole} from "../types/types";
import {useKeycloak} from "@react-keycloak/web";
import {Link} from "react-router-dom"
import {useCurrentUser} from "../hooks/CurrentUserHook";
import {WebSocketContext} from "../contexts/WebSocketContext";

export default () => {
    const { keycloak } = useKeycloak();
    const currentUser = useCurrentUser();
    const stream = useContext(WebSocketContext)

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

    const changeStreamState = async (mode: "start" | "stop") => {
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
                        { currentUser.hasRole(UserRole.ADMIN) && stream?.streamStatus !== undefined &&
                            <Button
                                className="mr-2"
                                variant={streamButtons[stream.streamStatus].variant}
                                onClick={streamButtons[stream.streamStatus].onClick}
                                disabled={streamButtons[stream.streamStatus].disabled}>
                                {streamButtons[stream.streamStatus].text}
                            </Button>
                        }
                    </Container>
                </Navbar.Collapse>
            </Container>
        </Navbar>
    );
}
