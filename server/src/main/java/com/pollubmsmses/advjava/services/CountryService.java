package com.pollubmsmses.advjava.services;

import com.pollubmsmses.advjava.models.CasesPerDay;
import com.pollubmsmses.advjava.models.Country;
import com.pollubmsmses.advjava.repositories.CasesPerDayRepository;
import com.pollubmsmses.advjava.repositories.CountryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import java.util.List;
import java.util.TreeMap;

@RequiredArgsConstructor
@Service
public class CountryService {
    private final CountryRepository countryRepository;

    public Map<String, String> getCountriesCSV() {
        Map<String, String> countries = new TreeMap<>();

        String path = "/importData/locations.csv";
        String line;
        String csvSplitBy = ",";

        try (InputStream is = CountryService.class.getResourceAsStream(path);

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
    public List<Country> getAll(){
        return countryRepository.findAll();
    }

    public List<CasesPerDay> test(){
        return countryRepository.findById(1L).get().getCasesPerDays();
    }
}
