import Button from "react-bootstrap/Button";
import * as React from "react";
import {StreamButtonsType, StreamStatus} from "../types/types";
import {useKeycloak} from "@react-keycloak/web";
import {useContext} from "react";
import {WebSocketContext} from "../contexts/WebSocketContext";

const streamButtons: StreamButtonsType = {
    STARTING: {
        variant: "primary",
        action: undefined,
        disabled: true,
        text: "Stream starting",
        nextStatus: StreamStatus.RUNNING
    },
    RUNNING: {
        variant: "danger",
        action: "stop",
        disabled: false,
        text: "Stop stream",
        nextStatus: StreamStatus.STOPPING
    },
    STOPPING: {
        variant: "danger",
        action: undefined,
        disabled: true,
        text: "Stream stopping",
        nextStatus: StreamStatus.STOPPED
    },
    STOPPED: {
        variant: "primary",
        action: "start",
        disabled: false,
        text: "Start stream",
        nextStatus: StreamStatus.STARTING
    }
}

export default () => {
    const { keycloak } = useKeycloak()
    const stream = useContext(WebSocketContext)

    const changeStreamState = async (mode: "start" | "stop" | undefined) => {
        if (mode === undefined) {
            return
        }
        await fetch(`/api/stream/control/${mode}`, {
            method: 'POST',
            headers: {
                "Authorization": "Bearer " + keycloak.token
            }
        })
    }

    if (stream === undefined) {
        return null
    }
    return (
        <Button
            className="mr-2"
            variant={streamButtons[stream.streamStatus].variant}
            onClick={() => changeStreamState(streamButtons[stream.streamStatus].action)}
            disabled={streamButtons[stream.streamStatus].disabled}>
            {streamButtons[stream.streamStatus].text}
        </Button>
    )
}
