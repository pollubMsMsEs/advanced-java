<?php

namespace App\Http\Controllers;

use App\Models\CasesPerDay;
use App\Models\Country;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;

class CasesPerDayController extends Controller
{
    public function importCasesCSV()
    {
        $countries = CountryController::getCountriesCSV();

        if (($open = fopen(storage_path() . "/importData/casesAndDeaths.csv", "r")) !== false && $countries !== false) {

            $currentCountryName = null;
            $currentCountryId = null;
            $insert_data = [];

            CasesPerDay::query()->delete();

            while (($data = fgetcsv($open, 200, ",")) !== false) {
                if ($data[0] === "date")
                    continue;

                $countryName = $data[1];

                if (($countryCode = array_key_exists($countryName, $countries) ? $countries[$countryName] : false) === false) {
                    continue;
                }

                if ($currentCountryName !== $countryName) {
                    $currentCountryName = $countryName;
                    $currentCountryId = Country::query()->firstOrCreate(["name" => $countryName, "alpha3_code" => $countryCode])["id"];
                }

                $insert_data[] = [
                    "day" => $data[0],
                    "country_id" => $currentCountryId,
                    "newCases" => intval($data[2]),
                    "newDeaths" => intval($data[3])
                ];
            }

            $insert_data = collect($insert_data);

            $CHUNK_SIZE = 1000;
            $chunks = $insert_data->chunk($CHUNK_SIZE);

            $count = 0;
            foreach ($chunks as $chunk) {
                CasesPerDay::insert($chunk->toArray());
                $count += $CHUNK_SIZE;
            }

            fclose($open);

            echo "Finished importing";
        }
    }
}