package com.pollubmsmses.advjava.controllers.casesPerDay;

import java.time.LocalDate;
import java.util.List;

public class CasesRequest {
    private LocalDate begin_date;
    private LocalDate end_date;
    private List<Long> countries;

    public LocalDate getEnd_date() {
        return end_date;
    }
    public LocalDate getBegin_date() {
        return begin_date;
    }
    public List<Long> getCountries() {
        return countries;
    }
}
