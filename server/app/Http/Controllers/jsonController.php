<?php

namespace App\Http\Controllers;

use App\Models\CasesPerDay;
use App\Models\Vaccinations;
use Illuminate\Http\Request;

class jsonController extends Controller
{
    public function export()
    {
        $filename = storage_path() . "/exported/data.json";
        $cases = 1000;
        $vaccinations = 1000;


        if (file_exists($filename)) {
            unlink($filename);
        }

        $data = ["cases" => [], "vaccinations" => []];

        foreach (CasesPerDay::with('country')->lazy(200) as $case) {
            $temp = [
                "day" => $case->day,
                "country" => $case->country->name,
                "new_cases" => $case->newCases,
                "new_deaths" => $case->newDeaths,
            ];

            array_push($data["cases"], $temp);

            $cases -= 200;
            if ($cases <= 0) {
                break;
            }
        }


        foreach (Vaccinations::with('country')->with('vaccineManufacturer')->lazy(200) as $vaccination) {
            $temp = [
                "day" => $vaccination->day,
                "country" => $vaccination->country->name,
                "vaccine_manufacturer" => $vaccination->vaccineManufacturer->name,
                "total" => $vaccination->total,
            ];

            array_push($data["vaccinations"], $temp);

            $vaccinations -= 200;
            if ($vaccinations <= 0) {
                break;
            }
        }

        file_put_contents($filename, json_encode($data), FILE_APPEND | LOCK_EX);
        return response()->download(
            $filename,
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