import { useEffect, useState } from "react";
import axiosClient from "../axiosClient";

export default function CountryCheckbox({
    country,
    checked,
    updateSelected,
    style,
}: {
    country: { id: number; name: string };
    checked: boolean;
    updateSelected: (updateData: { id: number; checked: boolean }) => void;
    style?: React.CSSProperties;
}) {
    const [flagURL, setFlagURL] = useState(null);

    async function getFlag() {
        try {
            const response = await axiosClient.get(
                `http://localhost:80/api/country/flag/${country.id}`
            );
            return response.data.data;
        } catch (e: any) {
            console.error(`NO FLAG IN  BCS: ${country.name}` + e);
        }
    }

    return (
        <div style={style}>
            <input
                type="checkbox"
                name={country.name}
                checked={checked}
                onChange={(e) => {
                    const { checked } = e.target;
                    updateSelected({ id: country.id, checked });

                    getFlag().then(setFlagURL);
                }}
            />
            <span>{country.name}</span>
            {flagURL && (
                <img
                    src={flagURL}
                    alt={`${country.name} flag`}
                    style={{ width: "30px", height: "20px" }}
                />
            )}
        </div>
    );
}
