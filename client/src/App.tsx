import { useState } from "react";
import coronaLogo from "/corona.svg";
import axios from "axios";
import { ToastContainer, toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
//import './App.css'

function App() {
    const [isImportingVaccinations, setIsImportingVaccinations] =
        useState(false);
    const [isImportingCases, setIsImportingCases] = useState(false);

    async function importCases() {
        setIsImportingCases(true);

        const result = await toast
            .promise(axios.put("http://localhost:8000/api/import/cases"), {
                pending: "Importing cases...",
                success: "Imported succesfully!",
                error: "Import failed",
            })
            .catch();

        console.log(result.data);

        setIsImportingCases(false);
    }

    async function importVaccinations() {
        setIsImportingVaccinations(true);

        const result = await axios.put(
            "http://localhost:8000/api/import/cases"
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
