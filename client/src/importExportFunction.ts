import { toast } from "react-toastify";
import axiosClient from "./axiosClient";

export async function sendImportExportRequest(
    operation: "Import" | "Export",
    url: string,
    extension: string,
    fileRef?: any
) {
    const toastId = toast.loading(`${operation}ing...`);
    //console.log(fileRef.current.files);

    try {
        let result;

        switch (operation) {
            case "Import":
                if (fileRef) {
                    const formData = new FormData();

                    formData.append("data", fileRef.current.files[0]);
                    result = await axiosClient.post(url, formData, {
                        headers: {
                            "Content-Type": "multipart/form-data",
                        },
                    });
                } else {
                    result = await axiosClient.put(url);
                }

                break;
            case "Export":
                result = await axiosClient.get(url, { responseType: "blob" });
                // eslint-disable-next-line no-case-declarations
                const aElement = document.createElement("a");
                aElement.setAttribute("download", `data.${extension}`);
                // eslint-disable-next-line no-case-declarations
                const href = URL.createObjectURL(result.data);
                aElement.href = href;
                aElement.setAttribute("target", "_blank");
                aElement.click();
                URL.revokeObjectURL(href);
                break;
        }

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
        console.error(error);
        toast.update(toastId, {
            render: `${operation} failed: ${error.message ?? ""}`,
            type: "error",
            isLoading: false,
            autoClose: 4000,
        });
    }
}