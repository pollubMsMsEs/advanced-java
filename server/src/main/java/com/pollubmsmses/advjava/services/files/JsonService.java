package com.pollubmsmses.advjava.services.files;

import com.pollubmsmses.advjava.models.Vaccination;
import com.pollubmsmses.advjava.models.VaccineManufacturer;
import com.pollubmsmses.advjava.repositories.VaccineManufacturerRepository;
import com.pollubmsmses.advjava.services.CountryService;
import com.pollubmsmses.advjava.services.files.wrappers.DataWrapper;
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

    private final ImportService importService;
    private final CountryService countryService;
    
    private final ObjectMapper objectMapper = new ObjectMapper()
        .registerModule(new JavaTimeModule())
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    public String exportData(Object data) {
        try {
            return objectMapper.writeValueAsString(data);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to parse data to JSON", e);
        }
    }

    @Transactional
    public void importData(String jsonData) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule());
        Map<String, List<Map<String, Object>>> content = objectMapper.readValue(jsonData, new TypeReference<>() {});
        DataWrapper wrapper = objectMapper.convertValue(content,DataWrapper.class);

        countryService.importCountriesCSV();
        importService.importCasesPerDay(wrapper.getCases());
        importService.importVaccinations(wrapper.getVaccinations());
    }
}
