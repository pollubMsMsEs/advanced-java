package com.pollubmsmses.advjava.services;

import com.pollubmsmses.advjava.models.Country;
import com.pollubmsmses.advjava.models.Vaccination;
import com.pollubmsmses.advjava.models.VaccineManufacturer;
import com.pollubmsmses.advjava.repositories.CountryRepository;
import com.pollubmsmses.advjava.repositories.VaccinationRepository;
import com.pollubmsmses.advjava.repositories.VaccineManufacturerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
@Slf4j
public class HeavyImportService {
    private final String VACCINATIONS_PATH = "importData/vaccinations-by-manufacturer.csv";
    private final String VACCINATIONS_TABLE = "vaccination";

    private final VaccinationRepository vaccinationRepository;
    private final CountryRepository countryRepository;
    private final VaccineManufacturerRepository vaccineManufacturerRepository;
    private final CountryService countryService;
    private final VaccineManufacturerService vaccineManufacturerService;
    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public void importVaccinationsCSV() throws Exception{
        if (!countryService.importCountriesCSV()) throw new Exception("Couldn't open countries CSV");

        ClassPathResource resource = new ClassPathResource(VACCINATIONS_PATH);
        BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream()));

        jdbcTemplate.execute(String.format("DELETE FROM %s",VACCINATIONS_TABLE));

        String line;
        String insertQuery = (String.format("INSERT INTO %s (id, day, total, country_id, vaccine_manufacturer_id) VALUES (NULL, ?, ?, ?, ?)",VACCINATIONS_TABLE));

        Country currentCountry = null;
        Map<String,Boolean> countriesBlackList = new HashMap<>();
        Map<String, VaccineManufacturer> manufacturers = new HashMap<>();
        List<Vaccination> vaccinations = new ArrayList<>();

        br.readLine();
        int rows = 0;
        while ((line = br.readLine()) != null) {
            //if(rows >= 5000) break;
            String[] data = line.split(",");

            String countryName = data[0];
            if(countriesBlackList.getOrDefault(countryName,false)) continue;

            if(currentCountry == null || !currentCountry.getName().equals(countryName)) {
                Country foundCountry = countryRepository.findFirstByName(countryName);
                if(foundCountry == null){
                    countriesBlackList.put(countryName,true);
                    continue;
                }

                currentCountry = foundCountry;
            }

            VaccineManufacturer manufacturer = vaccineManufacturerService.getManufacturerAndAddIfMissing(data[2],manufacturers);


            LocalDate day = LocalDate.parse(data[1]);
            Long total = Long.parseLong(data[3]);

            rows++;
            vaccinations.add(Vaccination.of(day,total,currentCountry,manufacturer));
        }

        jdbcTemplate.batchUpdate(insertQuery,
                vaccinations,
                1000,
                (PreparedStatement ps, Vaccination vaccination) -> {
                    ps.setDate(1, Date.valueOf(vaccination.getDay()));
                    ps.setLong(2,vaccination.getTotal());
                    ps.setLong(3,vaccination.getCountry().getId());
                    ps.setLong(4,vaccination.getVaccineManufacturer().getId());
                });

        log.info("Inserted: " + rows);
    }
}
