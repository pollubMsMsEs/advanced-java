package com.pollubmsmses.advjava.controllers;

import com.pollubmsmses.advjava.models.Country;
import com.pollubmsmses.advjava.services.CountryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/country")
@RequiredArgsConstructor
public class CountryController {
    private final CountryService countryService;

    @GetMapping("all")
    public List<Country> getAll(){
        return countryService.getAll();
    }
}
