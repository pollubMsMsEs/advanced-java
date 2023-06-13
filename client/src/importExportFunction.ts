import { toast } from "react-toastify";
import axiosClient from "./axiosClient";

export async function sendImportExportRequest(
    operation: "Import" | "Export",
    url: string
) {
    const toastId = toast.loading(`${operation}ing...`);

    try {
        const result =
            operation === "Import"
                ? await axiosClient.put(url)
                : await axiosClient.get(url, { responseType: "blob" });
        console.log(result);

        if (result.data.error) {
            throw new Error(result.data.msg);
        }

        toast.update(toastId, {
            render: `${operation} succedded!`,
            type: "success",
            isLoading: false,
            autoClose: 4000,
        });
    } catch (error: any) {
        toast.update(toastId, {
            render: `${operation} failed: ${error.message ?? ""}`,
            type: "error",
            isLoading: false,
            autoClose: 4000,
        });
    }
}
