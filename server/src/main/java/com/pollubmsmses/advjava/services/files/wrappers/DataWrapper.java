package com.pollubmsmses.advjava.services.files.wrappers;

import lombok.Data;

import java.util.List;

@Data
public class DataWrapper {
    private Long id;
    private List<Case> cases;
    private List<VaccinationWrapper> vaccinations;

    public DataWrapper() {
    }

    public DataWrapper(List<Case> cases, List<VaccinationWrapper> vaccinations) {
        this.cases = cases;
        this.vaccinations = vaccinations;
    }
}