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
import { useAuthenticationContext } from "../StateContext.js";
import axiosClient from "../axiosClient";
import Logo from "../components/Logo";
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

    const [isTabOpen, setIsTabOpen] = useState(false);

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
                    gridTemplateRows: "80px minmax(0,1fr)", // @Skic minmax is necessary for maxHeight to work in grid cells
                }}
            >
                <header
                    style={{
                        gridColumn: "1 / -1",
                        gridRow: "1 / 2",
                        display: "grid",
                        gridAutoFlow: "column",
                        gridAutoColumns: "minmax(0,1fr)",
                        alignItems: "center",
                        gap: "100px",
                        padding: "0 20px",
                        borderBottom: "1px solid black",
                    }}
                >
                    <Logo />
                    {context.user?.role && context.user.role === "ADMIN" && (
                        <div
                            style={{
                                position: "relative",
                                display: "flex",
                                flexDirection: "column",
                                alignItems: "center",
                            }}
                        >
                            <span
                                style={{
                                    fontWeight: "bold",
                                    height: "50%",
                                    display: "flex",
                                    alignItems: "center",
                                    borderBottom: "2px solid black",
                                }}
                                onClick={() => {
                                    setIsTabOpen((t) => !t);
                                }}
                            >
                                Imports
                            </span>
                        </div>
                    )}
                    <div
                        style={{
                            display: "flex",
                            alignItems: "center",
                            gap: "20px",
                            justifySelf: "end",
                        }}
                    >
                        <div>{context.user.name}</div>
                        <button
                            style={{
                                color: "aliceblue",
                                backgroundColor: "#0284c7",
                                border: "none",
                                padding: "12px 30px",
                                borderRadius: "5px",
                                justifySelf: "end",
                            }}
                            onClick={async () => {
                                //await axiosClient.post("/logout");
                                context.setToken(null);
                                context.setUser({});
                            }}
                        >
                            Logout
                        </button>
                    </div>
                </header>
                <main
                    style={{
                        padding: "10px",
                        display: "grid",
                        gridColumn: "1 / -1",
                        gridTemplateColumns: "max-content 1fr",
                        gridTemplateRows: "minmax(0,1fr) 100px", // @Skic minmax is necessary for maxHeight to work in grid cells
                        boxSizing: "border-box", // @Skic For padding to not create overflow
                        maxHeight: "100vh",
                    }}
                >
                    <div
                        style={{
                            position: "absolute",
                            right: "10px",
                            left: "10px",
                            marginTop: "-11px",
                            display: "grid",
                            placeItems: "center",
                        }}
                    >
                        <div
                            style={{
                                backgroundColor: "white",
                                boxShadow: "0px 8px 24px -20px black",
                                zIndex: "2",
                                display: "flex",
                                justifyContent: "space-evenly",
                                width: "min(80%,1000px)",
                                height: isTabOpen ? "150px" : "0",
                                padding: isTabOpen ? "10px 0" : "0",
                                border: "1px solid black",
                                borderTop: "none",
                                transition: "all 1s",
                                overflow: "hidden",
                            }}
                        >
                            <ImportBar />
                        </div>
                    </div>
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
                            style={{
                                overflowY: "auto",
                                flexShrink: 1,
                                display: "flex",
                                flexDirection: "column",
                                gap: "1px",
                            }}
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
            </div>
            <ToastContainer position={toast.POSITION.BOTTOM_CENTER} />
        </>
    );
}

export default App;
