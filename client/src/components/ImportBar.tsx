import { useState } from "react";
import ImportExportButton from "./ImportExportButton";
import { ChartQuery } from "../types";

export default function ImportBar({
    query,
    onImport,
}: {
    query: ChartQuery | null;
    onImport: () => void;
}) {
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
                    onSuccess={onImport}
                    type="Import"
                    target="Cases"
                    url="/import/cases"
                    withFile={false}
                />
                <ImportExportButton
                    isLocked={isLocked}
                    doLock={doLock}
                    onSuccess={onImport}
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
                    onSuccess={() => undefined}
                    type="Export"
                    target="JSON"
                    query={query}
                    url="/export/json"
                    withFile={false}
                />
                <ImportExportButton
                    isLocked={isLocked}
                    doLock={doLock}
                    onSuccess={onImport}
                    type="Import"
                    acceptedTypes="application/json"
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
                <ImportExportButton
                    isLocked={isLocked}
                    doLock={doLock}
                    onSuccess={() => undefined}
                    type="Export"
                    target="XML"
                    query={query}
                    url="/export/xml"
                    withFile={false}
                />
                <ImportExportButton
                    isLocked={isLocked}
                    doLock={doLock}
                    onSuccess={onImport}
                    type="Import"
                    acceptedTypes="application/xml,text/xml"
                    target="XML"
                    url="/import/xml"
                    withFile={true}
                />
            </div>
        </>
    );
}
