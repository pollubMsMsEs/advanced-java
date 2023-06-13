import { toast } from "react-toastify";
import axiosClient from "./axiosClient";

export async function sendImportExportRequest(
    operation: "Import" | "Export",
    url: string
) {
    const toastId = toast.loading(`${operation}ing...`);

    try {
        let result;

        switch (operation) {
            case "Import":
                result = await axiosClient.put(url);
                break;
            case "Export":
                result = await axiosClient.get(url, { responseType: "blob" });
                break;
        }

        console.log(result);

        if (result.data.error) {
            throw new Error(result.data.msg);
        }

        const aElement = document.createElement("a");
        aElement.setAttribute("download", `data.${"json"}`);
        const href = URL.createObjectURL(result.data);
        aElement.href = href;
        aElement.setAttribute("target", "_blank");
        aElement.click();
        URL.revokeObjectURL(href);

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
