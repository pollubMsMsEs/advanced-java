package com.pollubmsmses.advjava.controllers;

import com.pollubmsmses.advjava.models.CasesPerDay;
import com.pollubmsmses.advjava.models.Country;
import com.pollubmsmses.advjava.services.CountryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/casesPerDay")
@RequiredArgsConstructor
public class CasesPerDayController {
    private final CountryService countryService;

    @GetMapping("test")
    public List<CasesPerDay> getFromCountry(){
        return countryService.test();
    }
}
