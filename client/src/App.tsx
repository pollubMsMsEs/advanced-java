import { useEffect, useState } from "react";
import coronaLogo from "/corona.svg";
import axios from "axios";
import { ToastContainer, toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import { Line } from "react-chartjs-2";
import {
    Chart as ChartJS,
    CategoryScale,
    LinearScale,
    PointElement,
    LineElement,
    Title,
    Tooltip,
    Legend,
} from "chart.js";
import failPromise from "./scripts/failPromise";

ChartJS.register(
    CategoryScale,
    LinearScale,
    PointElement,
    LineElement,
    Title,
    Tooltip,
    Legend
);

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

function App() {
    const [isImportingVaccinations, setIsImportingVaccinations] =
        useState(false);
    const [isImportingCases, setIsImportingCases] = useState(false);

    const [countryList, setCountryList] = useState<string[] | null>([]);

    useEffect(() => {
        getCountriesList();
    }, []);

    const data = {
        labels: ["label 1", "label 2", "label 3"],
        datasets: [
            {
                label: "data 1",
                data: [5, 12, 7],
                borderColor: `#f0f257`,
                backgroundColor: `#f0f257`,
            },
        ],
    };

    const options = {
        responsive: true,
        plugins: {
            legend: {
                position: "top" as const,
            },
            title: {
                display: true,
                text: "Chart",
            },
        },
    };

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

    async function getCountriesList() {
        try {
            const countriesObj = await axios.get(
                "http://localhost:8000/api/countries"
            );

            console.log(countriesObj);

            const temp: string[] = [];
            for (const [name, _] of Object.entries(countriesObj.data.data)) {
                temp.push(name);
            }
            setCountryList(temp);
        } catch (error) {
            console.log(error);
            setCountryList(null);
        }
    }

    return (
        <div
            style={{
                display: "grid",
                height: "100vh",
                gridTemplateColumns: "250px 1fr",
            }}
        >
            <aside
                className="aside"
                style={{
                    display: "flex",
                    flexDirection: "column",
                    alignItems: "center",
                    gap: "10px",
                }}
            >
                <header style={{ display: "flex", gap: "20px" }}>
                    <h1>Covid visualizer</h1>
                    <img width="50px" src={coronaLogo} alt="Vite" />
                </header>
                <div
                    style={{
                        alignSelf: "stretch",
                        marginTop: "-20px",
                    }}
                >
                    <hr />
                </div>
                <button
                    onClick={importCases}
                    disabled={isImportingCases || isImportingVaccinations}
                    className="button"
                    style={{
                        color: "aliceblue",
                        backgroundColor: isImportingCases
                            ? "#a1a1aa"
                            : "#0284c7",
                        border: "none",
                        padding: "10px",
                        borderRadius: "5px",
                    }}
                >
                    Import Cases
                </button>

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
            </aside>
            <main
                style={{
                    maxHeight: "100vh",
                    padding: "10px",
                    display: "grid",
                    gridTemplateColumns: "150px 1fr",
                    gridTemplateRows: "1fr 200px",
                }}
            >
                <div>
                    <div>Countries</div>
                    <div style={{ overflowY: "clip" }}>
                        {countryList?.map((country) => (
                            <div>
                                <span>{country}</span>
                                <input type="checkbox" name="" id="" />
                            </div>
                        )) ?? "Couldn't load countries"}
                    </div>
                </div>
                <div
                    className="chart-container"
                    style={{ position: "relative", width: "95%" }} //@Skic Required for charts to scale properly
                >
                    <Line data={data} options={options} />
                </div>
            </main>
        </div>
    );
}

export default App;
