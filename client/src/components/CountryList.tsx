import React, { useEffect, useState } from "react";
import CountryCheckbox from "./CountryCheckbox";
import axiosClient from "../axiosClient";

interface CountryData {
    id: number;
    name: string;
}

export default function CountryList({
    selectedCountries,
    handleSelectedCountries,
    style,
}: {
    selectedCountries: number[];
    handleSelectedCountries: (countries: number[]) => void;
    style?: React.CSSProperties;
}) {
    const [countryList, setCountryList] = useState<
        { id: number; name: string }[] | null
    >(null);
    const allCountriesChecked =
        selectedCountries.length === countryList?.length ?? 0;

    useEffect(() => {
        async function getCountriesList() {
            try {
                const countriesResponse = await axiosClient.get("/countries");
                console.log(countriesResponse);
                const countriesObj: { [name: string]: number } =
                    countriesResponse.data.data;

                const countries: CountryData[] | undefined = [];

                for (const [name, id] of Object.entries(countriesObj)) {
                    countries.push({ id: id, name });

                    // DEVTEMP
                    if (name === "Poland") {
                        handleSelectedCountries([id]);
                    }
                }

                if (countries?.length === 0) throw Error("Empty countries");
                setCountryList(countries);
            } catch (error) {
                console.error(error);
                setCountryList(null);
            }
        }

        getCountriesList();
    }, [handleSelectedCountries]);

    const handleAllCountriesCheckboxChange = ({
        checked,
    }: {
        checked: boolean;
    }) => {
        if (checked) {
            handleSelectedCountries(countryList?.map((c) => c.id) ?? []);
        } else {
            handleSelectedCountries([]);
        }
    };

    const handleCountryCheckboxChange = ({
        id,
        checked,
    }: {
        id: number;
        checked: boolean;
    }) => {
        handleSelectedCountries(
            checked
                ? [...selectedCountries, id]
                : selectedCountries.filter((countryId) => countryId !== id)
        );
    };

    return (
        <div style={style}>
            {countryList && (
                <CountryCheckbox
                    country={{
                        id: -1,
                        name: "Whole world",
                    }}
                    updateSelected={handleAllCountriesCheckboxChange}
                    style={{ fontWeight: "bold" }}
                    checked={allCountriesChecked}
                />
            )}
            {countryList?.map((country) => (
                <CountryCheckbox
                    key={country.id}
                    country={country}
                    updateSelected={handleCountryCheckboxChange}
                    style={{ display: "flex", gap: "5px" }}
                    checked={selectedCountries.some((v) => v === country.id)}
                />
            )) ?? "Couldn't load countries"}
        </div>
    );
}
