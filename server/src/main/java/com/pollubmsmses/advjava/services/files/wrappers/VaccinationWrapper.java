package com.pollubmsmses.advjava.services.files.wrappers;

import lombok.Data;

import java.time.LocalDate;

@Data
public class VaccinationWrapper {
    private String country;
    private Long total;
    private String vaccine_manufacturer;
    private LocalDate day;
}
