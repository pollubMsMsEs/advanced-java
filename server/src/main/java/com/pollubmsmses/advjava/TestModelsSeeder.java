package com.pollubmsmses.advjava;

import com.pollubmsmses.advjava.models.CasesPerDay;
import com.pollubmsmses.advjava.models.Country;
import com.pollubmsmses.advjava.repositories.CasesPerDayRepository;
import com.pollubmsmses.advjava.repositories.CountryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class TestModelsSeeder {

    private final CountryRepository countryRepository;
    private final CasesPerDayRepository casesPerDayRepository;

    @Bean
    CommandLineRunner commandLineRunner() {
        return args -> {
            // Create 10 countries with fake data
            for (int i = 1; i <= 10; i++) {
                Country country = Country.of("Country"+i,"C" + i);

                var casesPerDay = CasesPerDay.of(country);

                // Save the country to the database
                countryRepository.save(country);
                casesPerDayRepository.save(casesPerDay);
            }
        };
    }
}