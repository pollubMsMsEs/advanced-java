package com.pollubmsmses.advjava.controllers.vaccination;

import java.time.LocalDate;
import java.util.List;

public class VaccinationRequest {
    private LocalDate begin_date;
    private LocalDate end_date;
    private List<Long> countries;
    private List<Long> manufacturers;

    public LocalDate getEnd_date() {
        return end_date;
    }
    public LocalDate getBegin_date() {
        return begin_date;
    }
    public List<Long> getCountries() {
        return countries;
    }
    public List<Long> getManufacturers() {
        return manufacturers;
    }
    
    // Gettery, settery i inne metody...
}
