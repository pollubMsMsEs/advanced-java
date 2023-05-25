<?php

namespace App\Http\Controllers;

use App\Models\Country;
use Illuminate\Http\Request;

class CasesPerDayController extends Controller
{
    public function importCasesCSV()
    {
        $countries = CountryController::getCountriesCSV();

        $testCountry = "Poland";

        try {
            Country::create(["name" => $testCountry, "alpha3_code" => $countries[$testCountry]]);

            echo "git";
        } catch (\Exception $e) {
            echo "not git: $e";
        }

    }
}