import {useKeycloak} from "@react-keycloak/web";
import {CurrentUser, UserRole} from "../types/types";

export function useCurrentUser(): CurrentUser {
    const { keycloak } = useKeycloak();

    function hasRole(role: UserRole): boolean {
        return keycloak.tokenParsed?.resource_access?.surveillance.roles.includes(role.valueOf()) ?? false
    }

    function isAuthenticated(): boolean {
        return keycloak.authenticated ?? false
    }

    function preferredUsername(): string | undefined {
        return keycloak.tokenParsed?.preferred_username
    }

    return {
        hasRole : hasRole,
        isAuthenticated: isAuthenticated(),
        preferredUsername: preferredUsername()
    }
}
