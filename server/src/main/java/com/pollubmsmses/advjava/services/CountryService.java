package com.pollubmsmses.advjava.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pollubmsmses.advjava.models.CasesPerDay;
import com.pollubmsmses.advjava.models.Country;
import com.pollubmsmses.advjava.repositories.CountryRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class CountryService {
    private final CountryRepository countryRepository;
    private final String LOCATIONS_PATH = "/importData/locations.csv";
    private final String ISO_MAPPING_PATH = "/importData/countryISOMapping.json";

    private Map<String, String> code3to2;

    @PostConstruct
    public void loadISOMapping() {
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<Map<String, String>> typeReference = new TypeReference<Map<String, String>>() {};

        try (InputStream inputStream = CountryService.class.getResourceAsStream(ISO_MAPPING_PATH)) {
            code3to2 = mapper.readValue(inputStream,typeReference);
            log.info("Country codes read succesfuly, example: POL=" + code3to2.get("POL"));
        }catch (IOException e) {
            e.printStackTrace();
        }

    }

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
