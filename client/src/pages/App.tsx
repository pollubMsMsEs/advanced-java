import { useState, useMemo, useEffect } from "react";
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
} from "chart.js";
import ImportBar from "../components/ImportBar";
import DateRangePicker from "@wojtekmaj/react-daterange-picker";
import "@wojtekmaj/react-daterange-picker/dist/DateRangePicker.css";
import "react-calendar/dist/Calendar.css";
import ChartContainer from "../components/ChartContainer";
import CountryList from "../components/CountryList";
import {
    ChartQuery,
    SelectableOptions as SelectableOptionsType,
} from "../types";
import SelectableOptions from "../components/SelectableOptions";
import { useAuthenticationContext } from "../stateContext";
import axiosClient from "../axiosClient";
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
    const [chartQuery, setChartQuery] = useState<ChartQuery>({
        begin_date: "2021-06-18",
        end_date: "2021-08-27",
        countries: [],
    });

    const [selectedOptions, setSelectedOptions] =
        useState<SelectableOptionsType>({
            vaccinations: false,
            newCases: true, //DEVTEMP
            deaths: false,
        });

    const context = useAuthenticationContext();

    function handleDateChange([startDate, endDate]: any) {
        setChartQuery((prevQuery) => {
            return {
                ...prevQuery,
                begin_date: dayjs(startDate).format("YYYY-MM-DD"),
                end_date: dayjs(endDate).format("YYYY-MM-DD"),
            };
        });
    }

    function handleOptionsChange({
        name,
        checked,
    }: {
        name: string;
        checked: boolean;
    }) {
        setSelectedOptions((prevOptions) => ({
            ...prevOptions,
            [name]: checked,
        }));
    }

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

    useEffect(() => {
        document.title = "Covid Visualizer";
    }, []);

    return (
        <>
            <div
                style={{
                    display: "grid",
                    maxHeight: "100vh",
                    gridTemplateColumns: "250px 1fr",
                }}
            >
                {context.user?.role && context.user.role === "admin" ? (
                    <ImportBar />
                ) : (
                    <div></div>
                )}
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
                        <SelectableOptions
                            selectedOptions={selectedOptions}
                            handleOptionsUpdate={handleOptionsChange}
                        />
                    </div>
                    <ChartContainer
                        query={chartQuery}
                        selectedOptions={selectedOptions}
                    />
                </main>
                <button
                    onClick={async () => {
                        await axiosClient.post("/logout");
                        context.setToken(null);
                        context.setUser({});
                    }}
                >
                    Logout
                </button>
            </div>
            <ToastContainer position={toast.POSITION.BOTTOM_CENTER} />
        </>
    );
}

export default App;
