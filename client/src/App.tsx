import { useState, useEffect, ChangeEvent } from "react";
import coronaLogo from "/corona.svg";
import axios from "axios";
import { ToastContainer, toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import { Line } from "react-chartjs-2";
import * as dayjs from "dayjs";
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
    const [isImportingVaccinations, setIsImportingVaccinations] = useState(false);
    const [isImportingCases, setIsImportingCases] = useState(false);

    const [countryList, setCountryList] = useState<string[] | null>(null);

    const [startDate, setStartDate] = useState(dayjs().format("YYYY-MM-DD"));
    const [endDate, setEndDate] = useState(dayjs().format("YYYY-MM-DD"));
    const [chartData, setChartData] = useState<any | null>(null);
    const [selectedCountries, setSelectedCountries] = useState<string[]>([]);

    const [selectedOptions, setSelectedOptions] = useState({
        vaccinations: false,
        newCases: false,
        deaths: false,
    });

    const handleStartDateChange = (event: ChangeEvent<HTMLInputElement>) => {
        if (new Date(event.target.value) <= new Date(endDate)) {
            setStartDate(event.target.value);
        } else {
            toast.info("Start date must be before end date");
        }
    };

    const handleEndDateChange = (event: ChangeEvent<HTMLInputElement>) => {
        if (new Date(startDate) <= new Date(event.target.value)) {
            setEndDate(event.target.value);
        } else {
            toast.info("End date must be after start date");
        }
    };

    const generateDateRange = (startDate: string, endDate: string) => {
        const labels: string[] = [];
        const date1 = new Date(startDate);
        const date2 = new Date(endDate);

        while (date1 <= date2) {
            labels.push(dayjs(date1).format("YYYY-MM-DD"));
            date1.setDate(date1.getDate() + 1);
        }
        return labels;
    };

    const generateWeeklyDateRange = (startDate: string, endDate: string, delay: number) => {
        const labels: string[] = [];
        const date1 = dayjs(startDate).add(delay, 'day').toDate();
        const date2 = new Date(endDate);

        while (date1 <= date2) {
            labels.push(dayjs(date1).format("YYYY-MM-DD"));
            date1.setDate(date1.getDate() + 7);
        }

        return labels;
    };

    const handleCheckboxChange = (
        event: ChangeEvent<HTMLInputElement>
    ) => {
        const { name, checked } = event.target;
        setSelectedOptions((prevOptions) => ({
            ...prevOptions,
            [name]: checked,
        }));
    }

    const handleGenerateData = async () => {
        try {
            const countries = await axios.get("http://localhost:8000/api/countries");
            let countryIds: number[] = [];
            const { vaccinations, newCases, deaths } = selectedOptions;

            if (selectedCountries.includes("all")) {
                countryIds = Object.values(countries.data.data);
            } else {
                countryIds = selectedCountries.map((countryName) => {
                    const countryId = countries.data.data[countryName];
                    return countryId;
                });
            }

            let labels;
            const datasets = [];

            if (deaths) {
                const responseDeaths = await axios.get("http://localhost:8000/api/deaths", {
                    params: {
                        begin_date: startDate,
                        end_date: endDate,
                        countries: countryIds,
                    },
                });
                const deathsData = responseDeaths.data;
                const deaths = Object.values(deathsData);
                datasets.push({
                    label: "Deaths",
                    data: deaths,
                    borderColor: "#ff0000",
                    backgroundColor: "#ff0000",
                });
                labels = generateDateRange(startDate, endDate);
            }

            if (newCases) {
                const responseNewCases = await axios.get("http://localhost:8000/api/cases", {
                    params: {
                        begin_date: startDate,
                        end_date: endDate,
                        countries: countryIds,
                    },
                });
                const newCasesData = responseNewCases.data;
                const newCases = Object.values(newCasesData);
                datasets.push({
                    label: "New Cases",
                    data: newCases,
                    borderColor: "#f0f257",
                    backgroundColor: "#f0f257",
                });
                labels = generateDateRange(startDate, endDate);
            }

            if (vaccinations) {
                const responseVaccinations = await axios.get("http://localhost:8000/api/vaccinations", {
                    params: {
                        begin_date: startDate,
                        end_date: endDate,
                        countries: countryIds,
                    },
                });

                const vaccinationsData = responseVaccinations.data;
                const firstDateInData = Object.keys(vaccinationsData)[0];
                const delay = dayjs(firstDateInData).diff(dayjs(startDate), 'day');

                if (delay === 0) {
                    labels = generateDateRange(startDate, endDate);
                } else {
                    labels = generateWeeklyDateRange(startDate, endDate, delay);
                }

                const vaccinations = Object.values(vaccinationsData);
                datasets.push({
                    label: "Vaccinations",
                    data: vaccinations,
                    borderColor: "#00ff00",
                    backgroundColor: "#00ff00",
                });
            }

            const data = {
                labels: labels,
                datasets: datasets,
            };
            setChartData(data);
        } catch (error) {
            console.error(error);
            setChartData(null);
        }
    };

    const handleCountryCheckboxChange = (
        event: ChangeEvent<HTMLInputElement>
    ) => {
        const { name, checked } = event.target;
        if (name === "all") {
            if (checked) {
                setSelectedCountries(countryList ? [...countryList, "all"] : ["all"]);
            } else {
                setSelectedCountries([]);
            }
        } else {
            setSelectedCountries(prevSelectedCountries => {
                const updatedSelectedCountries = checked
                    ? [...prevSelectedCountries, name]
                    : prevSelectedCountries.filter(country => country !== name);
                return updatedSelectedCountries;
            });
        }
    };

    async function importCases() {
        setIsImportingCases(true);

        await sendImportRequest("http://localhost:80/api/import/cases");

        setIsImportingCases(false);
    }

    async function importVaccinations() {
        setIsImportingVaccinations(true);

        await sendImportRequest("http://localhost:8000/api/import/vaccinations");

        setIsImportingVaccinations(false);
    }

    async function getCountriesList() {
        try {
            const countriesObj = await axios.get(
                "http://localhost:80/api/countries"
            );
            const countryNames = Object.keys(countriesObj.data.data);
            setCountryList(countryNames);
        } catch (error) {
            console.error(error);
            setCountryList(null);
        }
    }
    useEffect(() => {
        getCountriesList();
    }, []);

    return (
        <div
            style={{
                display: "grid",
                maxHeight: "100vh",
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
                        backgroundColor: isImportingCases ? "#a1a1aa" : "#0284c7",
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
                        backgroundColor: isImportingVaccinations ? "#a1a1aa" : "#0284c7",
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
                    padding: "10px",
                    display: "grid",
                    gridTemplateColumns: "max-content 1fr",
                    gridTemplateRows: "minmax(0,1fr) 200px", // @Skic minmax is necessary for maxHeight to work in children
                    boxSizing: "border-box", // @Skic For padding to not create overflow
                    maxHeight: "100vh",
                }}
            >
                <div
                    className="data-picker"
                    style={{
                        display: "flex",
                        gap: "5px",
                        flexDirection: "column",
                        maxHeight: "100%",
                    }}
                >
                    <div style={{ overflowY: "auto", flexShrink: 1 }}>
                        <div key="all">
                            <span>Cały świat</span>
                            <input
                                type="checkbox"
                                name="all"
                                id="all"
                                onChange={handleCountryCheckboxChange}
                            />
                        </div>
                        {countryList?.map((country) => (
                            <div key={country}>
                                <input
                                    type="checkbox"
                                    name={country}
                                    id={country}
                                    onChange={handleCountryCheckboxChange}
                                />
                                <span>{country}</span>
                            </div>
                        )) ?? "Couldn't load countries"}
                    </div>
                    <br />
                    <h3 style={{ margin: "0" }}>Daty</h3>
                    <div>
                        <label>Od: </label>
                        <input
                            type="date"
                            id="date1"
                            value={startDate}
                            onChange={handleStartDateChange}
                            min={"2020-01-03"}
                            max={"2023-05-17"}
                        />
                    </div>
                    <div>
                        <label>Do: </label>
                        <input
                            type="date"
                            id="date2"
                            value={endDate}
                            onChange={handleEndDateChange}
                            min={"2020-01-03"}
                            max={"2023-05-17"}
                        />
                    </div>
                    <br />
                    <div>
                        <h3 style={{ margin: "0" }}>COVID-19 data</h3>
                        <input
                            type="checkbox"
                            name="vaccinations"
                            id="vaccinations"
                            onChange={handleCheckboxChange}
                        />
                        <span>Vaccinations</span>
                    </div>
                    <div>
                        <input
                            type="checkbox"
                            name="newCases"
                            id="newCases"
                            onChange={handleCheckboxChange}
                        />
                        <span>New Cases</span>
                    </div>
                    <div>
                        <input
                            type="checkbox"
                            name="deaths"
                            id="deaths"
                            onChange={handleCheckboxChange}
                        />
                        <span>Deaths</span>
                    </div>

                    <button onClick={handleGenerateData}>Pokaż wykres</button>
                </div>
                <div
                    className="chart-container"
                    style={{ position: "relative", width: "95%" }} //@Skic Required for charts to scale properly
                >
                    {chartData ? (
                        <Line data={chartData} />
                    ) : (
                        <p>Podaj dane, aby wyświetlić wykres</p>
                    )}
                </div>
            </main>
        </div>
    );
}

export default App;
