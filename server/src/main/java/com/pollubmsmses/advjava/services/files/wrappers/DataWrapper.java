package com.pollubmsmses.advjava.services.files.wrappers;

import lombok.Data;

import java.util.List;

@Data
public class DataWrapper {
    private List<Case> cases;
    private List<VaccinationWrapper> vaccinations;
}