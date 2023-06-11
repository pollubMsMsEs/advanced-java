import { useState } from "react";
import { AuthenticationContext } from "../stateContext";

export function AuthenticationContextProvider({ children }: any) {
    const [user, setUser] = useState({
        email: "marcin@ewa",
    });
    const [token, _setToken] = useState(localStorage.getItem("JWT_TOKEN"));

    const setToken = (token: string | null) => {
        _setToken(token);
        if (token) {
            localStorage.setItem("JWT_TOKEN", token);
        } else {
            localStorage.removeItem("JWT_TOKEN");
        }
    };

    return (
        <AuthenticationContext.Provider
            value={{
                user,
                token,
                setUser,
                setToken,
            }}
        >
            {children}
        </AuthenticationContext.Provider>
    );
}
