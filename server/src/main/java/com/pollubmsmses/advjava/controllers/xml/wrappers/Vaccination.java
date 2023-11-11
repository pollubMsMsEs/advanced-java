package com.pollubmsmses.advjava.controllers.xml.wrappers;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Vaccination {
    private String country;
    private Long total;
    private String vaccine_manufacturer;
    private LocalDate day;
    // getters and setters
}