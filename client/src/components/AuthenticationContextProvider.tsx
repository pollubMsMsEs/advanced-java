import { useState } from "react";
import { AuthenticationContext } from "../stateContext";

export function AuthenticationContextProvider({ children }: any) {
    const [user, _setUser] = useState(
        JSON.parse(localStorage.getItem("USER") ?? "{}")
    );
    const [token, _setToken] = useState(localStorage.getItem("JWT_TOKEN"));

    const setToken = (token: string | null) => {
        _setToken(token);
        if (token) {
            localStorage.setItem("JWT_TOKEN", token);
        } else {
            localStorage.removeItem("JWT_TOKEN");
        }
    };

    const setUser = (user: { name: string; email: string; role: string }) => {
        _setUser(user);
        localStorage.setItem("USER", JSON.stringify(user));
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
