package com.pollubmsmses.advjava.services;

import com.pollubmsmses.advjava.models.Country;
import com.pollubmsmses.advjava.repositories.CountryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CountryService {
    private final CountryRepository countryRepository;

    public List<Country> getAll(){
        return countryRepository.findAll();
    }
}
