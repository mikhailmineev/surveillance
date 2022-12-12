import * as React from 'react'
import {useEffect, useState} from "react";
import {useKeycloak} from "@react-keycloak/web";
import {Actuator} from "../types/types";

export default () => {
    const [links, setLinks] = useState<Actuator>({})
    const { keycloak } = useKeycloak();

    useEffect(() => {
        const fetchData = async () => {
            let rawActuatorData = await fetch("/api/actuator", {
                headers: {
                    "Authorization": "Bearer " + keycloak.token
                }
            })
            let actuatorData = await rawActuatorData.json()
            let links = actuatorData._links;
            setLinks(links)
        }
        fetchData()
    }, [])
    return (
        <div className="container">
            <h1>Actuator options</h1>
            <div id="actuatoroptions">
                { Object.keys(links).map(key => {
                    return (
                        <p><a href={links[key].href}>{links[key].href}</a></p>
                    )})
                }
            </div>
        </div>
    );
};
