import { useState } from "react";
import ImportExportButton from "./ImportExportButton";

export default function ImportBar() {
    const [isLocked, setIsLocked] = useState(false);

    function doLock(lock: boolean) {
        setIsLocked(lock);
    }

    return (
        <>
            <div
                className="buttons"
                style={{
                    display: "flex",
                    flexDirection: "column",
                    alignItems: "stretch",
                    gap: "10px",
                }}
            >
                <h3 style={{ margin: "0", textAlign: "center" }}>CSV</h3>
                <ImportExportButton
                    isLocked={isLocked}
                    doLock={doLock}
                    type="Import"
                    target="Cases"
                    url="/import/cases"
                    withFile={false}
                />
                <ImportExportButton
                    isLocked={isLocked}
                    doLock={doLock}
                    type="Import"
                    target="Vaccinations"
                    url="/import/vaccinations"
                    withFile={false}
                />
            </div>
            <div
                className="buttons"
                style={{
                    display: "flex",
                    flexDirection: "column",
                    alignItems: "stretch",
                    gap: "10px",
                }}
            >
                <h3 style={{ margin: "0", textAlign: "center" }}>JSON</h3>
                <ImportExportButton
                    isLocked={isLocked}
                    doLock={doLock}
                    type="Export"
                    target="JSON"
                    url="/export/json"
                    withFile={false}
                />
                <ImportExportButton
                    isLocked={isLocked}
                    doLock={doLock}
                    type="Import"
                    target="JSON"
                    url="/import/json"
                    withFile={true}
                />
            </div>
            <div
                className="buttons"
                style={{
                    display: "flex",
                    flexDirection: "column",
                    alignItems: "stretch",
                    gap: "10px",
                }}
            >
                <h3 style={{ margin: "0", textAlign: "center" }}>XML</h3>
            </div>
        </>
    );
}
