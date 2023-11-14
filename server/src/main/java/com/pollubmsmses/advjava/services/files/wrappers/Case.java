package com.pollubmsmses.advjava.services.files.wrappers;

import lombok.Data;

import java.time.LocalDate;

@Data
public class Case {
    private String country;
    private Long new_cases;
    private Long new_deaths;
    private LocalDate day;

    public Case() {
    }

    public Case(String country, Long new_cases, Long new_deaths, LocalDate day) {
        this.country = country;
        this.new_cases = new_cases;
        this.new_deaths = new_deaths;
        this.day = day;
    }
}
