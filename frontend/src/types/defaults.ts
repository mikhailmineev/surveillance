import {User, UserRole} from "./types";

export function anonymousUser(): User {
    return {
        role: UserRole.ANONYMOUS
    }
}
