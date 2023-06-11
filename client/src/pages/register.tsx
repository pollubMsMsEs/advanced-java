import { useState, useEffect } from "react";
import { Link, useNavigate } from "react-router-dom";

export function Register() {
    const [user, setUser] = useState({
        email: "",
        password: "",
    });

    useEffect(() => {
        document.title = "Register | Covid Visualizer";
    }, []);

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
            ></div>
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
                        Register
                    </h1>

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
                            backgroundColor: "#a78a7f",
                            color: "white",
                            border: "none",
                            cursor: "pointer",
                        }}
                        onClick={Register}
                    >
                        Register
                    </button>
                    <Link
                        to="/login"
                        style={{
                            textAlign: "center",
                            color: "black",
                            textDecoration: "underline",
                            fontSize: "14px",
                        }}
                    >
                        Already have an account? Login
                    </Link>
                </div>
            </div>
        </div>
    );
}

export default Register;
