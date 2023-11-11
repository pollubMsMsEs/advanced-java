package com.pollubmsmses.advjava.controllers.xml.wrappers;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataWrapper {
    private List<Case> cases;
    private List<Vaccination> vaccinations;
    // getters and setters
}
