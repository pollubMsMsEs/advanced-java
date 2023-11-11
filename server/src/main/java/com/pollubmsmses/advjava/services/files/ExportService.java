package com.pollubmsmses.advjava.services.files;

import com.pollubmsmses.advjava.repositories.CasesPerDayRepository;
import com.pollubmsmses.advjava.repositories.VaccinationRepository;
import com.pollubmsmses.advjava.repositories.VaccineManufacturerRepository;
import com.pollubmsmses.advjava.services.CountryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
@Slf4j
public class ExportService {
    private final CasesPerDayRepository casesPerDayRepository;
    private final VaccinationRepository vaccinationRepository;

    public Map<String, List<Map<String, Object>>> collectData(LocalDate beginDate, LocalDate endDate, List<Long> country,List<Long> vaccineManufacturer){
        Map<String, List<Map<String, Object>>> data = new HashMap<>();
        data.put("cases", new ArrayList<>());
        data.put("vaccinations", new ArrayList<>());

        casesPerDayRepository.filter(beginDate,endDate,country).forEach(caseEntity -> {
            Map<String, Object> temp = new HashMap<>();
            temp.put("day", caseEntity.getDay());
            temp.put("country", caseEntity.getCountry().getName());
            temp.put("new_cases", caseEntity.getNewCases());
            temp.put("new_deaths", caseEntity.getNewDeaths());

            data.get("cases").add(temp);
        });

        vaccinationRepository.filter(beginDate,endDate,country,vaccineManufacturer).forEach(vaccination -> {
            Map<String, Object> temp = new HashMap<>();
            temp.put("day", vaccination.getDay());
            temp.put("country", vaccination.getCountry().getName());
            temp.put("vaccine_manufacturer", vaccination.getVaccineManufacturer().getName());
            temp.put("total", vaccination.getTotal());

            data.get("vaccinations").add(temp);
        });

        return data;
    }
}
