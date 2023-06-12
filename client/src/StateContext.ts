import { createContext, useContext } from "react";

export const AuthenticationContext = createContext<{
    user: { name: string; email: string; role: string };
    token: string | null;
    setUser: any;
    setToken: any;
}>({
    user: { name: "", email: "", role: "" },
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
