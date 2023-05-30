import { useState } from "react";
import coronaLogo from "/corona.svg";
import axios from "axios";
import { ToastContainer, toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import failPromise from "./scripts/failPromise";
//import './App.css'

function App() {
    const [isImportingVaccinations, setIsImportingVaccinations] =
        useState(false);
    const [isImportingCases, setIsImportingCases] = useState(false);

    async function sendImportRequest(url: string) {
        const toastId = toast.loading("Importing...");

        try {
            const result = await axios.put(url);
            console.log(result);

            if (result.data.error) {
                throw new Error(result.data.msg);
            }

            toast.update(toastId, {
                render: "Import succedded!",
                type: "success",
                isLoading: false,
                autoClose: 4000,
            });
        } catch (error: any) {
            toast.update(toastId, {
                render: `Import failed: ${error.message ?? ""}`,
                type: "error",
                isLoading: false,
                autoClose: 4000,
            });
        }
    }

    async function importCases() {
        setIsImportingCases(true);

        await sendImportRequest("http://localhost:8000/api/import/cases");

        setIsImportingCases(false);
    }

    async function importVaccinations() {
        setIsImportingVaccinations(true);

        await sendImportRequest(
            "http://localhost:8000/api/import/vaccinations"
        );

        setIsImportingVaccinations(false);
    }

    return (
        <>
            <header style={{ display: "flex", gap: "20px" }}>
                <h1>Covid visualizer</h1>
                <img width="50px" src={coronaLogo} alt="Vite" />
            </header>
            <button
                onClick={importCases}
                disabled={isImportingCases || isImportingVaccinations}
                className="button"
                style={{
                    color: "aliceblue",
                    backgroundColor: isImportingCases ? "#a1a1aa" : "#0284c7",
                    border: "none",
                    padding: "10px",
                    borderRadius: "5px",
                }}
            >
                Import Cases
            </button>
            <br />
            <hr />
            <button
                onClick={importVaccinations}
                disabled={isImportingCases || isImportingVaccinations}
                className="button"
                style={{
                    color: "aliceblue",
                    backgroundColor: isImportingVaccinations
                        ? "#a1a1aa"
                        : "#0284c7",
                    border: "none",
                    padding: "10px",
                    borderRadius: "5px",
                }}
            >
                Import Vaccinations
            </button>
            <ToastContainer position={toast.POSITION.BOTTOM_CENTER} />
        </>
    );
}

export default App;
