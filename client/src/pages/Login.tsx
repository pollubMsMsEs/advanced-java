import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import axiosClient from "../axiosClient";
import { useAuthenticationContext } from "../StateContext";
import Logo from "../components/Logo";
export default function Login() {
    const [user, setUser] = useState({
        email: "",
        password: "",
    });

    const [errors, setErrors] = useState<{ [key: string]: string }>({});

    useEffect(() => {
        document.title = "Login | Covid Visualizer";
    }, []);

    const context = useAuthenticationContext();

    async function onSubmit() {
        try {
            const result = await axiosClient.post("/login", user);
            console.log(result);

            const returnedUser = {
                name: result.data.name,
                email: result.data.email,
                role: result.data.role,
            };

            context.setToken(result.data.token);
            context.setUser(returnedUser);
        } catch (error: any) {
            setErrors(error.response.data);

            console.error(error.response);
        }
    }

    return (
        <div
            style={{
                display: "flex",
                flexDirection: "column",
                justifyContent: "center",
                alignItems: "center",
                minHeight: "100vh",
            }}
        >
            <div
                style={{
                    display: "flex",
                    justifyContent: "center",
                    alignItems: "center",
                    marginBottom: "20px",
                }}
            >
                <Logo />
            </div>
            <form
                style={{
                    backgroundColor: "white",
                    padding: "20px",
                    width: "500px",
                    borderRadius: "8px",
                    boxShadow: "0px 2px 20px 5px rgba(0, 0, 0, 0.1)",
                }}
                onSubmit={(e) => {
                    e.preventDefault();
                    onSubmit();
                }}
            >
                <div
                    style={{
                        display: "flex",
                        flexDirection: "column",
                        gap: "10px",
                    }}
                >
                    <h2
                        style={{
                            fontSize: "24px",
                            fontWeight: "bold",
                            textAlign: "center",
                            textTransform: "uppercase",
                        }}
                    >
                        Login
                    </h2>
                    <div
                        style={{
                            color: "#e63946",
                        }}
                    >
                        {Object.entries(errors).map(([field, value]) => (
                            <div key={`${field}${value}`}>{value}</div>
                        ))}
                    </div>
                    <input
                        type="email"
                        placeholder="Enter your email"
                        value={user.email}
                        onChange={(e) =>
                            setUser({ ...user, email: e.target.value })
                        }
                        style={{
                            padding: "10px",
                            borderRadius: "4px",
                            border: "1px solid #ccc",
                        }}
                    />
                    <input
                        type="password"
                        placeholder="Enter your password"
                        value={user.password}
                        onChange={(e) =>
                            setUser({ ...user, password: e.target.value })
                        }
                        style={{
                            padding: "10px",
                            borderRadius: "4px",
                            border: "1px solid #ccc",
                        }}
                    />
                    <button
                        style={{
                            padding: "10px",
                            borderRadius: "4px",
                            backgroundColor: "#0284c7",
                            color: "white",
                            border: "none",
                            cursor: "pointer",
                        }}
                    >
                        Login
                    </button>
                    <Link
                        to="/register"
                        style={{
                            textAlign: "center",
                            color: "black",
                            textDecoration: "underline",
                            fontSize: "14px",
                        }}
                    >
                        Don't have an account? Register
                    </Link>
                </div>
            </form>
        </div>
    );
}
