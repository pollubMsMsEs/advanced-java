<?php

use App\Http\Controllers\CasesPerDayController;
use App\Http\Controllers\CountryController;
use App\Http\Controllers\VaccinationsController;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Route;

/*
|--------------------------------------------------------------------------
| API Routes
|--------------------------------------------------------------------------
|
| Here is where you can register API routes for your application. These
| routes are loaded by the RouteServiceProvider and all of them will
| be assigned to the "api" middleware group. Make something great!
|
*/

Route::middleware('auth:sanctum')->get('/user', function (Request $request) {
    return $request->user();
});

Route::get("/countries", [CountryController::class, "index"]);
Route::get("/import/cases", [CasesPerDayController::class, "importCasesCSV"]);
Route::get("/import/vaccinations", [VaccinationsController::class, "importVaccinationsCSV"]);