package com.pollubmsmses.advjava.services;

import com.pollubmsmses.advjava.controllers.casesPerDay.CasesResponse;
import com.pollubmsmses.advjava.models.CasesPerDay;
import com.pollubmsmses.advjava.models.Country;
import com.pollubmsmses.advjava.repositories.CasesPerDayRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CasesPerDayTests {
    @Test
    public void test_getCases_validInput() {
        // Arrange
        CasesPerDayRepository casesPerDayRepository = mock(CasesPerDayRepository.class);
        CasesPerDayService casesPerDayService = new CasesPerDayService(casesPerDayRepository);
        LocalDate beginDate = LocalDate.of(2021, 1, 1);
        LocalDate endDate = LocalDate.of(2021, 1, 31);
        List<Long> country = Arrays.asList(1L, 2L, 3L);
        List<CasesPerDay> cases = new ArrayList<>();
        cases.add(CasesPerDay.of(LocalDate.of(2021, 1, 1), 100L, 10L, Country.of("Name1","001")));
        cases.add(CasesPerDay.of(LocalDate.of(2021, 1, 2), 200L, 20L, Country.of("Name1","002")));
        cases.add(CasesPerDay.of(LocalDate.of(2021, 1, 3), 300L, 30L, Country.of("Name1","003")));
        when(casesPerDayRepository.filter(beginDate, endDate, country)).thenReturn(cases);

        // Act
        CasesResponse result = casesPerDayService.getCases(beginDate, endDate, country);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.getData().size());
        assertEquals(100L, result.getData().get(LocalDate.of(2021, 1, 1)).longValue());
        assertEquals(200L, result.getData().get(LocalDate.of(2021, 1, 2)).longValue());
        assertEquals(300L, result.getData().get(LocalDate.of(2021, 1, 3)).longValue());
    }

    @Test
    public void test_getDeaths_validInput() {
        // Arrange
        CasesPerDayRepository casesPerDayRepository = mock(CasesPerDayRepository.class);
        CasesPerDayService casesPerDayService = new CasesPerDayService(casesPerDayRepository);
        LocalDate beginDate = LocalDate.of(2021, 1, 1);
        LocalDate endDate = LocalDate.of(2021, 1, 31);
        List<Long> country = Arrays.asList(1L, 2L, 3L);
        List<CasesPerDay> cases = new ArrayList<>();
        cases.add(new CasesPerDay(1L, LocalDate.of(2021, 1, 1), 100L, 10L, Country.of("Name1","001")));
        cases.add(new CasesPerDay(2L, LocalDate.of(2021, 1, 2), 200L, 20L, Country.of("Name1","002")));
        when(casesPerDayRepository.filter(beginDate, endDate, country)).thenReturn(cases);

        // Act
        CasesResponse result = casesPerDayService.getDeaths(beginDate, endDate, country);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getData().size());
        assertEquals(10L, result.getData().get(LocalDate.of(2021, 1, 1)).longValue());
        assertEquals(20L, result.getData().get(LocalDate.of(2021, 1, 2)).longValue());
    }

    @Test
    public void test_getCases_emptyResult() {
        // Arrange
        CasesPerDayRepository casesPerDayRepository = mock(CasesPerDayRepository.class);
        CasesPerDayService casesPerDayService = new CasesPerDayService(casesPerDayRepository);
        LocalDate beginDate = LocalDate.of(2021, 1, 1);
        LocalDate endDate = LocalDate.of(2021, 1, 31);
        List<Long> country = Arrays.asList(1L, 2L, 3L);
        List<CasesPerDay> cases = new ArrayList<>();
        when(casesPerDayRepository.filter(beginDate, endDate, country)).thenReturn(cases);

        // Act
        CasesResponse result = casesPerDayService.getCases(beginDate, endDate, country);

        // Assert
        assertNotNull(result);
        assertTrue(result.getData().isEmpty());
    }

    @Test
    public void test_getCases_nullBeginDate() {
        // Arrange
        CasesPerDayRepository casesPerDayRepository = mock(CasesPerDayRepository.class);
        CasesPerDayService casesPerDayService = new CasesPerDayService(casesPerDayRepository);
        LocalDate endDate = LocalDate.of(2021, 1, 31);
        List<Long> country = Arrays.asList(1L, 2L, 3L);

        // Act
        CasesResponse result = casesPerDayService.getCases(null, endDate, country);

        // Assert
        assertNotNull(result);
        assertTrue(result.getData().isEmpty());
    }

    @Test
    public void test_getDeaths_nullBeginDate() {
        // Arrange
        CasesPerDayRepository casesPerDayRepository = mock(CasesPerDayRepository.class);
        CasesPerDayService casesPerDayService = new CasesPerDayService(casesPerDayRepository);
        LocalDate endDate = LocalDate.of(2021, 1, 31);
        List<Long> country = Arrays.asList(1L, 2L, 3L);

        // Act
        CasesResponse result = casesPerDayService.getDeaths(null, endDate, country);

        // Assert
        assertNotNull(result);
        assertTrue(result.getData().isEmpty());
    }

    @Test
    public void test_getCases_nullEndDate() {
        // Arrange
        CasesPerDayRepository casesPerDayRepository = mock(CasesPerDayRepository.class);
        CasesPerDayService casesPerDayService = new CasesPerDayService(casesPerDayRepository);
        LocalDate beginDate = LocalDate.of(2021, 1, 1);
        List<Long> country = Arrays.asList(1L, 2L, 3L);

        // Act
        CasesResponse result = casesPerDayService.getCases(beginDate, null, country);

        // Assert
        assertNotNull(result);
        assertTrue(result.getData().isEmpty());
    }

}
