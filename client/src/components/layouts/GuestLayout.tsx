import { Navigate, Outlet } from "react-router-dom";
import { useAuthenticationContext } from "../../stateContext";

export default function GuestLayout() {
    const { token } = useAuthenticationContext();

    if (token) {
        return <Navigate to="/" />;
    }

    return <Outlet />;
}
