<?php

use App\Http\Controllers\CasesPerDayController;
use App\Http\Controllers\Controller;
use App\Http\Controllers\CountryController;
use App\Http\Controllers\VaccinationsController;
use App\Http\Controllers\VaccineManufacturerController;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Route;
use App\Http\Controllers\AuthController;

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
/*
Route::middleware('auth:sanctum')->get('/user', function (Request $request) {
    return $request->user();
});
*/

Route::controller(AuthController::class)->group(function () {
    Route::post('login', 'login');
    Route::post('register', 'register');
    Route::post('logout', 'logout');
    Route::post('refresh', 'refresh');
});

//Route::middleware('auth.role:user,admin')->group(function () {
Route::get("/healthz", [Controller::class, "healthz"]);
Route::get("/countries", [CountryController::class, "index"]);
Route::get("/manufacturers", [VaccineManufacturerController::class, "index"]);
Route::get("/cases", [CasesPerDayController::class, "getCases"]);
Route::get("/deaths", [CasesPerDayController::class, "getDeaths"]);
Route::get("/vaccinations", [VaccinationsController::class, "getVaccinations"]);
//});

//Route::middleware('auth.role:admin')->group(function () {
Route::put("/import/cases", [CasesPerDayController::class, "importCasesCSV"]);
Route::put("/import/vaccinations", [VaccinationsController::class, "importVaccinationsCSV"]);
//});