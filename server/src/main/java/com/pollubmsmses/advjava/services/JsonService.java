package com.pollubmsmses.advjava.services;

import com.pollubmsmses.advjava.models.Vaccination;
import com.pollubmsmses.advjava.models.VaccineManufacturer;
import com.pollubmsmses.advjava.repositories.VaccineManufacturerRepository;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
public class JsonService {
    private final String EXPORTED_PATH = "exported/data.json";

    private final String CASESPERDAY_TABLE = "cases_per_day";
    private final CasesPerDayRepository casesPerDayRepository;
    private final VaccineManufacturerRepository vaccineManufacturerRepository;
    private final VaccinationRepository vaccinationRepository;
    private final CountryRepository countryRepository;
    private final CountryService countryService;
    
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
        List<Vaccination> vaccinationsToSave = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, List<Map<String, Object>>> content = objectMapper.readValue(jsonData, new TypeReference<>() {});

        countryService.importCountriesCSV();

        Country currentCountry = null;

        for(Map<String, Object> caseData : content.getOrDefault("cases", new ArrayList<>())){
            String countryName = (String) caseData.get("country");
            LocalDate day = LocalDate.parse((String) caseData.get("day"));

            if(currentCountry == null || !currentCountry.getName().equals(countryName)) {
                currentCountry = countryService.getCountryByNameOrCreateCustom(countryName,(String) caseData.getOrDefault("alpha3code",CountryService.getCustomAlpha3Code()));
            }

            CasesPerDay updatedCase = casesPerDayRepository.findTopByDayAndCountryId(day,currentCountry.getId()).orElse(CasesPerDay.of(day,0L,0L,currentCountry));
            updatedCase.setNewCases(Long.parseLong(String.valueOf(caseData.get("new_cases"))));
            updatedCase.setNewDeaths(Long.parseLong(String.valueOf(caseData.get("new_deaths"))));

            casesToSave.add(updatedCase);
        }

        for(Map<String, Object> vaccination : content.getOrDefault("vaccinations", new ArrayList<>())){
            String countryName = (String) vaccination.get("country");
            String vaccineManufacturerName = (String) vaccination.get("vaccine_manufacturer");
            LocalDate day = LocalDate.parse((String) vaccination.get("day"));


            if(currentCountry == null || !currentCountry.getName().equals(countryName)) {
                currentCountry = countryService.getCountryByNameOrCreateCustom(countryName,(String) vaccination.getOrDefault("alpha3code",CountryService.getCustomAlpha3Code()));
            }

            VaccineManufacturer manufacturer = vaccineManufacturerRepository.findFirstByName(vaccineManufacturerName).orElseGet(() -> {
                VaccineManufacturer created = VaccineManufacturer.of(vaccineManufacturerName);
                vaccineManufacturerRepository.saveAndFlush(created);
                return created;
            });

            Vaccination updatedVaccination = vaccinationRepository.findTopByDayAndCountryIdAndVaccineManufacturerId(day,currentCountry.getId(),manufacturer.getId()).orElse(Vaccination.of(day,0L,currentCountry,manufacturer));
            updatedVaccination.setTotal(Long.parseLong(String.valueOf(vaccination.get("total"))));

            vaccinationsToSave.add(updatedVaccination);
        }

        casesPerDayRepository.saveAll(casesToSave);
        vaccinationRepository.saveAll(vaccinationsToSave);
    }
}
