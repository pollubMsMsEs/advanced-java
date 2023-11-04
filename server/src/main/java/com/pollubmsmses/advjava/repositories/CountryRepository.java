package com.pollubmsmses.advjava.repositories;

import com.pollubmsmses.advjava.models.Country;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CountryRepository extends JpaRepository<Country, Long> {
    Country findFirstByName(String countryName);
    Country findFirstByAlpha3code(String alpha3code);
    Country getCountryById(Long id);
}
