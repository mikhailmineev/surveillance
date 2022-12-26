import * as React from 'react'
import {useEffect, useState} from "react";
import {useKeycloak} from "@react-keycloak/web";
import {Actuator} from "../types/types";
import {JsonViewer} from "@textea/json-viewer";
import { Form } from 'react-bootstrap';

export default () => {
    const [links, setLinks] = useState<Actuator>({})
    const [data, setData] = useState<any>({})
    const { keycloak } = useKeycloak();

    const fetchActuatorData = async function (link: string) {
        let croppedLink = link.replace("https://localhost", "")
        let rawActuatorData = await fetch(croppedLink, {
            headers: {
                "Authorization": "Bearer " + keycloak.token
            }
        })
        let actuatorData = await rawActuatorData.json()
        setData(actuatorData)
    }

    useEffect(() => {
        const fetchData = async () => {
            let rawActuatorData = await fetch("/api/actuator", {
                headers: {
                    "Authorization": "Bearer " + keycloak.token
                }
            })
            let actuatorData = await rawActuatorData.json()
            let links = actuatorData._links;
            delete links["self"]
            setLinks(links)
        }
        fetchData()
    }, [])
    return (
        <div className="container">
            <form>
                <h1>Actuator options</h1>
                <div className="mb-3">
                    <Form.Label htmlFor="actuatoroptions">Actuator endpoint</Form.Label>
                    <Form.Select id="actuatoroptions" onChange={(e) => {
                        e.target.value && fetchActuatorData(e.target.value)
                    }}>
                        <option value={undefined} defaultChecked={true}>Select...</option>
                        { Object.keys(links).map(key => {
                            return (
                                <option value={links[key].href}>{links[key].href}</option>
                            )})
                        }
                    </Form.Select>
                </div>
                <div className="mb-3">
                    <Form.Label>Actuator data</Form.Label>
                    <JsonViewer value={data} />
                </div>
            </form>
        </div>
    );
};
