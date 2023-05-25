<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Country extends Model
{
    use HasFactory;

    public function casesPerDays()
    {
        return $this->hasMany(CasesPerDay::class);
    }

    public function vaccinations()
    {
        return $this->hasMany(Vaccinations::class);
    }

    protected $fillable = [
        "name",
        "alpha3_code"
    ];
}