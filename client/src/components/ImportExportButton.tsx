import { useRef } from "react";
import { sendImportExportRequest } from "../importExportFunction";

export default function ImportButton({
    isLocked,
    doLock,
    onSuccess,
    type,
    target,
    acceptedTypes,
    url,
    query,
    withFile,
}: {
    isLocked: boolean;
    doLock: (lock: boolean) => void;
    onSuccess: () => void;
    type: "Import" | "Export";
    target: string;
    acceptedTypes?: string;
    url: string;
    query?: any;
    withFile: boolean;
}) {
    const fileRef = useRef(null);

    async function doOperation() {
        doLock(true);

        if (withFile) {
            await sendImportExportRequest(
                type,
                url,
                target.toLowerCase(),
                query,
                onSuccess,
                fileRef
            );
        } else {
            await sendImportExportRequest(
                type,
                url,
                target.toLowerCase(),
                query,
                onSuccess
            );
        }

        doLock(false);
    }

    return (
        <>
            <button
                onClick={doOperation}
                disabled={isLocked}
                className="button"
                style={{
                    color: "aliceblue",
                    backgroundColor: isLocked ? "#a1a1aa" : "#0284c7",
                    border: "none",
                    padding: "10px",
                    borderRadius: "5px",
                }}
            >
                {`${type} ${target}`}
            </button>
            {withFile && (
                <input
                    ref={fileRef}
                    type="file"
                    name="data"
                    accept={acceptedTypes}
                />
            )}
        </>
    );
}
