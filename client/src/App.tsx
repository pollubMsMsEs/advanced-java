import { useState, useEffect, ChangeEvent, useRef } from "react";
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
    LogarithmicScale,
    ChartOptions,
} from "chart.js";
import ImportBar from "./components/ImportBar";
import CountryCheckbox from "./components/CountryCheckbox";

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

interface CountryData {
    id: number;
    name: string;
}

function App() {
    const chartRef: any /*React.RefObject<Chart> | null | undefined*/ =
        useRef(null);

    const [countryList, setCountryList] = useState<
        { id: number; name: string }[] | null
    >(null);

    const [startDate, setStartDate] = useState("2021-06-18"); //DEVTEMP 2020-01-03
    const [endDate, setEndDate] = useState("2021-08-27"); //DEVTEMP 2023-05-17
    const [chartData, setChartData] = useState<any | null>(null);
    const [chartOptions, setChartOptions] = useState<
        ChartOptions<"line"> | undefined
    >();

    const [selectedCountries, setSelectedCountries] = useState<number[]>([]);
    const allCountriesChecked =
        selectedCountries.length === countryList?.length ?? 0;

    const [selectedOptions, setSelectedOptions] = useState({
        vaccinations: false,
        newCases: false,
        deaths: false,
    });

    useEffect(() => {
        getCountriesList();
    }, []);

    useEffect(() => {
        createChartOptions();
    }, [chartData]);

    const handleStartDateChange = (event: ChangeEvent<HTMLInputElement>) => {
        if (new Date(event.target.value) <= new Date(endDate)) {
            setStartDate(event.target.value);
        } else {
            toast.info("Start date must be before end date");
        }
    };

    function createChartOptions() {
        const ctx: CanvasRenderingContext2D | undefined =
            chartRef?.current?.ctx;
        if (ctx == null) return;

        console.log(chartRef);

        const gradient = ctx.createLinearGradient(
            0,
            0,
            0,
            chartRef?.current?.height ?? 200
        );
        gradient.addColorStop(0, "#ff0000D0");
        gradient.addColorStop(1, "#f0f257");
        setChartOptions({
            parsing: {
                xAxisKey: "x",
                yAxisKey: "y",
            },
            scales: {
                y: {
                    axis: "y",
                    type: "logarithmic",
                    position: "left",
                    border: {
                        color: gradient,
                        width: 2,
                    },
                    display: "auto",
                    title: {
                        text: "Cases & Deaths",
                        display: true,
                    },
                },
                sum: {
                    axis: "y",
                    type: "linear",
                    position: "right",
                    border: {
                        color: "#00ff00",
                        width: 2,
                    },
                    display: "auto",
                    title: {
                        text: "Vaccinations",
                        display: true,
                    },
                },
            },
        });
    }

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

    const generateWeeklyDateRange = (
        startDate: string,
        endDate: string,
        delay: number
    ) => {
        const labels: string[] = [];
        const date1 = dayjs(startDate).add(delay, "day").toDate();
        const date2 = new Date(endDate);

        while (date1 <= date2) {
            labels.push(dayjs(date1).format("YYYY-MM-DD"));
            date1.setDate(date1.getDate() + 7);
        }

        return labels;
    };

    const handleCheckboxChange = (event: ChangeEvent<HTMLInputElement>) => {
        const { name, checked } = event.target;
        setSelectedOptions((prevOptions) => ({
            ...prevOptions,
            [name]: checked,
        }));
    };

    const handleGenerateData = async () => {
        try {
            const { vaccinations, newCases, deaths } = selectedOptions;

            let labels;
            const datasets = [];
            console.log(selectedCountries);

            if (deaths) {
                const responseDeaths = await axios.get(
                    "http://localhost:80/api/deaths",
                    {
                        params: {
                            begin_date: startDate,
                            end_date: endDate,
                            countries: selectedCountries,
                        },
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
                labels = generateDateRange(startDate, endDate);
            }

            if (newCases) {
                const responseNewCases = await axios.get(
                    "http://localhost:80/api/cases",
                    {
                        params: {
                            begin_date: startDate,
                            end_date: endDate,
                            countries: selectedCountries,
                        },
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
                labels = generateDateRange(startDate, endDate);
            }

            if (vaccinations) {
                const responseVaccinations = await axios.get(
                    "http://localhost:80/api/vaccinations",
                    {
                        params: {
                            begin_date: startDate,
                            end_date: endDate,
                            countries: selectedCountries,
                        },
                    }
                );

                const vaccinationsData: object = responseVaccinations.data;
                const firstDateInData = Object.keys(vaccinationsData)[0];

                const delay = dayjs(firstDateInData).diff(
                    dayjs(startDate),
                    "day"
                );

                if (delay === 0) {
                    labels = generateDateRange(startDate, endDate);
                } else {
                    labels = generateWeeklyDateRange(startDate, endDate, delay);
                }

                labels = Object.keys(vaccinationsData);

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

    const handleAllCountriesCheckboxChange = ({
        checked,
    }: {
        checked: boolean;
    }) => {
        if (checked) {
            setSelectedCountries(countryList?.map((c) => c.id) ?? []);
        } else {
            setSelectedCountries([]);
        }
    };

    const handleCountryCheckboxChange = ({
        id,
        checked,
    }: {
        id: number;
        checked: boolean;
    }) => {
        setSelectedCountries((prevSelectedCountries) => {
            const updatedSelectedCountries = checked
                ? [...prevSelectedCountries, id]
                : prevSelectedCountries.filter((countryId) => countryId !== id);
            return updatedSelectedCountries;
        });
    };

    async function getCountriesList() {
        try {
            const countriesResponse = await axios.get(
                "http://localhost:80/api/countries"
            );
            const countriesObj: { [name: string]: number } =
                countriesResponse.data.data;

            const countries: CountryData[] = [];
            for (const [name, id] of Object.entries(countriesObj)) {
                countries.push({ id: id, name });
            }
            setCountryList(countries);
        } catch (error) {
            console.error(error);
            setCountryList(null);
        }
    }

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
                            gap: "5px",
                            flexDirection: "column",
                            maxHeight: "100%",
                        }}
                    >
                        <div style={{ overflowY: "auto", flexShrink: 1 }}>
                            {countryList && (
                                <CountryCheckbox
                                    country={{
                                        id: -1,
                                        name: "Whole world",
                                    }}
                                    updateSelected={
                                        handleAllCountriesCheckboxChange
                                    }
                                    style={{ fontWeight: "bold" }}
                                    checked={allCountriesChecked}
                                />
                            )}
                            {countryList?.map((country) => (
                                <CountryCheckbox
                                    key={country.id}
                                    country={country}
                                    updateSelected={handleCountryCheckboxChange}
                                    checked={selectedCountries.some(
                                        (v) => v === country.id
                                    )}
                                />
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

                        <button onClick={handleGenerateData}>
                            Pokaż wykres
                        </button>
                    </div>
                    <div
                        className="chart-container"
                        style={{ position: "relative", width: "95%" }} //@Skic Required for charts to scale properly
                    >
                        {chartData ? (
                            <Line
                                ref={chartRef}
                                data={chartData}
                                options={chartOptions}
                            />
                        ) : (
                            <p>Podaj dane, aby wyświetlić wykres</p>
                        )}
                    </div>
                </main>
            </div>
            <ToastContainer position={toast.POSITION.BOTTOM_CENTER} />
        </>
    );
}

export default App;
