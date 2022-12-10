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

export interface SystemInfo {
    streamActive: boolean
}
