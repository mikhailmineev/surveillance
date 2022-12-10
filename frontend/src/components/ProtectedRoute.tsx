import { useCurrentUser } from "../hooks/CurrentUserHook";
import * as React from "react";

export const ProtectedRoute = ({children} : {children : JSX.Element}) : JSX.Element => {
    const currentUser = useCurrentUser();

    return currentUser.isAuthenticated ? children :
        <div>
            <h1 className="text-green-800 text-4xl">401 Unauthenticated</h1>
        </div>
}
