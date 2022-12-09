export enum UserRole {
    CONSUMER = "CONSUMER",
    PUBLISHER = "PUBLISHER",
    ADMIN = "ADMIN"
}

export interface SystemInfo {
    streamActive: boolean
}
