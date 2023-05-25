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

            $count = 0;

            CasesPerDay::query()->delete();
            echo "<pre>";
            while (($data = fgetcsv($open, 100, ",")) !== false && $count < 300) {
                if ($data[0] === "date")
                    continue;

                $countryName = $data[1];
                $countryCode = array_key_exists($countryName, $countries) ? $countries[$countryName] : false;
                if ($countryCode === false)
                    continue;

                DB::transaction(function () use ($data, $countryName, $countryCode) {

                    $country = Country::query()->firstOrCreate(["name" => $countryName, "alpha3_code" => $countryCode]);
                    CasesPerDay::query()->create(
                        [
                            "day" => $data[0],
                            "country_id" => $country["id"],
                            "newCases" => intval($data[2]),
                            "newDeaths" => intval($data[3])
                        ]
                    );
                });

                $count++;
            }

            fclose($open);

            echo "Finished importing";
        }
    }
}