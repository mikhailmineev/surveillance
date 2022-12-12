import { useCurrentUser } from "../hooks/CurrentUserHook";
import * as React from "react";

export const ProtectedRoute = ({children} : {children : JSX.Element}) : JSX.Element => {
    const currentUser = useCurrentUser();

    return currentUser.isAuthenticated ? children :
        <div className="container">
            <h1>401 Unauthenticated</h1>
        </div>
}
