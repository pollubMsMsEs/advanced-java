<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Vaccinations extends Model
{
    use HasFactory;

    public function vaccineManufacturer()
    {
        return $this->belongsTo(VaccineManufacturer::class);
    }

    public function country()
    {
        return $this->belongsTo(Country::class);
    }

    protected $fillable = [
        "day",
        "country_id",
        "vaccine_manufacturer_id",
        "total"
    ];
}