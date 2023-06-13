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
        );
    }

    public function import(Request $request)
    {
        if ($request->file("data") && $request->file("data")->isValid() && $request->file("data")->getMimeType() === "application/json") {

            return response()->json(["acknowledged" => true]);
        } else {
            return response()->json(["error" => true, "msg" => "Incorrect file"]);
        }
    }
}