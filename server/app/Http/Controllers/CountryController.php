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

        try {
            if (($open = fopen(storage_path() . "/importData/locations.csv", "r")) !== false) {
                while (($data = fgetcsv($open, 1000, ",")) !== false) {
                    $countries[$data[0]] = $data[1];
                }
                unset($countries["location"]);

                fclose($open);

            } else {
                $countries = false;
            }
        } catch (\Exception $e) {
            $countries = false;
        }


        return $countries;
    }

    /**
     * Display a listing of the resource.
     */
    public function index()
    {
        try {
            $countries = Country::query()->pluck("id", "name");
            return response()->json(["data" => $countries]);
        } catch (\Exception $e) {
            return response()->json(["error" => true, "msg" => "Couldn't get countries"]);
        }
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