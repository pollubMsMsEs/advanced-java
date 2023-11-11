package com.pollubmsmses.advjava.services.files.wrappers;

import lombok.Data;

import java.time.LocalDate;

@Data
public class Case {
    private String country;
    private Long new_cases;
    private Long new_deaths;
    private LocalDate day;
}
