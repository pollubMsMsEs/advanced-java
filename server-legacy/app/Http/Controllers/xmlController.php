<?php

namespace App\Http\Controllers;

use App\Models\CasesPerDay;
use App\Models\Country;
use App\Models\Vaccinations;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;

class xmlController extends Controller
{
    public function export()
    {
        $filename = storage_path() . "/exported/data.xml";
        $cases = 1000;
        $vaccinations = 1000;

        if (file_exists($filename)) {
            unlink($filename);
        }

        $xw = xmlwriter_open_memory();
        xmlwriter_set_indent($xw, 1);
        $res = xmlwriter_set_indent_string($xw, ' ');

        xmlwriter_start_document($xw, '1.0', 'UTF-8');
        xmlwriter_start_element($xw, 'covid_visualizer');

        foreach (CasesPerDay::with('country')->lazy(200) as $case) {
            xmlwriter_start_element($xw, 'cases');

            xmlwriter_start_element($xw, 'day');
            xmlwriter_text($xw, $case->day);
            xmlwriter_end_element($xw);

            xmlwriter_start_element($xw, 'country');
            xmlwriter_text($xw, $case->country->name);
            xmlwriter_end_element($xw);

            xmlwriter_start_element($xw, 'new_cases');
            xmlwriter_text($xw, $case->newCases);
            xmlwriter_end_element($xw);

            xmlwriter_start_element($xw, 'new_deaths');
            xmlwriter_text($xw, $case->newDeaths);
            xmlwriter_end_element($xw);

            xmlwriter_end_element($xw);

            $cases--;
            if ($cases <= 0) {
                break;
            }
        }

        foreach (Vaccinations::with('country')->with('vaccineManufacturer')->lazy(200) as $vaccination) {
            xmlwriter_start_element($xw, 'vaccinations');

            xmlwriter_start_element($xw, 'day');
            xmlwriter_text($xw, $vaccination->day);
            xmlwriter_end_element($xw);

            xmlwriter_start_element($xw, 'country');
            xmlwriter_text($xw, $vaccination->country->name);
            xmlwriter_end_element($xw);

            xmlwriter_start_element($xw, 'vaccine_manufacturer');
            xmlwriter_text($xw, $vaccination->vaccineManufacturer->name);
            xmlwriter_end_element($xw);

            xmlwriter_start_element($xw, 'total');
            xmlwriter_text($xw, $vaccination->total);
            xmlwriter_end_element($xw);

            xmlwriter_end_element($xw);

            $vaccinations--;
            if ($vaccinations <= 0) {
                break;
            }
        }

        xmlwriter_end_element($xw);
        xmlwriter_end_document($xw);

        file_put_contents($filename, xmlwriter_output_memory($xw), LOCK_EX);
        return response()->download(
            $filename,
        );
    }

    public function import(Request $request)
    {
        $filepath = storage_path() . "/imported";
        $filename = "data.xml";
        $filefull = $filepath . "/" . $filename;

        if ($request->file("data") && $request->file("data")->isValid() && $request->file("data")->getMimeType() === "text/xml") {
            $request->file("data")->move($filepath, $filename);
            $content = json_decode(json_encode(simplexml_load_file($filefull)), true);

            $transactionLvl = 2;
            try {
                DB::statement('SET SESSION TRANSACTION ISOLATION LEVEL SERIALIZABLE');
                $transactionLvl = DB::select("SHOW VARIABLES LIKE 'transaction_isolation'");
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
            } finally {
                DB::statement('SET SESSION TRANSACTION ISOLATION LEVEL REPEATABLE READ');
            }

            return response()->json(["acknowledged" => true, "transactionLvl" => $transactionLvl]);
        } else {
            return response()->json(["error" => true, "msg" => "Incorrect file"]);
        }
    }
}