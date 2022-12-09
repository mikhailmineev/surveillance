import Container from 'react-bootstrap/Container';
import Nav from 'react-bootstrap/Nav';
import Navbar from 'react-bootstrap/Navbar';
import Button from "react-bootstrap/Button";
import {useEffect, useState} from "react";
import {SystemInfo, User, UserRole} from "../types/types";
import {anonymousUser} from "../types/defaults";

function Header() {
    const [currentUser, setCurrentUser] = useState<User>(anonymousUser());
    const [systemInfo, setSystemInfo] = useState<SystemInfo | undefined>(undefined);

    useEffect(() => {
        const fetchData = async () => {
            let rawUserData = await fetch("/api/user")
            let rawSystemData = await fetch("/api/system")
            let userData = await rawUserData.json()
            let systemData = await rawSystemData.json()
            setCurrentUser(userData)
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
                            <Nav.Link href="#/">Streams</Nav.Link>
                        </Nav.Item>
                        { currentUser.role === UserRole.ADMIN &&
                            <Nav.Item as="li">
                                <Nav.Link href="#/configure">Configure</Nav.Link>
                            </Nav.Item>
                        }
                        { currentUser.role === UserRole.ADMIN &&
                            <Nav.Item as="li">
                                <Nav.Link href="#/actuatorui">Actuator</Nav.Link>
                            </Nav.Item>
                        }
                    </Nav>
                    <Container fluid className="p-0 d-flex justify-content-between flex-row-reverse">
                        <Button variant="outline-primary" href="/logout">Logout</Button>
                        { currentUser.role === UserRole.ADMIN && systemInfo?.streamActive === false &&
                            <Button className="mr-2" variant="primary" href="/stream/control/start">Start stream</Button>
                        }
                        { currentUser.role === UserRole.ADMIN && systemInfo?.streamActive === true &&
                            <Button className="mr-2" variant="danger" href="/stream/control/stop">Stop stream</Button>
                        }
                    </Container>
                </Navbar.Collapse>
            </Container>
        </Navbar>
    );
}

export default Header;
