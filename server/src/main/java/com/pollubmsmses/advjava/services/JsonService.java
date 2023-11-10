package com.pollubmsmses.advjava.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pollubmsmses.advjava.models.CasesPerDay;
import com.pollubmsmses.advjava.models.Country;
import com.pollubmsmses.advjava.repositories.CasesPerDayRepository;
import com.pollubmsmses.advjava.repositories.CountryRepository;
import com.pollubmsmses.advjava.repositories.VaccinationRepository;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

@Service
public class JsonService {
    private final String EXPORTED_PATH = "exported/data.json";

    private final String CASESPERDAY_TABLE = "cases_per_day";
    private final CasesPerDayRepository casesPerDayRepository;
    private final VaccinationRepository vaccinationRepository;
    private final CountryRepository countryRepository;
    private final CountryService countryService;

    @Autowired
    public JsonService(CasesPerDayRepository casesPerDayRepository,
                         VaccinationRepository vaccinationRepository,
                         CountryRepository countryRepository,
                         CountryService countryService) {
        this.casesPerDayRepository = casesPerDayRepository;
        this.vaccinationRepository = vaccinationRepository;
        this.countryRepository = countryRepository;
        this.countryService = countryService;
    }
    
    private final ObjectMapper objectMapper = new ObjectMapper()
        .registerModule(new JavaTimeModule())
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    public String exportData() {

        Map<String, List<Map<String, Object>>> data = new HashMap<>();
        data.put("cases", new ArrayList<>());
        data.put("vaccinations", new ArrayList<>());

        casesPerDayRepository.findAll(PageRequest.of(0, 1000)).forEach(caseEntity -> {
            Map<String, Object> temp = new HashMap<>();
            temp.put("day", caseEntity.getDay());
            temp.put("country", caseEntity.getCountry().getName());
            temp.put("new_cases", caseEntity.getNewCases());
            temp.put("new_deaths", caseEntity.getNewDeaths());

            data.get("cases").add(temp);
        });

        vaccinationRepository.findAll(PageRequest.of(0, 1000)).forEach(vaccination -> {
            Map<String, Object> temp = new HashMap<>();
            temp.put("day", vaccination.getDay());
            temp.put("country", vaccination.getCountry().getName());
            temp.put("vaccine_manufacturer", vaccination.getVaccineManufacturer().getName());
            temp.put("total", vaccination.getTotal());

            data.get("vaccinations").add(temp);
        });

        try {
            return objectMapper.writeValueAsString(data);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to write data to file", e);
        }
    }

    @Transactional
    public void importData(String jsonData) throws IOException {
        List<CasesPerDay> casesToSave = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, List<Map<String, Object>>> content = objectMapper.readValue(jsonData, new TypeReference<>() {});

        countryService.importCountriesCSV();

        List<Map<String, Object>> cases = content.get("cases");
        for (Map<String, Object> caseData : cases) {
            String countryName = (String) caseData.get("country");
            LocalDate day = LocalDate.parse((String) caseData.get("day"));
            
            Country country = countryRepository.findByName(countryName).orElseGet(() -> {
                String alpha3code = (String) caseData.getOrDefault("alpha3code",CountryService.getCustomAlpha3Code());
                Country customCountry = Country.of(countryName,alpha3code);
                countryRepository.save(customCountry);
                return customCountry;
            });

            CasesPerDay updatedCase = casesPerDayRepository.findTopByDayAndCountryId(day,country.getId()).orElse(CasesPerDay.of(day,0L,0L,country));
            updatedCase.setNewCases(Long.parseLong(String.valueOf(caseData.get("new_cases"))));
            updatedCase.setNewDeaths(Long.parseLong(String.valueOf(caseData.get("new_deaths"))));

            casesToSave.add(updatedCase);
        }
        casesPerDayRepository.saveAll(casesToSave);
    }
}
