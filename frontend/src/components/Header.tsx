import Container from 'react-bootstrap/Container';
import Nav from 'react-bootstrap/Nav';
import Navbar from 'react-bootstrap/Navbar';
import Button from "react-bootstrap/Button";
import * as React from "react";
import {UserRole} from "../types/types";
import {useKeycloak} from "@react-keycloak/web";
import {Link} from "react-router-dom"
import {useCurrentUser} from "../hooks/CurrentUserHook";
import StreamButton from "./StreamButton";
import Protected from "./Protected";

export default () => {
    const { keycloak } = useKeycloak()
    const currentUser = useCurrentUser()

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
                        <Protected role={UserRole.ADMIN}>
                            <Nav.Item as="li">
                                <Nav.Link as={Link} to="/configure">Configure</Nav.Link>
                            </Nav.Item>
                        </Protected>
                        <Protected role={UserRole.ADMIN}>
                            <Nav.Item as="li">
                                <Nav.Link as={Link} to="/actuator">Actuator</Nav.Link>
                            </Nav.Item>
                        </Protected>
                    </Nav>
                    <Container fluid className="p-0 d-flex justify-content-between flex-row-reverse">
                        <Protected>
                            <Button variant="outline-primary" onClick={() => keycloak.login()}>
                                Login
                            </Button>
                        </Protected>
                        <Protected>
                            <Button variant="outline-primary" onClick={() => keycloak.logout()}>
                                Logout ({currentUser.preferredUsername})
                            </Button>
                        </Protected>
                        <Protected role={UserRole.ADMIN}>
                            <StreamButton />
                        </Protected>
                    </Container>
                </Navbar.Collapse>
            </Container>
        </Navbar>
    );
}
