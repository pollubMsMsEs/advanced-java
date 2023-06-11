import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import axiosClient from "../axiosClient";
import { useAuthenticationContext } from "../stateContext";
export default function Login() {
    const [user, setUser] = useState({
        email: "",
        password: "",
    });

    const [errors, setErrors] = useState<{ [key: string]: string[] }>({});

    useEffect(() => {
        document.title = "Login | Covid Visualizer";
    }, []);

    const context = useAuthenticationContext();

    async function onSubmit() {
        try {
            const result = await axiosClient.post("/login", user);
            console.log(result);
            const returnedUser = {
                name: result.data.user.name,
                email: result.data.user.email,
                role: result.data.user.role,
            };

            context.setToken(result.data.authorisation.token);
            context.setUser(returnedUser);
        } catch (error: any) {
            if (error.response.status === 422) {
                setErrors(error.response.data.errors);
            } else if (error.response.status === 401) {
                setErrors({ login: ["Bad credentials"] });
            }
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
                backgroundColor: "#d6ccc2",
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
                <h1
                    style={{
                        fontSize: "32px",
                        fontWeight: "bold",
                        textTransform: "uppercase",
                        margin: "0",
                        color: "#780000",
                    }}
                >
                    Covid Visualizer
                </h1>
            </div>
            <div
                style={{
                    backgroundColor: "white",
                    padding: "20px",
                    width: "500px",
                    borderRadius: "8px",
                    boxShadow: "0px 2px 4px rgba(0, 0, 0, 0.1)",
                }}
            >
                <div
                    style={{
                        display: "flex",
                        flexDirection: "column",
                        gap: "10px",
                    }}
                >
                    <h1
                        style={{
                            fontSize: "24px",
                            fontWeight: "bold",
                            textAlign: "center",
                            textTransform: "uppercase",
                        }}
                    >
                        Login
                    </h1>
                    <div>
                        {Object.entries(errors).map(([field, values]: any) =>
                            values.map((v: any) => (
                                <div key={`${field}${v}`}>{v}</div>
                            ))
                        )}
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
                        type="button"
                        style={{
                            padding: "10px",
                            borderRadius: "4px",
                            backgroundColor: "#a78a7f",
                            color: "white",
                            border: "none",
                            cursor: "pointer",
                        }}
                        onClick={onSubmit}
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
            </div>
        </div>
    );
}
