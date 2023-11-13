package com.pollubmsmses.advjava.services;

import com.pollubmsmses.advjava.controllers.vaccination.VaccinationsResponse;
import com.pollubmsmses.advjava.models.Country;
import com.pollubmsmses.advjava.models.Vaccination;
import com.pollubmsmses.advjava.models.VaccineManufacturer;
import com.pollubmsmses.advjava.repositories.CountryRepository;
import com.pollubmsmses.advjava.repositories.VaccinationRepository;
import com.pollubmsmses.advjava.repositories.VaccineManufacturerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class VaccinationTests {

    @Mock
    private VaccinationRepository vaccinationRepository;

    @InjectMocks
    private VaccinationService vaccinationService;

    @Test
    public void test_valid_date_range_and_ids() {
        // Given
        LocalDate beginDate = LocalDate.of(2021, 1, 1);
        LocalDate endDate = LocalDate.of(2021, 1, 31);
        List<Long> countryIds = Arrays.asList(1L, 2L);
        List<Long> manufacturerIds = Arrays.asList(1L, 2L);


        // When
        VaccinationsResponse response = vaccinationService.getVaccinations(beginDate, endDate, countryIds, manufacturerIds);

        // Then
        assertNotNull(response);
        assertEquals(31, response.getData().size());
    }

    @Test
    public void test_vaccination_repository_returns_bigger_and_then_equal_total() {
        // Given
        LocalDate beginDate = LocalDate.of(2021, 1, 1);
        LocalDate endDate = LocalDate.of(2021, 1, 3);
        List<Long> country = Arrays.asList(1L, 2L);
        List<Long> vaccineManufacturer = Arrays.asList(1L, 2L);

        Vaccination vaccination1 = Vaccination.of(LocalDate.of(2021, 1, 1), 100L, Country.of("NAME1","001"), new VaccineManufacturer(1L,"MAN1", new ArrayList<>()));
        Vaccination vaccination2 = Vaccination.of(LocalDate.of(2021, 1, 2), 200L, Country.of("NAME2","002"), new VaccineManufacturer(2L,"MAN2", new ArrayList<>()));
        Vaccination vaccination3 = Vaccination.of(LocalDate.of(2021, 1, 3), 150L, Country.of("NAME3","003"), new VaccineManufacturer(1L,"MAN1", new ArrayList<>()));

        List<Vaccination> vaccinations = Arrays.asList(vaccination1, vaccination2, vaccination3);

        when(vaccinationRepository.filter(beginDate, endDate, country, vaccineManufacturer)).thenReturn(vaccinations);

        // When
        VaccinationsResponse response = vaccinationService.getVaccinations(beginDate, endDate, country, vaccineManufacturer);

        // Then
        Map<LocalDate, Long> expectedData = new TreeMap<>();
        expectedData.put(LocalDate.of(2021, 1, 1), 100L);
        expectedData.put(LocalDate.of(2021, 1, 2), 200L);
        expectedData.put(LocalDate.of(2021, 1, 3), 200L);

        assertEquals(expectedData, response.getData());
    }
}
