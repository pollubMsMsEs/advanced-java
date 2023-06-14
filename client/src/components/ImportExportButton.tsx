import { useRef } from "react";
import { sendImportExportRequest } from "../importExportFunction";

export default function ImportButton({
    isLocked,
    doLock,
    type,
    target,
    url,
    withFile,
}: {
    isLocked: boolean;
    doLock: (lock: boolean) => void;
    type: "Import" | "Export";
    target: string;
    url: string;
    withFile: boolean;
}) {
    const fileRef = useRef(null);

    async function doOperation() {
        doLock(true);

        if (withFile) {
            console.log(fileRef);
            await sendImportExportRequest(
                type,
                url,
                target.toLowerCase(),
                fileRef
            );
        } else {
            await sendImportExportRequest(type, url, target.toLowerCase());
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
            {withFile && <input ref={fileRef} type="file" name="data" />}
        </>
    );
}
