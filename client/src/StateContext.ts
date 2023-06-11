import { createContext, useContext } from "react";

export const AuthenticationContext = createContext<{
    user: { email: string } | null;
    token: string | null;
    setUser: any;
    setToken: any;
}>({
    user: null,
    token: null,
    setUser: () => {
        console.error("setUser: Implement me!");
    },
    setToken: () => {
        console.error("setToken: Implement me!");
    },
});

export function useAuthenticationContext() {
    return useContext(AuthenticationContext);
}
