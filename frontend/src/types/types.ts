export enum UserRole {
    ANONYMOUS = "ANONYMOUS",
    CONSUMER = "CONSUMER",
    PUBLISHER = "PUBLISHER",
    ADMIN = "ADMIN"
}

export interface User {
    role: UserRole
}

export interface SystemInfo {
    streamActive: boolean
}
