import { useState, useEffect, ChangeEvent } from "react";
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
import CountryCheckbox from "./components/CountryCheckbox";
import DateRangePicker from "@wojtekmaj/react-daterange-picker";
import "@wojtekmaj/react-daterange-picker/dist/DateRangePicker.css";
import "react-calendar/dist/Calendar.css";
import ChartContainer from "./components/ChartContainer";
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
    const [countryList, setCountryList] = useState<
        { id: number; name: string }[] | null
    >(null);

    const [startDate, setStartDate] = useState("2021-06-18"); //DEVTEMP 2020-01-03
    const [endDate, setEndDate] = useState("2021-08-27"); //DEVTEMP 2023-05-17
    const [selectedCountries, setSelectedCountries] = useState<number[]>([]);
    const allCountriesChecked =
        selectedCountries.length === countryList?.length ?? 0;
    const [selectedOptions, setSelectedOptions] = useState({
        vaccinations: false,
        newCases: true, //DEVTEMP
        deaths: false,
    });

    const [chartQuery, setChartQuery] = useState({});

    const [chartData, setChartData] = useState<ChartData<"line"> | null>(null);
    useEffect(() => {
        getCountriesList();
    }, []);

    function handleDateChange([startDate, endDate]: any) {
        setStartDate(dayjs(startDate).format("YYYY-MM-DD"));
        setEndDate(dayjs(endDate).format("YYYY-MM-DD"));
        //setChartQuery((prevQuery) => {});
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

    const handleGenerateData = async () => {
        try {
            const { vaccinations, newCases, deaths } = selectedOptions;

            const datasets: any[] = [];

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
            return checked
                ? [...prevSelectedCountries, id]
                : prevSelectedCountries.filter((countryId) => countryId !== id);
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

                // DEVTEMP
                if (name === "Poland") {
                    setSelectedCountries([id]);
                }
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
                            gap: "20px",
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
                        <DateRangePicker
                            onChange={handleDateChange}
                            value={[new Date(startDate), new Date(endDate)]}
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

                        <button onClick={handleGenerateData}>
                            Pokaż wykres
                        </button>
                    </div>
                    <ChartContainer data={chartData} />
                </main>
            </div>
            <ToastContainer position={toast.POSITION.BOTTOM_CENTER} />
        </>
    );
}

export default App;
