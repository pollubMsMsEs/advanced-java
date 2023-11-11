package com.pollubmsmses.advjava.controllers.xml.wrappers;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Case {
    private String country;
    private Long new_cases;
    private Long new_deaths;
    private LocalDate day;
    // getters and setters
}
