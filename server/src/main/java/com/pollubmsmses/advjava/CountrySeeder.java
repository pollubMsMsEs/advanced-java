package com.pollubmsmses.advjava;

import com.pollubmsmses.advjava.models.Country;
import com.pollubmsmses.advjava.repositories.CountryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class CountrySeeder {

    private final CountryRepository countryRepository;

    @Bean
    CommandLineRunner commandLineRunner() {
        return args -> {
            // Create 10 countries with fake data
            for (int i = 1; i <= 10; i++) {
                Country country = new Country(null,"Country"+i,"C" + i);

                // Save the country to the database
                countryRepository.save(country);
            }
        };
    }
}