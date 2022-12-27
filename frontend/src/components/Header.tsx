import Container from 'react-bootstrap/Container';
import Nav from 'react-bootstrap/Nav';
import Navbar from 'react-bootstrap/Navbar';
import Button from "react-bootstrap/Button";
import * as React from "react";
import {useContext} from "react";
import {UserRole} from "../types/types";
import {useKeycloak} from "@react-keycloak/web";
import {Link} from "react-router-dom"
import {useCurrentUser} from "../hooks/CurrentUserHook";
import {WebSocketContext} from "../contexts/WebSocketContext";
import StreamButton from "./StreamButton";

export default () => {
    const { keycloak } = useKeycloak();
    const currentUser = useCurrentUser();
    const stream = useContext(WebSocketContext)

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
                            <StreamButton status={stream.streamStatus} />
                        }
                    </Container>
                </Navbar.Collapse>
            </Container>
        </Navbar>
    );
}
