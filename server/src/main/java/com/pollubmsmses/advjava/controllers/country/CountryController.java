package com.pollubmsmses.advjava.controllers.country;

import com.pollubmsmses.advjava.models.Country;
import com.pollubmsmses.advjava.services.CountryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class CountryController {
    private final CountryService countryService;

    @GetMapping("country/all")
    public Map<String, String> getAllFromCSV(){
        return countryService.getCountriesCSV();
    }

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
}
