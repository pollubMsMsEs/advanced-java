<?php

namespace App\Http\Controllers;

use App\Models\CasesPerDay;
use App\Models\Country;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;
use Illuminate\Validation\ValidationException;

class CasesPerDayController extends Controller
{
    public function importCasesCSV()
    {
        try {
            if (($countries = CountryController::getCountriesCSV()) === false)
                throw new \Exception("Couldn't open countries CSV", 333);

            if (($open = fopen(storage_path() . "/importData/casesAndDeaths.csv", "r")) === false)
                throw new \Exception("Couldn't open cases CSV", 333);

            $currentCountryName = null;
            $currentCountryId = null;
            $insert_data = [];

            CasesPerDay::query()->delete();

            while (($data = fgetcsv($open, 200, ",")) !== false) {
                if ($data[0] === "date")
                    continue;

                $countryName = $data[1];

                if (($countryCode = array_key_exists($countryName, $countries) ? $countries[$countryName] : false) === false) {
                    continue;
                }

                if ($currentCountryName !== $countryName) {
                    $currentCountryName = $countryName;
                    $currentCountryId = Country::query()->firstOrCreate(["name" => $countryName, "alpha3_code" => $countryCode])["id"];
                }

                $insert_data[] = [
                    "day" => $data[0],
                    "country_id" => $currentCountryId,
                    "newCases" => intval($data[2]),
                    "newDeaths" => intval($data[3])
                ];
            }

            $insert_data = collect($insert_data);

            $CHUNK_SIZE = 1000;
            $chunks = $insert_data->chunk($CHUNK_SIZE);

            $count = 0;
            foreach ($chunks as $chunk) {
                CasesPerDay::insert($chunk->toArray());
                $count += $CHUNK_SIZE;
            }

            fclose($open);

            return response()->json(["acknowledged" => true]);

        } catch (\Exception $error) {
            return response()->json(["error" => true, "msg" => $error->getMessage(), "user_friendly_msg" => $error->getCode() === 333]);
        }

    }

    public function getCases(Request $request)
    {
        try {
            $validated = $request->validate([
                'begin_date' => "date",
                "end_date" => "date|after:begin_date",
                "countries" => "array"
            ]);

            $validated["begin_date"] = date('Y-m-d', strtotime($validated["begin_date"]));
            $validated["end_date"] = date('Y-m-d', strtotime($validated["end_date"]));

            // where country in
            // where date between
            // groupBy date
            // sum new cases

            $result = CasesPerDay::query()
                ->whereIn("country_id", $validated["countries"])
                ->whereBetween('day', [$validated["begin_date"], $validated["end_date"]])
                ->groupBy("day")
                ->selectRaw("sum(newCases) as sum, day")
                ->pluck("sum", "day");

            return response()->json($result);
        } catch (ValidationException $e) {
            return response()->json(["errors" => $e->errors()]);
        }
    }

    protected function buildFailedValidationResponse(Request $request, array $errors)
    {
        return $this->setStatusCode(422)->respondWithError($errors);
    }
}