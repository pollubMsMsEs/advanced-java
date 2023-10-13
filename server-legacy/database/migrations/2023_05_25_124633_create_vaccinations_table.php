<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration {
    /**
     * Run the migrations.
     */
    public function up(): void
    {
        Schema::create('vaccinations', function (Blueprint $table) {
            $table->id();
            $table->date("day");
            $table->foreignId("country_id")->references("id")->on("countries")->cascadeOnDelete();
            $table->foreignId("vaccine_manufacturer_id")->references("id")->on("vaccine_manufacturers")->cascadeOnDelete();
            $table->unsignedInteger("total");
            $table->timestamps();
            $table->unique(["day", "country_id", "vaccine_manufacturer_id"]);
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::dropIfExists('vaccinations');
    }
};