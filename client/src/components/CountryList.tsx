import React, { useEffect, useState } from "react";
import CountryCheckbox from "./CountryCheckbox";
import axios from "axios";

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
        getCountriesList();
    }, []); // eslint-disable-line react-hooks/exhaustive-deps

    async function getCountriesList() {
        try {
            const countriesResponse = await axios.get(
                "http://localhost:80/api/countries"
            );
            const countriesObj: { [name: string]: number } =
                countriesResponse.data.data;

            const countries: CountryData[] = [];
            for (const [name, id] of Object.entries(countriesObj)) {
                countries.push({ id: id, name });

                // DEVTEMP
                if (name === "Poland") {
                    handleSelectedCountries([id]);
                }
            }
            setCountryList(countries);
        } catch (error) {
            console.error(error);
            setCountryList(null);
        }
    }

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
                    checked={selectedCountries.some((v) => v === country.id)}
                />
            )) ?? "Couldn't load countries"}
        </div>
    );
}
