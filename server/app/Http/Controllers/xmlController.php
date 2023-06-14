<?php

namespace App\Http\Controllers;

use App\Models\CasesPerDay;
use App\Models\Vaccinations;
use Illuminate\Http\Request;

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

        xmlwriter_end_document($xw);

        file_put_contents($filename, xmlwriter_output_memory($xw), LOCK_EX);
        return response()->download(
            $filename,
        );
    }

    public function import()
    {

    }
}