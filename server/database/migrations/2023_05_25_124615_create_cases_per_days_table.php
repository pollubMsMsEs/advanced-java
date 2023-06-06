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
        Schema::create('cases_per_days', function (Blueprint $table) {
            $table->id();
            $table->date("day");
            $table->foreignId("country_id")->references("id")->on("countries")->cascadeOnDelete();
            $table->unsignedInteger("newCases");
            $table->unsignedInteger("newDeaths");
            $table->timestamps();
            $table->unique(["day", "country_id"]);
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::dropIfExists('cases_per_days');
    }
};