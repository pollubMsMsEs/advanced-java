<?php

namespace App\Http\Controllers;

use App\Models\CasesPerDay;
use App\Models\Country;
use App\Models\Vaccinations;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;
use PhpParser\Error;

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

            $cases--;
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

            $vaccinations--;
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
        $filepath = storage_path() . "/imported";
        $filename = "data.json";
        $filefull = $filepath . "/" . $filename;
        if ($request->file("data") && $request->file("data")->isValid() && $request->file("data")->getMimeType() === "application/json") {
            $request->file("data")->move($filepath, $filename);
            $content = json_decode(file_get_contents($filefull), true);

            $transactionLvl = 2;
            try {
                $transactionLvl = DB::transactionLevel();
                DB::transaction(function () use ($content) {
                    if (($countries = CountryController::getCountriesCSV()) === false)
                        throw new \Exception("Couldn't open countries CSV", 333);

                    $currentCountryName = null;
                    $currentCountryId = null;
                    $insert_data = [];

                    CasesPerDay::query()->delete();

                    foreach ($content["cases"] as $data) {
                        $countryName = $data["country"];

                        if (($countryCode = array_key_exists($countryName, $countries) ? $countries[$countryName] : false) === false) {
                            continue;
                        }

                        if ($currentCountryName !== $countryName) {
                            $currentCountryName = $countryName;
                            $currentCountryId = Country::query()->firstOrCreate(["name" => $countryName, "alpha3_code" => $countryCode])["id"];
                        }

                        $insert_data[] = [
                            "day" => $data["day"],
                            "country_id" => $currentCountryId,
                            "newCases" => intval($data["new_cases"]),
                            "newDeaths" => intval($data["new_deaths"])
                        ];
                    }

                    //throw new Error("Rollback test");

                    $insert_data = collect($insert_data);

                    $CHUNK_SIZE = 1000;
                    $chunks = $insert_data->chunk($CHUNK_SIZE);

                    $count = 0;
                    foreach ($chunks as $chunk) {
                        CasesPerDay::insert($chunk->toArray());
                        $count += $CHUNK_SIZE;
                    }
                });
            } catch (\Exception $error) {
                return response()->json(["error" => true, "msg" => $error->getMessage()]);
            }


            return response()->json(["acknowledged" => true, "transactionLvl" => $transactionLvl]);
        } else {
            return response()->json(["error" => true, "msg" => "Incorrect file"]);
        }
    }
}