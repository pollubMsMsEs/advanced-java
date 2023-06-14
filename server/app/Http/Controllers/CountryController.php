<?php

namespace App\Http\Controllers;

use App\Models\Country;
use App\Http\Requests\StoreCountryRequest;
use App\Http\Requests\UpdateCountryRequest;
use Illuminate\Support\Facades\Http;
use Illuminate\Support\Facades\Request;

class CountryController extends Controller
{
    private $code3to2 = [
        "AFG" => "AF",
        "ALA" => "AX",
        "ALB" => "AL",
        "DZA" => "DZ",
        "ASM" => "AS",
        "AND" => "AD",
        "AGO" => "AO",
        "AIA" => "AI",
        "ATA" => "AQ",
        "ATG" => "AG",
        "ARG" => "AR",
        "ARM" => "AM",
        "ABW" => "AW",
        "AUS" => "AU",
        "AUT" => "AT",
        "AZE" => "AZ",
        "BHS" => "BS",
        "BHR" => "BH",
        "BGD" => "BD",
        "BRB" => "BB",
        "BLR" => "BY",
        "BEL" => "BE",
        "BLZ" => "BZ",
        "BEN" => "BJ",
        "BMU" => "BM",
        "BTN" => "BT",
        "BOL" => "BO",
        "BES" => "BQ",
        "BIH" => "BA",
        "BWA" => "BW",
        "BVT" => "BV",
        "BRA" => "BR",
        "VGB" => "VG",
        "IOT" => "IO",
        "BRN" => "BN",
        "BGR" => "BG",
        "BFA" => "BF",
        "BDI" => "BI",
        "KHM" => "KH",
        "CMR" => "CM",
        "CAN" => "CA",
        "CPV" => "CV",
        "CYM" => "KY",
        "CAF" => "CF",
        "TCD" => "TD",
        "CHL" => "CL",
        "CHN" => "CN",
        "HKG" => "HK",
        "MAC" => "MO",
        "CXR" => "CX",
        "CCK" => "CC",
        "COL" => "CO",
        "COM" => "KM",
        "COG" => "CG",
        "COD" => "CD",
        "COK" => "CK",
        "CRI" => "CR",
        "CIV" => "CI",
        "HRV" => "HR",
        "CUB" => "CU",
        "CUW" => "CW",
        "CYP" => "CY",
        "CZE" => "CZ",
        "DNK" => "DK",
        "DJI" => "DJ",
        "DMA" => "DM",
        "DOM" => "DO",
        "ECU" => "EC",
        "EGY" => "EG",
        "SLV" => "SV",
        "GNQ" => "GQ",
        "ERI" => "ER",
        "EST" => "EE",
        "ETH" => "ET",
        "FLK" => "FK",
        "FRO" => "FO",
        "FJI" => "FJ",
        "FIN" => "FI",
        "FRA" => "FR",
        "GUF" => "GF",
        "PYF" => "PF",
        "ATF" => "TF",
        "GAB" => "GA",
        "GMB" => "GM",
        "GEO" => "GE",
        "DEU" => "DE",
        "GHA" => "GH",
        "GIB" => "GI",
        "GRC" => "GR",
        "GRL" => "GL",
        "GRD" => "GD",
        "GLP" => "GP",
        "GUM" => "GU",
        "GTM" => "GT",
        "GGY" => "GG",
        "GIN" => "GN",
        "GNB" => "GW",
        "GUY" => "GY",
        "HTI" => "HT",
        "HMD" => "HM",
        "VAT" => "VA",
        "HND" => "HN",
        "HUN" => "HU",
        "ISL" => "IS",
        "IND" => "IN",
        "IDN" => "ID",
        "IRN" => "IR",
        "IRQ" => "IQ",
        "IRL" => "IE",
        "IMN" => "IM",
        "ISR" => "IL",
        "ITA" => "IT",
        "JAM" => "JM",
        "JPN" => "JP",
        "JEY" => "JE",
        "JOR" => "JO",
        "KAZ" => "KZ",
        "KEN" => "KE",
        "KIR" => "KI",
        "PRK" => "KP",
        "KOR" => "KR",
        "KWT" => "KW",
        "KGZ" => "KG",
        "LAO" => "LA",
        "LVA" => "LV",
        "LBN" => "LB",
        "LSO" => "LS",
        "LBR" => "LR",
        "LBY" => "LY",
        "LIE" => "LI",
        "LTU" => "LT",
        "LUX" => "LU",
        "MKD" => "MK",
        "MDG" => "MG",
        "MWI" => "MW",
        "MYS" => "MY",
        "MDV" => "MV",
        "MLI" => "ML",
        "MLT" => "MT",
        "MHL" => "MH",
        "MTQ" => "MQ",
        "MRT" => "MR",
        "MUS" => "MU",
        "MYT" => "YT",
        "MEX" => "MX",
        "FSM" => "FM",
        "MDA" => "MD",
        "MCO" => "MC",
        "MNG" => "MN",
        "MNE" => "ME",
        "MSR" => "MS",
        "MAR" => "MA",
        "MOZ" => "MZ",
        "MMR" => "MM",
        "NAM" => "NA",
        "NRU" => "NR",
        "NPL" => "NP",
        "NLD" => "NL",
        "ANT" => "AN",
        "NCL" => "NC",
        "NZL" => "NZ",
        "NIC" => "NI",
        "NER" => "NE",
        "NGA" => "NG",
        "NIU" => "NU",
        "NFK" => "NF",
        "MNP" => "MP",
        "NOR" => "NO",
        "OMN" => "OM",
        "PAK" => "PK",
        "PLW" => "PW",
        "PSE" => "PS",
        "PAN" => "PA",
        "PNG" => "PG",
        "PRY" => "PY",
        "PER" => "PE",
        "PHL" => "PH",
        "PCN" => "PN",
        "POL" => "PL",
        "PRT" => "PT",
        "PRI" => "PR",
        "QAT" => "QA",
        "REU" => "RE",
        "ROU" => "RO",
        "RUS" => "RU",
        "RWA" => "RW",
        "BLM" => "BL",
        "SHN" => "SH",
        "KNA" => "KN",
        "LCA" => "LC",
        "MAF" => "MF",
        "SPM" => "PM",
        "VCT" => "VC",
        "WSM" => "WS",
        "SMR" => "SM",
        "STP" => "ST",
        "SAU" => "SA",
        "SEN" => "SN",
        "SRB" => "RS",
        "SYC" => "SC",
        "SLE" => "SL",
        "SGP" => "SG",
        "SXM" => "SX",
        "SVK" => "SK",
        "SVN" => "SI",
        "SLB" => "SB",
        "SOM" => "SO",
        "ZAF" => "ZA",
        "SGS" => "GS",
        "SSD" => "SS",
        "ESP" => "ES",
        "LKA" => "LK",
        "SDN" => "SD",
        "SUR" => "SR",
        "SJM" => "SJ",
        "SWZ" => "SZ",
        "SWE" => "SE",
        "CHE" => "CH",
        "SYR" => "SY",
        "TWN" => "TW",
        "TJK" => "TJ",
        "TZA" => "TZ",
        "THA" => "TH",
        "TLS" => "TL",
        "TGO" => "TG",
        "TKL" => "TK",
        "TON" => "TO",
        "TTO" => "TT",
        "TUN" => "TN",
        "TUR" => "TR",
        "TKM" => "TM",
        "TCA" => "TC",
        "TUV" => "TV",
        "UGA" => "UG",
        "UKR" => "UA",
        "ARE" => "AE",
        "GBR" => "GB",
        "USA" => "US",
        "UMI" => "UM",
        "URY" => "UY",
        "UZB" => "UZ",
        "VUT" => "VU",
        "VEN" => "VE",
        "VNM" => "VN",
        "VIR" => "VI",
        "WLF" => "WF",
        "ESH" => "EH",
        "YEM" => "YE",
        "ZMB" => "ZM",
        "ZWE" => "ZW",
        "XKX" => "XK"
    ];

    public static function getCountriesCSV()
    {
        $countries = [];

        try {
            if (($open = fopen(storage_path() . "/importData/locations.csv", "r")) !== false) {
                while (($data = fgetcsv($open, 1000, ",")) !== false) {
                    if (strlen($data[1]) !== 3) {
                        continue;
                    }
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
        // try {
        //     $countries = Country::query()->get(["id", "name", "alpha3_code"]);

        //     $countriesWithFlags = [];

        //     foreach ($countries as $country) {
        //         $countryCode = $country["alpha3_code"];

        //         $code2 = $this->code3to2[$countryCode];

        //         $body =

        //         $response = Http::withBody($body, "text/xml")->post("http://webservices.oorsprong.org/websamples.countryinfo/CountryInfoService.wso");


        //         $xml = simplexml_load_string($response);
        //         $xml->registerXPathNamespace("soap", "http://schemas.xmlsoap.org/soap/envelope/");
        //         $xml->registerXPathNamespace("m", "http://www.oorsprong.org/websamples.countryinfo");
        //         $found = $xml->xpath("//soap:Body/m:CountryFlagResponse/m:CountryFlagResult");

        //         $flagLink = json_decode(json_encode($found), true)[0][0];
        //         $countriesWithFlags[$country["name"]] =
        //             [
        //                 "id" => $country["id"],
        //                 "flag" => $flagLink
        //             ];

        //     }


        //     return response()->json(["data" => $countriesWithFlags]);
        // } catch (\Exception $e) {
        //     return response()->json(["error" => true, "msg" => $e->getMessage()]);
        // }

        try {
            $countries = Country::query()->pluck("id", "name");
            return response()->json(["data" => $countries]);
        } catch (\Exception $e) {
            return response()->json(["error" => true, "msg" => "Couldn't get countries"]);
        }
    }

    public function getFlag(Request $request, int $id)
    {
        try {
            $country = Country::query()->find($id);

            $countryCode = $country["alpha3_code"];

            $code2 = $this->code3to2[$countryCode];

            $body = "<?xml version='1.0' encoding='utf-8'?>
                <soap:Envelope xmlns:soap='http://schemas.xmlsoap.org/soap/envelope/'>
                <soap:Body>
                    <CountryFlag xmlns='http://www.oorsprong.org/websamples.countryinfo'>
                    <sCountryISOCode>$code2</sCountryISOCode>
                    </CountryFlag>
                </soap:Body>
                </soap:Envelope>";

            $response = Http::withBody($body, "text/xml")->post("http://webservices.oorsprong.org/websamples.countryinfo/CountryInfoService.wso");


            $xml = simplexml_load_string($response);
            $xml->registerXPathNamespace("soap", "http://schemas.xmlsoap.org/soap/envelope/");
            $xml->registerXPathNamespace("m", "http://www.oorsprong.org/websamples.countryinfo");
            $found = $xml->xpath("//soap:Body/m:CountryFlagResponse/m:CountryFlagResult");

            $flagLink = json_decode(json_encode($found), true)[0][0];

            return response()->json(["data" => $flagLink]);
        } catch (\Exception $e) {
            return response()->json(["error" => true, "msg" => $e->getMessage()]);
        }
    }
}