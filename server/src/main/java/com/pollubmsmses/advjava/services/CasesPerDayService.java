package com.pollubmsmses.advjava.services;

import com.pollubmsmses.advjava.models.CasesPerDay;
import com.pollubmsmses.advjava.models.Country;
import com.pollubmsmses.advjava.repositories.CasesPerDayRepository;
import com.pollubmsmses.advjava.repositories.CountryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
@Slf4j
public class CasesPerDayService {
    private final CasesPerDayRepository casesPerDayRepository;
    private final CountryRepository countryRepository;
    private final CountryService countryService;
    public ResponseEntity<Map<String, Object>> importCasesCSV() {
            try {
                Map<String, String> countries = countryService.getCountriesCSV();
                if (countries == null) {
                    throw new Exception("Couldn't open countries CSV");
                }

                ClassPathResource resource = new ClassPathResource("importData/casesAndDeaths.csv");
                BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream()));

                String currentCountryName = null;
                Country currentCountry = null;
                List<CasesPerDay> insertData = new ArrayList<>();

                casesPerDayRepository.deleteAll();
                casesPerDayRepository.flush();

                String line;
                while ((line = br.readLine()) != null) {
                    String[] data = line.split(",");
                    if ("location".equals(data[1])) {
                        continue;
                    }

                    String countryName = data[1];

                    String countryCode = countries.getOrDefault(countryName, null);
                    if (countryCode == null) {
                        continue;
                    }

                    if (!countryName.equals(currentCountryName)) {
                        currentCountryName = countryName;
                        Country country = countryRepository.findFirstByName(countryName);

                        if(country == null) {
                            country = Country.of(countryName, countryCode);
                            countryRepository.save(country);
                        }

                        currentCountry = country;
                    }


                    LocalDate day = LocalDate.parse(data[0]);
                    Double newCases = data[2].isEmpty() ?  0.0 : Double.parseDouble(data[2]);
                    Double newDeaths = data[3].isEmpty() ? 0.0 : Double.parseDouble(data[3]);

                    CasesPerDay casesPerDay = CasesPerDay.of(day,newCases,newDeaths,currentCountry);
                    insertData.add(casesPerDay);
                }

                casesPerDayRepository.saveAll(insertData);

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
    }

