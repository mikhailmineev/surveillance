import Container from 'react-bootstrap/Container';
import Nav from 'react-bootstrap/Nav';
import Navbar from 'react-bootstrap/Navbar';
import Button from "react-bootstrap/Button";
import { useEffect, useState } from "react";
import { SystemInfo } from "../types/types";
import { useKeycloak } from "@react-keycloak/web";
import { Link } from "react-router-dom"
import * as React from "react";

function Header() {
    const [systemInfo, setSystemInfo] = useState<SystemInfo | undefined>(undefined);
    const { keycloak } = useKeycloak();

    useEffect(() => {
        const fetchData = async () => {
            let rawSystemData = await fetch("/api/system")
            let systemData = await rawSystemData.json()
            setSystemInfo(systemData)
        }
        fetchData()
    }, [])
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
                        { keycloak.tokenParsed?.resource_access?.surveillance.roles.includes("ADMIN") &&
                            <Nav.Item as="li">
                                <Nav.Link as={Link} to="/configure">Configure</Nav.Link>
                            </Nav.Item>
                        }
                        { keycloak.tokenParsed?.resource_access?.surveillance.roles.includes("ADMIN") &&
                            <Nav.Item as="li">
                                <Nav.Link as={Link} to="/actuator">Actuator</Nav.Link>
                            </Nav.Item>
                        }
                    </Nav>
                    <Container fluid className="p-0 d-flex justify-content-between flex-row-reverse">
                        {!keycloak.authenticated && (
                            <Button variant="outline-primary" onClick={() => keycloak.login()}>
                                Login
                            </Button>
                        )}
                        {!!keycloak.authenticated && (
                            <Button variant="outline-primary" onClick={() => keycloak.logout()}>
                                Logout ({keycloak.tokenParsed?.preferred_username})
                            </Button>
                        )}
                        { keycloak.tokenParsed?.resource_access?.surveillance.roles.includes("ADMIN") && systemInfo?.streamActive === false &&
                            <Button className="mr-2" variant="primary" href="/stream/control/start">Start stream</Button>
                        }
                        { keycloak.tokenParsed?.resource_access?.surveillance.roles.includes("ADMIN") && systemInfo?.streamActive === true &&
                            <Button className="mr-2" variant="danger" href="/stream/control/stop">Stop stream</Button>
                        }
                    </Container>
                </Navbar.Collapse>
            </Container>
        </Navbar>
    );
}

export default Header;
