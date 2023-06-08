import { ChartData, ChartOptions } from "chart.js";
import { useEffect, useRef, useState } from "react";
import { Line } from "react-chartjs-2";

export default function ChartContainer({
    data,
}: {
    data: ChartData<"line"> | null;
}) {
    const chartRef: any /*React.RefObject<Chart> | null | undefined*/ =
        useRef(null);
    const [options, setOptions] = useState<ChartOptions<"line"> | undefined>();

    useEffect(() => {
        createChartOptions();
    }, [data]);

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

    return (
        <div
            className="chart-container"
            style={{
                display: "grid",
                position: "relative", //@Skic Required for charts to scale properly
                width: "95%",
            }}
        >
            {data ? (
                <Line ref={chartRef} data={data} options={options} />
            ) : (
                <p style={{ placeSelf: "center" }}>
                    Edit data to generate chart
                </p>
            )}
        </div>
    );
}
