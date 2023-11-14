package com.pollubmsmses.advjava.services.files.wrappers;

import lombok.Data;

import java.time.LocalDate;

@Data
public class VaccinationWrapper {
    private String country;
    private Long total;
    private String vaccine_manufacturer;
    private LocalDate day;

    public VaccinationWrapper() {
    }
    
    public VaccinationWrapper(String country, Long total, String vaccine_manufacturer, LocalDate date) {
        this.country = country;
        this.total = total;
        this.vaccine_manufacturer = vaccine_manufacturer;
        this.day = date;
    }
}
