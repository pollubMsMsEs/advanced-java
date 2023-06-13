import { sendImportExportRequest } from "../importExportFunction";

export default function ImportButton({
    isLocked,
    doLock,
    type,
    target,
    url,
}: {
    isLocked: boolean;
    doLock: (lock: boolean) => void;
    type: "Import" | "Export";
    target: string;
    url: string;
}) {
    async function doOperation() {
        doLock(true);

        await sendImportExportRequest(type, url);

        doLock(false);
    }

    return (
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
    );
}
