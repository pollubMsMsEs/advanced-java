package com.pollubmsmses.advjava.services;

import com.pollubmsmses.advjava.controllers.vaccination.VaccinationsResponse;
import com.pollubmsmses.advjava.models.Country;
import com.pollubmsmses.advjava.models.Vaccination;
import com.pollubmsmses.advjava.models.VaccineManufacturer;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import com.pollubmsmses.advjava.repositories.CountryRepository;
import com.pollubmsmses.advjava.repositories.VaccinationRepository;
import com.pollubmsmses.advjava.repositories.VaccineManufacturerRepository;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class VaccinationService {
    private final String VACCINATIONS_PATH = "importData/vaccinations-by-manufacturer.csv";

    private final VaccinationRepository vaccinationRepository;
    private final CountryRepository countryRepository;
    private final VaccineManufacturerRepository vaccineManufacturerRepository;
    private final CountryService countryService;

    @Transactional
    public ResponseEntity<Map<String, Object>> importVaccinationsCSV() {
        try {
            Map<String, String> countries = countryService.getCountriesCSV();
            if (countries == null) {
                throw new Exception("Couldn't open countries CSV");
            }

            ClassPathResource resource = new ClassPathResource(VACCINATIONS_PATH);
            BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream()));

            String currentCountryName = null;
            Long currentCountryId = null;
            Map<String, Long> manufacturerIds = new HashMap<>();
            List<Vaccination> insertData = new ArrayList<>();

            vaccinationRepository.deleteAll();

            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if ("location".equals(data[0])) {
                    continue;
                }

                String countryName = data[0];

                String countryCode = countries.getOrDefault(countryName, null);
                if (countryCode == null) {
                    continue;
                }

                if (!countryName.equals(currentCountryName)) {
                    currentCountryName = countryName;
                    Country country = countryRepository.findFirstByName(countryName);
                    if (country == null) {
                        country = countryRepository.findFirstByAlpha3code(countryCode);
                        if(country == null) {
                            country = Country.of(countryName, countryCode);
                            countryRepository.save(country);
                        }
                    }
                    currentCountryId = country.getId();
                }

                String manufacturerName = data[2];
                if (!manufacturerIds.containsKey(manufacturerName)) {
                    VaccineManufacturer manufacturer = vaccineManufacturerRepository.findFirstByName(manufacturerName);
                    if (manufacturer == null) {
                        manufacturer = VaccineManufacturer.of(manufacturerName);
                        vaccineManufacturerRepository.save(manufacturer);
                    }
                    manufacturerIds.put(manufacturerName, manufacturer.getId());
                }

                LocalDate day = LocalDate.parse(data[1]);
                Country currentCountry = countryRepository.findById(currentCountryId).orElse(null);
                VaccineManufacturer manufacturer = vaccineManufacturerRepository.findById(manufacturerIds.get(manufacturerName)).orElse(null);
                Long total = Long.parseLong(data[3]);
                Vaccination vaccination = Vaccination.of(day, total, currentCountry, manufacturer);
                insertData.add(vaccination);
            }

            final int CHUNK_SIZE = 1000;
            int i = 0;
            while (i < insertData.size()) {
                List<Vaccination> chunk = insertData.subList(i, Math.min(i + CHUNK_SIZE, insertData.size()));
                vaccinationRepository.saveAll(chunk);
                i += CHUNK_SIZE;
            }

            br.close();

            Map<String, Object> response = new HashMap<>();
            response.put("acknowledged", true);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", true);
            errorResponse.put("msg", e.getMessage());
            errorResponse.put("user_friendly_msg", e.getMessage().equals("333"));
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @Transactional
    public VaccinationsResponse getVaccinations(LocalDate begin_date, LocalDate end_date, List<Long> country, List<Long> vaccineManufacturer) {
        if (vaccineManufacturer == null || vaccineManufacturer.isEmpty()) {
            vaccineManufacturer = new ArrayList<>();
            for (long i = 1; i <= 15; i++) {
                vaccineManufacturer.add(i);
            }
        }

        List<Vaccination> vaccinations = vaccinationRepository.filter(begin_date, end_date, country, vaccineManufacturer);
        
        // Mapa dla wyników
        Map<LocalDate, Long> resultMap = new TreeMap<>();

        Map<Long, Long> totalPerManufacturer = new TreeMap<>();

        for (Vaccination vaccination : vaccinations) {
            LocalDate day = vaccination.getDay();
            VaccineManufacturer manufacturer = vaccination.getVaccineManufacturer();
        
            if (manufacturer != null) {
                Long manufacturerId = manufacturer.getId();
                Long total = vaccination.getTotal();
        
                // Update total per manufacturer
                totalPerManufacturer.put(manufacturerId, totalPerManufacturer.getOrDefault(manufacturerId, 0L) + total);
        
                // Storing in the resultMap directly without tempResult
                resultMap.put(day, resultMap.getOrDefault(day, 0L) + total);
            }
        }

        // Uzupełnienie brakujących dat
        LocalDate currentDay = begin_date;
        while (currentDay != null && !currentDay.isAfter(end_date)) {
            resultMap.putIfAbsent(currentDay, 0L);
            currentDay = currentDay.plusDays(1);
        }

        LocalDate previousDay = begin_date;
    currentDay = begin_date.plusDays(1);
    while (currentDay != null && !currentDay.isAfter(end_date)) {
        if (resultMap.get(currentDay) < resultMap.get(previousDay)) {
            resultMap.put(currentDay, resultMap.get(previousDay));
        }
        previousDay = currentDay;
        currentDay = currentDay.plusDays(1);
    }

        return new VaccinationsResponse(resultMap);
    }
}