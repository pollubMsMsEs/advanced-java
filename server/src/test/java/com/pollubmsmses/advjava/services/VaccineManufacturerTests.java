package com.pollubmsmses.advjava.services;

import com.pollubmsmses.advjava.models.VaccineManufacturer;
import com.pollubmsmses.advjava.repositories.VaccinationRepository;
import com.pollubmsmses.advjava.repositories.VaccineManufacturerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VaccineManufacturerTests {

    @Mock
    private VaccineManufacturerRepository vaccineManufacturerRepository;

    @InjectMocks
    private VaccineManufacturerService vaccineManufacturerService;

    @Test
    public void test_returns_map_sorted_by_name_and_id() {
        // Given
        List<VaccineManufacturer> manufacturers = Arrays.asList(
                new VaccineManufacturer(1L, "Manufacturer A", new ArrayList<>()),
                new VaccineManufacturer(2L, "Manufacturer C", new ArrayList<>()),
                new VaccineManufacturer(3L, "Manufacturer B", new ArrayList<>())
        );
        when(vaccineManufacturerRepository.findAll()).thenReturn(manufacturers);

        // When
        Map<String, Long> sortedManufacturers = vaccineManufacturerService.getSortedManufacturers();

        // Then
        Map<String, Long> expectedMap = new LinkedHashMap<>();
        expectedMap.put("Manufacturer A", 1L);
        expectedMap.put("Manufacturer B", 3L);
        expectedMap.put("Manufacturer C", 2L);

        assertEquals(expectedMap, sortedManufacturers);
    }

    @Test
    public void test_returns_manufacturer_from_map_if_exists() {
        // Given
        Map<String, VaccineManufacturer> manufacturers = new HashMap<>();
        VaccineManufacturer existingManufacturer = new VaccineManufacturer(1L, "Manufacturer A", new ArrayList<>());
        manufacturers.put("Manufacturer A", existingManufacturer);

        // When
        VaccineManufacturer result = vaccineManufacturerService.getManufacturerAndAddIfMissing("Manufacturer A", manufacturers);

        // Then
        assertEquals(existingManufacturer, result);
    }

    @Test
    public void test_returns_manufacturer_from_repository_if_exists() {
        // Given
        Map<String, VaccineManufacturer> manufacturers = new HashMap<>();
        VaccineManufacturer existingManufacturer = new VaccineManufacturer(1L, "Manufacturer A", new ArrayList<>());
        when(vaccineManufacturerRepository.findFirstByName(existingManufacturer.getName())).thenReturn(Optional.of(existingManufacturer));

        // When
        VaccineManufacturer result = vaccineManufacturerService.getManufacturerAndAddIfMissing("Manufacturer A", manufacturers);

        // Then
        manufacturers.put("Manufacturer A",existingManufacturer);
        assertEquals(existingManufacturer, result);
        verify(vaccineManufacturerRepository,never()).saveAndFlush(any());
    }

    @Test
    public void test_creates_manufacturer_if_doesnt_exist() {
        // Given
        Map<String, VaccineManufacturer> manufacturers = new HashMap<>();
        VaccineManufacturer existingManufacturer = new VaccineManufacturer(1L, "Manufacturer A", new ArrayList<>());
        when(vaccineManufacturerRepository.findFirstByName(existingManufacturer.getName())).thenReturn(Optional.empty());

        // When
        VaccineManufacturer result = vaccineManufacturerService.getManufacturerAndAddIfMissing("Manufacturer A", manufacturers);

        // Then
        assertEquals(result.getName(),existingManufacturer.getName());
        verify(vaccineManufacturerRepository,times(1)).saveAndFlush(any());
    }
}
