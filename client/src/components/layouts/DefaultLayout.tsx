import { Navigate, Outlet } from "react-router-dom";
import { useAuthenticationContext } from "../../stateContext";

export default function DefaultLayout() {
    const { token } = useAuthenticationContext();

    if (!token) {
        return <Navigate to="/login" />;
    }

    return <Outlet />;
}
