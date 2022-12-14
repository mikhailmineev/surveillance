import {MouseEventHandler} from "react";

export enum UserRole {
    CONSUMER = "CONSUMER",
    PUBLISHER = "PUBLISHER",
    ADMIN = "ADMIN"
}

export interface CurrentUser {
    hasRole: (role: UserRole) => boolean,
    isAuthenticated: boolean,
    preferredUsername: string | undefined
}

export interface Actuator {
    [index: string]: ActuatorEntry;
}

export interface ActuatorEntry {
    href: string,
    templated: boolean
}

export type StreamButtonsType = {
    [index in StreamStatus]: StreamButtonType
}

export type StreamButtonType = {
    variant: string,
    onClick: MouseEventHandler<HTMLButtonElement>,
    disabled: boolean,
    text: string,
    nextStatus: StreamStatus
}

export interface SystemConfig {
    recorders: CameraConfig[],
    inputSources: InputSource[]
}

export interface CameraConfig {
    name: string,
    video: string,
    audio: string,
    inputFramerate: string,
    inputResolution: string
}

export interface InputSource {
    type: InputType,
    id: String,
    name: String,
    formats: InputFormat[],
}

export enum InputType {
    VIDEO = "VIDEO",
    AUDIO = "AUDIO"
}

export interface InputFormat {
    resolution: string,
    fps: string
}

export interface SystemInfo {
    streamStatus: StreamStatus
}

export enum StreamStatus {
    STARTING = "STARTING",
    RUNNING = "RUNNING",
    STOPPING = "STOPPING",
    STOPPED = "STOPPED"
}

export interface StreamRecord {
    date: string,
    videos: StreamRecordVideo[],
}

export interface StreamRecordVideo {
    name: string,
    cameraId: string,
    date: string
}
