import { useState, ChangeEvent, useEffect, useRef, useMemo } from "react";
import axios from "axios";
import { ToastContainer, toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
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
    LogarithmicScale,
    ChartData,
} from "chart.js";
import ImportBar from "./components/ImportBar";
import DateRangePicker from "@wojtekmaj/react-daterange-picker";
import "@wojtekmaj/react-daterange-picker/dist/DateRangePicker.css";
import "react-calendar/dist/Calendar.css";
import ChartContainer from "./components/ChartContainer";
import CountryList from "./components/CountryList";
ChartJS.register(
    CategoryScale,
    LinearScale,
    PointElement,
    LineElement,
    Title,
    Tooltip,
    LogarithmicScale,
    Legend
);

function App() {
    const [chartQuery, setChartQuery] = useState<{
        begin_date: string;
        end_date: string;
        countries: number[];
    }>({ begin_date: "2021-06-18", end_date: "2021-08-27", countries: [] });

    const [selectedOptions, setSelectedOptions] = useState({
        vaccinations: false,
        newCases: true, //DEVTEMP
        deaths: false,
    });

    const [chartData, setChartData] = useState<ChartData<"line"> | null>(null);

    function handleDateChange([startDate, endDate]: any) {
        setChartQuery((prevQuery) => {
            return {
                ...prevQuery,
                begin_date: dayjs(startDate).format("YYYY-MM-DD"),
                end_date: dayjs(endDate).format("YYYY-MM-DD"),
            };
        });
    }

    const handleOptionCheckboxChange = (
        event: ChangeEvent<HTMLInputElement>
    ) => {
        const { name, checked } = event.target;
        setSelectedOptions((prevOptions) => ({
            ...prevOptions,
            [name]: checked,
        }));
    };

    const handleSelectedCountries = useMemo(() => {
        return function (countries: number[]) {
            setChartQuery((prevQuery) => {
                return {
                    ...prevQuery,
                    countries,
                };
            });
        };
    }, []);

    const chartTimeout: any = useRef();

    useEffect(() => {
        const handleGenerateData = async () => {
            try {
                const { vaccinations, newCases, deaths } = selectedOptions;

                const datasets: any[] = [];

                if (deaths) {
                    const responseDeaths = await axios.get(
                        "http://localhost:80/api/deaths",
                        {
                            params: chartQuery,
                        }
                    );
                    const deathsData = responseDeaths.data;
                    const deaths = [];
                    for (const [x, y] of Object.entries(deathsData)) {
                        deaths.push({ x, y });
                    }

                    datasets.push({
                        label: "Deaths",
                        data: deaths,
                        borderColor: "#ff0000",
                        backgroundColor: "#ff0000",
                    });
                }

                if (newCases) {
                    const responseNewCases = await axios.get(
                        "http://localhost:80/api/cases",
                        {
                            params: chartQuery,
                        }
                    );
                    const newCasesData = responseNewCases.data;
                    const newCases = [];
                    for (const [x, y] of Object.entries(newCasesData)) {
                        newCases.push({ x, y });
                    }

                    datasets.push({
                        label: "New Cases",
                        data: newCases,
                        borderColor: "#f0f257",
                        backgroundColor: "#f0f257",
                    });
                }

                if (vaccinations) {
                    const responseVaccinations = await axios.get(
                        "http://localhost:80/api/vaccinations",
                        {
                            params: chartQuery,
                        }
                    );

                    const vaccinationsData: object = responseVaccinations.data;

                    const vaccinations = [];
                    for (const [x, y] of Object.entries(vaccinationsData)) {
                        vaccinations.push({ x, y });
                    }

                    datasets.push({
                        label: "Vaccinations",
                        yAxisID: "sum",
                        data: vaccinations,
                        borderColor: "#00ff00",
                        backgroundColor: "#00ff00",
                    });
                }

                const data = {
                    datasets: datasets,
                };
                console.log(data);
                setChartData(data);
            } catch (error) {
                console.error(error);
                setChartData(null);
            }
        };

        clearTimeout(chartTimeout.current);

        chartTimeout.current = setTimeout(() => {
            handleGenerateData();
            console.log("hej");
        }, 500);
    }, [chartQuery, selectedOptions]);

    return (
        <>
            <div
                style={{
                    display: "grid",
                    maxHeight: "100vh",
                    gridTemplateColumns: "250px 1fr",
                }}
            >
                <ImportBar />
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
                            gap: "20px",
                            flexDirection: "column",
                            maxHeight: "100%",
                        }}
                    >
                        <CountryList
                            selectedCountries={chartQuery.countries}
                            handleSelectedCountries={handleSelectedCountries}
                            style={{ overflowY: "auto", flexShrink: 1 }}
                        />
                        <DateRangePicker
                            onChange={handleDateChange}
                            value={[
                                new Date(chartQuery.begin_date),
                                new Date(chartQuery.end_date),
                            ]}
                        />
                        <h3 style={{ margin: "0" }}>COVID-19 data</h3>
                        <div>
                            <input
                                type="checkbox"
                                name="vaccinations"
                                id="vaccinations"
                                onChange={handleOptionCheckboxChange}
                                checked={selectedOptions.vaccinations}
                            />
                            <span>Vaccinations</span>
                        </div>
                        <div>
                            <input
                                type="checkbox"
                                name="newCases"
                                id="newCases"
                                onChange={handleOptionCheckboxChange}
                                checked={selectedOptions.newCases}
                            />
                            <span>New Cases</span>
                        </div>
                        <div>
                            <input
                                type="checkbox"
                                name="deaths"
                                id="deaths"
                                onChange={handleOptionCheckboxChange}
                                checked={selectedOptions.deaths}
                            />
                            <span>Deaths</span>
                        </div>
                    </div>
                    <ChartContainer data={chartData} />
                </main>
            </div>
            <ToastContainer position={toast.POSITION.BOTTOM_CENTER} />
        </>
    );
}

export default App;
