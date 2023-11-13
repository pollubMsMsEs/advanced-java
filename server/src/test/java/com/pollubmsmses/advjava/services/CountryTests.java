package com.pollubmsmses.advjava.services;

import com.pollubmsmses.advjava.models.Country;
import com.pollubmsmses.advjava.repositories.CasesPerDayRepository;
import com.pollubmsmses.advjava.repositories.CountryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CountryTests {
    @Mock
    private CountryRepository countryRepository;

    @InjectMocks
    private CountryService countryService;

    @Test
    public void test_returns_string_with_3_digits_starting_from_001_on_first_call() {
        // When
        String result = countryService.getCustomAlpha3Code();

        // Then
        assertEquals("001", result);
    }

    @Test
    public void test_returns_string_with_3_digits_and_increments_correctly() {
        // When
        String result = countryService.getCustomAlpha3Code();

        // Then
        assertEquals("001", result);
        result = countryService.getCustomAlpha3Code();
        assertEquals("002", result);
        result = countryService.getCustomAlpha3Code();
        assertEquals("003", result);
        result = countryService.getCustomAlpha3Code();
        assertEquals("004",result);
    }

    @Test
    public void test_successful_read() {
        // When,Then
        assertDoesNotThrow(countryService::loadISOMapping);
    }

    @Test
    public void test_read_csv_and_create_countries() {
        // When
        boolean result = countryService.importCountriesCSV();

        // Then
        assertTrue(result);
        verify(countryRepository,times(217)).findFirstByName(any());
    }

    @Test
    public void test_existing_country() {
        // Given
        String countryName = "United States";
        String alpha3code = "USA";
        Country existingCountry = Country.of(countryName, alpha3code);
        when(countryRepository.findByName(countryName)).thenReturn(Optional.of(existingCountry));

        // When
        Country result = countryService.getCountryByNameOrCreateCustom(countryName, alpha3code);

        // Then
        assertEquals(existingCountry, result);
        verify(countryRepository, never()).saveAndFlush(any(Country.class));
    }

    @Test
    public void test_createAndReturnNewCountry() {
        // Given
        String countryName = "TestCountry";
        String alpha3code = "TST";
        Country customCountry = Country.of(countryName, alpha3code);
        when(countryRepository.findByName(countryName)).thenReturn(Optional.empty());
        when(countryRepository.saveAndFlush(customCountry)).thenReturn(customCountry);

        // When
        Country result = countryService.getCountryByNameOrCreateCustom(countryName, alpha3code);

        // Then
        verify(countryRepository).findByName(countryName);
        verify(countryRepository).saveAndFlush(customCountry);
        assertEquals(customCountry, result);
    }

    @Test
    public void test_returns_map_with_country_names_and_ids() {
        // Given
        List<Country> countries = new ArrayList<>();
        countries.add(new Country(1L, "Country1", "Code1", new ArrayList<>(), new ArrayList<>()));
        countries.add(new Country(2L, "Country2", "Code2", new ArrayList<>(), new ArrayList<>()));
        when(countryRepository.findAll()).thenReturn(countries);

        // When
        Map<String, Long> result = countryService.getAllAsMap();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.containsKey("Country1"));
        assertTrue(result.containsKey("Country2"));
        assertEquals(1L, result.get("Country1").longValue());
        assertEquals(2L, result.get("Country2").longValue());
    }

    @Test
    public void test_getAllAsMap_sortedByCountryNameAscending() {
        // Given
        List<Country> countries = new ArrayList<>();
        countries.add(new Country(1L, "Germany", "DEU", new ArrayList<>(), new ArrayList<>()));
        countries.add(new Country(2L, "France", "FRA", new ArrayList<>(), new ArrayList<>()));
        countries.add(new Country(3L, "Italy", "ITA", new ArrayList<>(), new ArrayList<>()));
        countries.add(new Country(4L, "Spain", "ESP", new ArrayList<>(), new ArrayList<>()));

        when(countryRepository.findAll()).thenReturn(countries);

        // When
        Map<String, Long> result = countryService.getAllAsMap();

        // Then
        Map<String, Long> expected = new TreeMap<>();
        expected.put("France", 2L);
        expected.put("Germany", 1L);
        expected.put("Italy", 3L);
        expected.put("Spain", 4L);

        assertEquals(expected, result);
    }
}
