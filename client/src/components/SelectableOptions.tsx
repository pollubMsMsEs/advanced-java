import { ChangeEvent } from "react";
import { SelectableOptions } from "../types";

export default function SelectableOptions({
    selectedOptions,
    handleOptionsUpdate,
}: {
    selectedOptions: SelectableOptions;
    handleOptionsUpdate: ({
        name,
        checked,
    }: {
        name: string;
        checked: boolean;
    }) => void;
}) {
    const handleOptionCheckboxChange = (
        event: ChangeEvent<HTMLInputElement>
    ) => {
        handleOptionsUpdate(event.target);
    };

    return (
        <div>
            <h3 style={{ margin: "0" }}>COVID-19 data</h3>
            <div>
                <input
                    type="checkbox"
                    name="vaccinations"
                    id="vaccinations"
                    onChange={handleOptionCheckboxChange}
                    checked={selectedOptions.vaccinations}
                />
                <span>Vaccinations</span>
            </div>
            <div>
                <input
                    type="checkbox"
                    name="newCases"
                    id="newCases"
                    onChange={handleOptionCheckboxChange}
                    checked={selectedOptions.newCases}
                />
                <span>New Cases</span>
            </div>
            <div>
                <input
                    type="checkbox"
                    name="deaths"
                    id="deaths"
                    onChange={handleOptionCheckboxChange}
                    checked={selectedOptions.deaths}
                />
                <span>Deaths</span>
            </div>
        </div>
    );
}
