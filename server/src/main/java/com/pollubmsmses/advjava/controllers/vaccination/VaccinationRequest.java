package com.pollubmsmses.advjava.controllers.vaccination;

import java.time.LocalDate;
import java.util.List;

public class VaccinationRequest {
    private LocalDate begin_date;
    private LocalDate end_date;
    private List<Long> country;
    private List<Long> vaccineManufacturer;

    public LocalDate getEnd_date() {
        return end_date;
    }
    public LocalDate getBegin_date() {
        return begin_date;
    }
    public List<Long> getCountry() {
        return country;
    }
    public List<Long> getVaccineManufacturer() {
        return vaccineManufacturer;
    }
    
    // Gettery, settery i inne metody...
}
