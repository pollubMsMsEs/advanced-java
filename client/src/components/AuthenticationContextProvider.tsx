import { useState } from "react";
import { AuthenticationContext } from "../StateContext.js";

export function AuthenticationContextProvider({ children }: any) {
    const [user, _setUser] = useState<{
        name: string;
        email: string;
        role: "ADMIN" | "USER";
    }>(JSON.parse(localStorage.getItem("USER") ?? "{}"));
    const [token, _setToken] = useState(localStorage.getItem("JWT_TOKEN"));

    const setToken = (token: string | null) => {
        _setToken(token);
        if (token) {
            localStorage.setItem("JWT_TOKEN", token);
        } else {
            localStorage.removeItem("JWT_TOKEN");
        }
    };

    const setUser = (user: {
        name: string;
        email: string;
        role: "ADMIN" | "USER";
    }) => {
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
