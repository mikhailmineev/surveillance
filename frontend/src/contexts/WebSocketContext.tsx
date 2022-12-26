import React, {useEffect, useState} from "react";
import useWebSocket from "react-use-websocket";
import {useKeycloak} from "@react-keycloak/web";
import {Stream} from "../types/types";

export const WebSocketContext = React.createContext<Stream | undefined>(undefined)

export const WebSocketProvider = ({ children } : {children : JSX.Element}) => {
    const [stream, setStream] = useState<Stream | undefined>(undefined);
    const { keycloak } = useKeycloak();
    const [socketUrl, setSocketUrl] = useState("");
    const { lastJsonMessage } = useWebSocket(socketUrl, {
        shouldReconnect: () => true
    })

    useEffect(() => {
        const fetchData = async () => {
            if (keycloak.authenticated) {
                let rawSystemData = await fetch("/api/stream", {
                    headers: {
                        "Authorization": "Bearer " + keycloak.token
                    }
                })
                if (rawSystemData.status === 401) {
                    keycloak.onTokenExpired?.()
                } else {
                    let systemData = await rawSystemData.json()
                    setStream(systemData)
                }

            }
        }
        fetchData()
    }, [keycloak.authenticated])

    useEffect(() => {
        if (keycloak.authenticated) {
            let wsRoot = `${(window.location.protocol === "https:") ? "wss://" : "ws://"}${window.location.host}`
            setSocketUrl(`${wsRoot}/api/ws?access_token=${keycloak.token}`)
        }
    }, [keycloak.authenticated])

    useEffect(() => {
        if (lastJsonMessage !== null) {
            setStream(lastJsonMessage)
        }
    }, [lastJsonMessage]);

    return (
        <WebSocketContext.Provider value={stream}>
            { children }
        </WebSocketContext.Provider>
    )
}
