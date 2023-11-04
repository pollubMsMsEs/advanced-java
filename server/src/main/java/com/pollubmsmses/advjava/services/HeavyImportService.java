package com.pollubmsmses.advjava.services;

import com.pollubmsmses.advjava.models.CasesPerDay;
import com.pollubmsmses.advjava.models.Country;
import com.pollubmsmses.advjava.models.Vaccination;
import com.pollubmsmses.advjava.models.VaccineManufacturer;
import com.pollubmsmses.advjava.repositories.CountryRepository;
import com.pollubmsmses.advjava.repositories.VaccinationRepository;
import com.pollubmsmses.advjava.repositories.VaccineManufacturerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.*;

@RequiredArgsConstructor
@Service
@Slf4j
public class HeavyImportService {
    private final String VACCINATIONS_PATH = "importData/vaccinations-by-manufacturer.csv";
    private final String CASESPERDAY_PATH = "importData/casesAndDeaths.csv";
    private final String VACCINATIONS_TABLE = "vaccination";
    private final String CASESPERDAY_TABLE = "cases_per_day";

    private final CountryRepository countryRepository;
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
            String[] data = line.split(",");

            String countryName = data[0];
            Optional<Country> result = getCurrentCountry(countriesBlackList,countryName,currentCountry);
            if(result.isPresent()){
                currentCountry = result.get();
            } else {
                continue;
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

        log.info("Inserted: " + rows + " vaccinations");
    }


    @Transactional
    public void importCasesPerDayCSV() throws Exception{
        if (!countryService.importCountriesCSV()) throw new Exception("Couldn't open countries CSV");

        ClassPathResource resource = new ClassPathResource(CASESPERDAY_PATH);
        BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream()));

        jdbcTemplate.execute(String.format("DELETE FROM %s",CASESPERDAY_TABLE));

        String line;
        String insertQuery = (String.format("INSERT INTO %s (id, day, new_cases,new_deaths, country_id) VALUES (NULL, ?, ?, ?, ?)",CASESPERDAY_TABLE));

        Country currentCountry = null;
        Map<String,Boolean> countriesBlackList = new HashMap<>();
        List<CasesPerDay> casesPerDays = new ArrayList<>();

        br.readLine();
        int rows = 0;
        while ((line = br.readLine()) != null) {
            String[] data = line.split(",");

            String countryName = data[1];
            Optional<Country> result = getCurrentCountry(countriesBlackList,countryName,currentCountry);
            if(result.isPresent()){
                currentCountry = result.get();
            } else {
                continue;
            }

            LocalDate day = LocalDate.parse(data[0]);
            Long newCases = data[2].isEmpty() ?  0L : Math.round(Double.parseDouble(data[2]));
            Long newDeaths = data[3].isEmpty() ? 0L : Math.round(Double.parseDouble(data[3]));

            rows++;
            casesPerDays.add( CasesPerDay.of(day,newCases,newDeaths,currentCountry));
        }
        jdbcTemplate.batchUpdate(insertQuery,
                casesPerDays,
                1000,
                (PreparedStatement ps, CasesPerDay casesPerDay) -> {
                    ps.setDate(1, Date.valueOf(casesPerDay.getDay()));
                    ps.setDouble(2, casesPerDay.getNewCases());
                    ps.setDouble(3,casesPerDay.getNewDeaths());
                    ps.setLong(4,casesPerDay.getCountry().getId());
                });

        log.info("Inserted: " + rows);
    }

    private Optional<Country> getCurrentCountry(Map<String,Boolean> countriesBlackList, String countryName, Country previousCountry){
        if(countriesBlackList.getOrDefault(countryName,false)) return Optional.empty();

        if(previousCountry == null || !previousCountry.getName().equals(countryName)) {
            Country foundCountry = countryRepository.findFirstByName(countryName);
            if(foundCountry == null){
                countriesBlackList.put(countryName,true);
                return Optional.empty();
            }

            return Optional.of(foundCountry);
        }

        return Optional.of(previousCountry);
    }
}
