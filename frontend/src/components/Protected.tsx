import {UserRole} from "../types/types";
import {useCurrentUser} from "../hooks/CurrentUserHook";

export default ({role, children} : {role?: UserRole, children : JSX.Element}) : JSX.Element | null => {
    const currentUser = useCurrentUser()

    if (!currentUser.isAuthenticated) {
        return null
    }
    if (role !== undefined && !currentUser.hasRole(role)) {
        return null
    }
    return children

}
