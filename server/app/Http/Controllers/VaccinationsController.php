<?php

namespace App\Http\Controllers;

use App\Models\Country;
use App\Models\Vaccinations;
use App\Models\VaccineManufacturer;
use Illuminate\Http\Request;

class VaccinationsController extends Controller
{
    public function importVaccinationsCSV()
    {
        try {
            if (($countries = CountryController::getCountriesCSV()) === false)
                throw new \Exception("Couldn't open countries CSV", 333);
            if (($open = fopen(storage_path() . "/importData/vaccinations-by-manufacturer.csv", "r")) === false)
                throw new \Exception("Couldn't open countries CSV", 333);

            $currentCountryName = null;
            $currentCountryId = null;
            $manufacturerIds = [];
            $insert_data = [];

            Vaccinations::query()->delete();

            while (($data = fgetcsv($open, 200, ",")) !== false) {
                if ($data[0] === "location") {
                    continue;
                }

                $countryName = $data[0];

                if (($countryCode = array_key_exists($countryName, $countries) ? $countries[$countryName] : false) === false) {
                    continue;
                }

                if ($currentCountryName !== $countryName) {
                    $currentCountryName = $countryName;
                    $currentCountryId = Country::query()->firstOrCreate(["name" => $countryName, "alpha3_code" => $countryCode])["id"];
                }

                $manufacturerName = $data[2];
                if (!array_key_exists($manufacturerName, $manufacturerIds)) {
                    $manufacturerIds[$manufacturerName] = VaccineManufacturer::query()->firstOrCreate(["name" => $manufacturerName])["id"];

                }

                $insert_data[] = [
                    "day" => $data[1],
                    "country_id" => $currentCountryId,
                    "vaccine_manufacturer_id" => $manufacturerIds[$manufacturerName],
                    "total" => $data[3],
                ];
            }

            $insert_data = collect($insert_data);

            $CHUNK_SIZE = 1000;
            $chunks = $insert_data->chunk($CHUNK_SIZE);

            $count = 0;
            foreach ($chunks as $chunk) {
                Vaccinations::insert($chunk->toArray());
                $count += $CHUNK_SIZE;
            }

            fclose($open);

            return response()->json(["acknowledged" => true]);

        } catch (\Exception $error) {
            return response()->json(["error" => true, "msg" => $error->getMessage(), "user_friendly_msg" => $error->getCode() === 333]);
        }
    }

}