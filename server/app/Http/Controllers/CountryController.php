<?php

namespace App\Http\Controllers;

use App\Models\Country;
use App\Http\Requests\StoreCountryRequest;
use App\Http\Requests\UpdateCountryRequest;

class CountryController extends Controller
{
    public static function getCountriesCSV()
    {
        $countries = [];

        if (($open = fopen(storage_path() . "/importData/locations.csv", "r")) !== false) {
            while (($data = fgetcsv($open, 1000, ",")) !== false) {
                $countries[$data[0]] = $data[1];
            }

            fclose($open);
        }

        unset($countries["location"]);

        return $countries;
    }

    /**
     * Display a listing of the resource.
     */
    public function index()
    {

        return response()->json(CountryController::getCountriesCSV());
    }

    /**
     * Show the form for creating a new resource.
     */
    public function create()
    {
        //
    }

    /**
     * Store a newly created resource in storage.
     */
    public function store(StoreCountryRequest $request)
    {
        //
    }

    /**
     * Display the specified resource.
     */
    public function show(Country $country)
    {
        //
    }

    /**
     * Show the form for editing the specified resource.
     */
    public function edit(Country $country)
    {
        //
    }

    /**
     * Update the specified resource in storage.
     */
    public function update(UpdateCountryRequest $request, Country $country)
    {
        //
    }

    /**
     * Remove the specified resource from storage.
     */
    public function destroy(Country $country)
    {
        //
    }
}