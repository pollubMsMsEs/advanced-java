package com.pollubmsmses.advjava.controllers.country;

import com.pollubmsmses.advjava.services.CountryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CountryController {
    private final CountryService countryService;

    @GetMapping("countries")
    public ResponseEntity<?> getAll() {
        Map<String, Long> countries = countryService.getAllAsMap();

        if(countries != null){
            return ResponseEntity.ok(CountriesResponse.builder().data(countries).build());
        } else {
            CountriesErrorResponse error = CountriesErrorResponse.builder().error(true).msg("Couldn't get countries").build();
            return ResponseEntity.internalServerError().body(error);
        }

    }

    @GetMapping("/country/flag/{id}")
    public ResponseEntity<?> getFlag(@PathVariable Long id) {
        try {
            String flagLink = countryService.getCountryFlag(id);

            return ResponseEntity.ok(CountryFlagResponse.builder().data(flagLink).build());
        } catch (Exception e) {
            CountryFlagErrorResponse error = CountryFlagErrorResponse.builder().error(true).msg("An error occurred while retrieving the flag.").build();
            return ResponseEntity.internalServerError().body(error);
        }
    }
}
