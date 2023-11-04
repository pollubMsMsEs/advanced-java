package com.pollubmsmses.advjava.services;

import com.pollubmsmses.advjava.controllers.casesPerDay.CasesResponse;
import com.pollubmsmses.advjava.controllers.vaccination.VaccinationsResponse;
import com.pollubmsmses.advjava.models.CasesPerDay;
import com.pollubmsmses.advjava.models.Country;
import com.pollubmsmses.advjava.models.Vaccination;
import com.pollubmsmses.advjava.models.VaccineManufacturer;
import com.pollubmsmses.advjava.repositories.CasesPerDayRepository;
import com.pollubmsmses.advjava.repositories.CountryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.*;

@RequiredArgsConstructor
@Service
@Slf4j
public class CasesPerDayService {
    private final CasesPerDayRepository casesPerDayRepository;
    @Transactional
    public CasesResponse getCases(LocalDate begin_date, LocalDate end_date, List<Long> country) {
        List<CasesPerDay> cases = casesPerDayRepository.filter(begin_date, end_date, country);
        Map<LocalDate, Long> resultMap = new TreeMap<>();
        for (CasesPerDay casePerDay : cases) {
            LocalDate day = casePerDay.getDay();
            Long newCases = casePerDay.getNewCases();

            resultMap.put(day, resultMap.getOrDefault(day, 0L) + newCases);
        }
     return new CasesResponse(resultMap);
    }
    @Transactional
    public CasesResponse getDeaths(LocalDate begin_date, LocalDate end_date, List<Long> country) {
        List<CasesPerDay> cases = casesPerDayRepository.filter(begin_date, end_date, country);
        Map<LocalDate, Long> resultMap = new TreeMap<>();
        for (CasesPerDay casePerDay : cases) {
            LocalDate day = casePerDay.getDay();
            Long deaths = casePerDay.getNewDeaths();

            resultMap.put(day, resultMap.getOrDefault(day, 0L) + deaths);
        }
        return new CasesResponse(resultMap);
    }
}

