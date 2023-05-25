<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;

class CasesPerDayController extends Controller
{
    public function importCasesCSV()
    {
        $countries = CountryController::getCountriesCSV();


    }
}