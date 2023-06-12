<?php

namespace App\Http\Controllers;

use App\Models\CasesPerDay;
use App\Models\Vaccinations;
use Illuminate\Http\Request;

class jsonController extends Controller
{
    public function export()
    {
        unlink(storage_path() . "/exported/data.json");
        $data = ["cases" => [], "vaccinations" => []];

        foreach (CasesPerDay::with('country')->lazy(200) as $case) {
            $temp = [
                "day" => $case->day,
                "country" => $case->country->name,
                "new_cases" => $case->newCases,
                "new_deaths" => $case->newDeaths,
            ];

            array_push($data["cases"], $temp);
        }

        foreach (Vaccinations::with('country')->with('vaccineManufacturer')->lazy(200) as $vaccination) {
            $temp = [
                "day" => $vaccination->day,
                "country" => $vaccination->country->name,
                "vaccine_manufacturer" => $vaccination->vaccineManufacturer->name,
                "total" => $vaccination->total,
            ];

            array_push($data["vaccinations"], $temp);
        }

        file_put_contents(storage_path() . "/exported/data.json", json_encode($data), FILE_APPEND | LOCK_EX);
        return response()->download(
            storage_path() . "/exported/data.json",
            'data.json',
            array(
                'Content-Type: application/json',
            )
        );
    }

    public function import()
    {
        $data = ["cases" => [], "vaccinations" => []];
        $temp = ["new_cases" => 0];
        array_push($data["cases"], $temp);
        array_push($data["cases"], "da");
        array_push($data["cases"], "da");

        array_push($data["cases"], "da");
        array_push($data["cases"], "da");


        return response()->json($data);
    }
}