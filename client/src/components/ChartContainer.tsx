import { ChartData, ChartOptions } from "chart.js";
import { useEffect, useRef, useState } from "react";
import { Line } from "react-chartjs-2";
import { ChartQuery, SelectableOptions } from "../types";
import axiosClient from "../axiosClient";

export default function ChartContainer({
    query,
    selectedOptions,
}: {
    query: ChartQuery | null;
    selectedOptions: SelectableOptions;
}) {
    const chartRef: any /*React.RefObject<Chart> | null | undefined*/ =
        useRef(null);
    const [options, setOptions] = useState<ChartOptions<"line"> | undefined>();
    const [chartData, setChartData] = useState<ChartData<"line"> | null>(null);

    useEffect(() => {
        createChartOptions();
    }, [chartData]);

    function createChartOptions() {
        const ctx: CanvasRenderingContext2D | undefined =
            chartRef?.current?.ctx;
        if (ctx == null) return;

        const gradient = ctx.createLinearGradient(
            0,
            0,
            0,
            chartRef?.current?.height ?? 200
        );
        gradient.addColorStop(0, "#ff0000D0");
        gradient.addColorStop(0.9, "#f0f257");
        gradient.addColorStop(1, "#f0f257");
        setOptions({
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

    const chartTimeout: any = useRef();

    useEffect(() => {
        let ignore = false;

        const handleGenerateData = async () => {
            try {
                const { vaccinations, newCases, deaths } = selectedOptions;

                const datasets: any[] = [];

                const [responseDeaths, responseNewCases, responseVaccinations] =
                    await Promise.all([
                        deaths
                            ? axiosClient.get("/deaths", {
                                  params: query,
                                  paramsSerializer: { indexes: null },
                              })
                            : undefined,
                        newCases
                            ? axiosClient.get("/cases", {
                                  params: query,
                                  paramsSerializer: { indexes: null },
                              })
                            : undefined,
                        vaccinations
                            ? axiosClient.get("/vaccinations", {
                                  params: query,
                                  paramsSerializer: { indexes: null },
                              })
                            : undefined,
                    ]);

                if (ignore) return;

                if (responseDeaths) {
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

                if (responseNewCases) {
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

                if (responseVaccinations) {
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

                setChartData(data);
            } catch (error) {
                console.error(error);
                setChartData(null);
            }
        };

        chartTimeout.current = setTimeout(() => {
            handleGenerateData();
        }, 500);

        return () => {
            clearTimeout(chartTimeout.current);
            ignore = true;
        };
    }, [query, selectedOptions]);

    return (
        <div
            className="chart-container"
            style={{
                display: "grid",
                position: "relative", //@Skic Required for charts to scale properly
                width: "95%",
            }}
        >
            {chartData ? (
                <Line ref={chartRef} data={chartData} options={options} />
            ) : (
                <p style={{ placeSelf: "center" }}>
                    Edit data to generate chart
                </p>
            )}
        </div>
    );
}
