<?php

namespace App\Http\Controllers;

use App\Models\VaccineManufacturer;
use Illuminate\Http\Request;

class VaccineManufacturerController extends Controller
{
    public function index()
    {
        try {
            $result = VaccineManufacturer::query()->pluck("id", "name");
            return response()->json(["data" => $result]);
        } catch (\Exception $error) {
            return response()->json(["msg" => "Couldn't get manufacturers", "error" => true]);
        }

    }
}