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

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class VaccinationService {
    private final String VACCINATIONS_PATH = "importData/vaccinations-by-manufacturer.csv";

    private final VaccinationRepository vaccinationRepository;
    private final CountryRepository countryRepository;
    private final VaccineManufacturerRepository vaccineManufacturerRepository;
    private final CountryService countryService;

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