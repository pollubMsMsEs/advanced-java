import { useState } from "react";
import { toast } from "react-toastify";
import axiosClient from "../axiosClient";

async function sendImportRequest(url: string) {
    const toastId = toast.loading("Importing...");

    try {
        const result = await axiosClient.put(url);
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

export default function ImportBar() {
    const [isImportingVaccinations, setIsImportingVaccinations] =
        useState(false);
    const [isImportingCases, setIsImportingCases] = useState(false);

    async function importCases() {
        setIsImportingCases(true);

        await sendImportRequest("/import/cases");

        setIsImportingCases(false);
    }

    async function importVaccinations() {
        setIsImportingVaccinations(true);

        await sendImportRequest("/import/vaccinations");

        setIsImportingVaccinations(false);
    }

    return (
        <>
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
        </>
    );
}