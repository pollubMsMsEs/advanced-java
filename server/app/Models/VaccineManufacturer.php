<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class VaccineManufacturer extends Model
{
    use HasFactory;

    public function vaccinations()
    {
        return $this->hasMany(Vaccinations::class);
    }

    protected $fillable = [
        "name"
    ];
}