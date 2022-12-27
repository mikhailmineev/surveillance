import Button from "react-bootstrap/Button";
import * as React from "react";
import {StreamButtonsType, StreamStatus} from "../types/types";
import {useKeycloak} from "@react-keycloak/web";

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

export default ({status} : {status: StreamStatus}) => {
    const { keycloak } = useKeycloak();

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

    return (
        <Button
            className="mr-2"
            variant={streamButtons[status].variant}
            onClick={() => changeStreamState(streamButtons[status].action)}
            disabled={streamButtons[status].disabled}>
            {streamButtons[status].text}
        </Button>
    )

}