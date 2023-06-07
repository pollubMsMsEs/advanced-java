<?php

namespace App\Http\Controllers;

use App\Models\Country;
use App\Models\Vaccinations;
use App\Models\VaccineManufacturer;
use Illuminate\Http\Request;
use Illuminate\Validation\ValidationException;

class VaccinationsController extends Controller
{
    /*
    public function __construct()
    {
        // Autoryzacja tylko dla ról admin i user
        $this->middleware('auth.role:admin,user');
    }
    */
    public function importVaccinationsCSV()
    {
        try {
            if (($countries = CountryController::getCountriesCSV()) === false)
                throw new \Exception("Couldn't open countries CSV", 333);
            if (($open = fopen(storage_path() . "/importData/vaccinations-by-manufacturer.csv", "r")) === false)
                throw new \Exception("Couldn't open countries CSV", 333);

            $currentCountryName = null;
            $currentCountryId = null;
            $manufacturerIds = [];
            $insert_data = [];

            Vaccinations::query()->delete();

            while (($data = fgetcsv($open, 200, ",")) !== false) {
                if ($data[0] === "location") {
                    continue;
                }

                $countryName = $data[0];

                if (($countryCode = array_key_exists($countryName, $countries) ? $countries[$countryName] : false) === false) {
                    continue;
                }

                if ($currentCountryName !== $countryName) {
                    $currentCountryName = $countryName;
                    $currentCountryId = Country::query()->firstOrCreate(["name" => $countryName, "alpha3_code" => $countryCode])["id"];
                }

                $manufacturerName = $data[2];
                if (!array_key_exists($manufacturerName, $manufacturerIds)) {
                    $manufacturerIds[$manufacturerName] = VaccineManufacturer::query()->firstOrCreate(["name" => $manufacturerName])["id"];

                }

                $insert_data[] = [
                    "day" => $data[1],
                    "country_id" => $currentCountryId,
                    "vaccine_manufacturer_id" => $manufacturerIds[$manufacturerName],
                    "total" => $data[3],
                ];
            }

            $insert_data = collect($insert_data);

            $CHUNK_SIZE = 1000;
            $chunks = $insert_data->chunk($CHUNK_SIZE);

            $count = 0;
            foreach ($chunks as $chunk) {
                Vaccinations::insert($chunk->toArray());
                $count += $CHUNK_SIZE;
            }

            fclose($open);

            return response()->json(["acknowledged" => true]);

        } catch (\Exception $error) {
            return response()->json(["error" => true, "msg" => $error->getMessage(), "user_friendly_msg" => $error->getCode() === 333]);
        }
    }

    public function getVaccinations(Request $request)
    {
        try {
            $validated = $request->validate([
                'begin_date' => "date",
                "end_date" => "date|after:begin_date",
                "countries" => "array",
                "manufacturers" => "array"
            ]);

            $validated["begin_date"] = date('Y-m-d', strtotime($validated["begin_date"]));
            $validated["end_date"] = date('Y-m-d', strtotime($validated["end_date"]));

            // where country in
            // where date between
            // groupBy date
            // sum new cases

            $query = Vaccinations::query();

            if (array_key_exists("manufacturers", $validated)) {
                $query->whereIn("vaccine_manufacturer_id", $validated["manufacturers"]);
            }

            $queryResult = $query
                ->whereIn("country_id", $validated["countries"])
                ->whereBetween('day', [$validated["begin_date"], $validated["end_date"]])
                ->groupBy("day", "vaccine_manufacturer_id")
                ->selectRaw("day, vaccine_manufacturer_id, sum(total) as total")
                ->orderBy("day")
                ->orderBy("vaccine_manufacturer_id")
                ->get();

            // 1, 4, 5, 8, 9
            // 2022-04-29
            $totalPerManufacturer = [];
            $tempResult = [];

            foreach ($queryResult as $row) {
                extract($row->toArray());

                if (!array_key_exists($day, $tempResult)) {
                    $tempResult[$day] = [];
                }

                $tempResult[$day][$vaccine_manufacturer_id] = $total;
                $knownManufacturers[$vaccine_manufacturer_id] = true;
            }

            $lastDayUnix = strtotime(array_key_first($tempResult));

            $result = [];
            $log = [];
            foreach ($tempResult as $day => $manufacturers) {
                $daySum = 0;

                foreach ($manufacturers as $manufacturer => $total) {

                    if (array_key_exists($manufacturer, $totalPerManufacturer)) {
                        if ($totalPerManufacturer[$manufacturer] > $total) {
                            $total = $totalPerManufacturer[$manufacturer];
                        }
                    }
                    $totalPerManufacturer[$manufacturer] = $total;
                    $daySum += $total;

                }

                foreach ($totalPerManufacturer as $manufacturer => $value) {
                    if (!array_key_exists($manufacturer, $manufacturers)) {

                        $daySum += $value;
                    }

                }

                $dateDiff = strtotime($day) - $lastDayUnix;
                $days = intval(round($dateDiff / (60 * 60 * 24)));
                if ($days !== 0) {
                    $lastDaySum = $result[date('Y-m-d', $lastDayUnix)];
                    $diffPerDay = ($daySum - $lastDaySum) / $days;

                    for ($i = 1; $i < $days - 1; $i++) {
                        $missingDay = date('Y-m-d', strtotime($i === 1 ? "$i day" : "$i days", $lastDayUnix));
                        $result[$missingDay] = $lastDaySum + $diffPerDay * $i;
                    }
                }

                $result[$day] = $daySum;
            }

            return response()->json($result);
            //return response()->json(["result" => $result, "temp_result" => $tempResult, "totalPerManufacturer" => $totalPerManufacturer, "log" => $log]);
        } catch (ValidationException $e) {
            return response()->json(["errors" => $e->errors()]);
        }
    }

}