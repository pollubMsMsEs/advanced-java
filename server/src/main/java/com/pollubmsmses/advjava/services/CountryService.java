package com.pollubmsmses.advjava.services;

import com.pollubmsmses.advjava.models.CasesPerDay;
import com.pollubmsmses.advjava.models.Country;
import com.pollubmsmses.advjava.repositories.CountryRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CountryService {
    private final CountryRepository countryRepository;

    private final String LOCATIONS_PATH = "/importData/locations.csv";


    public Map<String, String> getCountriesCSV() {
        Map<String, String> countries = new TreeMap<>();

        String line;
        String csvSplitBy = ",";

        try (InputStream is = CountryService.class.getResourceAsStream(LOCATIONS_PATH);

                BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            while ((line = br.readLine()) != null) {
                String[] country = line.split(csvSplitBy);
                if (country[1].length() != 3) {
                    continue;
                }
                countries.put(country[0], country[1]);
            }
        } catch (IOException e) {
            e.printStackTrace();
            countries = null;
        }

        if (countries != null) {
            countries.remove("location");
        }

        return countries;
    }
    public Map<String, Long> getAllAsMap(){
        try {
            return countryRepository.findAll().stream().collect(Collectors.toMap(Country::getName,Country::getId,(oldValue,newValue) -> oldValue,TreeMap::new));
        } catch (Exception e) {
            return null;
        }
    }
}
