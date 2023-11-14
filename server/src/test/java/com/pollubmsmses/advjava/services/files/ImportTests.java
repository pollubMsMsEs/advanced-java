package com.pollubmsmses.advjava.services.files;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ParameterizedPreparedStatementSetter;

import com.pollubmsmses.advjava.models.Country;
import com.pollubmsmses.advjava.models.VaccineManufacturer;
import com.pollubmsmses.advjava.repositories.CasesPerDayRepository;
import com.pollubmsmses.advjava.repositories.CountryRepository;
import com.pollubmsmses.advjava.repositories.VaccinationRepository;
import com.pollubmsmses.advjava.repositories.VaccineManufacturerRepository;
import com.pollubmsmses.advjava.services.CasesPerDayService;
import com.pollubmsmses.advjava.services.CountryService;
import com.pollubmsmses.advjava.services.VaccineManufacturerService;
import com.pollubmsmses.advjava.services.files.wrappers.Case;
import com.pollubmsmses.advjava.services.files.wrappers.VaccinationWrapper;

import java.time.LocalDate;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ImportTests {
    @Mock
    private CountryRepository countryRepository;

    @Mock
    private CasesPerDayRepository casesPerDayRepository;

    @Mock
    private CasesPerDayService casesPerDayService;

    @Mock
    private CountryService countryService;

    @Mock
    private VaccineManufacturerService vaccineManufacturerService;

    @Mock
    private VaccineManufacturerRepository vaccineManufacturerRepository;

    @Mock
    private VaccinationRepository vaccinationRepository;

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private ImportService importService;

    @BeforeEach
    void setUp() {
    }

    @Test
    void importVaccinationsCSV_SuccessfulImport() throws Exception {
        when(countryService.importCountriesCSV()).thenReturn(true);

        importService.importVaccinationsCSV();

        verify(countryService).importCountriesCSV();

        verify(jdbcTemplate).execute(anyString());
        verify(jdbcTemplate).batchUpdate(anyString(), anyList(), anyInt(), any(ParameterizedPreparedStatementSetter.class));
    }

    @Test
    void importVaccinationsCSV_FailsWhenCountriesNotImported() {
        when(countryService.importCountriesCSV()).thenReturn(false);

        Exception exception = assertThrows(Exception.class, () -> importService.importVaccinationsCSV());

        assertEquals("Couldn't open countries CSV", exception.getMessage());

        verifyNoInteractions(vaccineManufacturerService);
        verifyNoMoreInteractions(jdbcTemplate);
    }

    @Test
    void importCasesPerDayCSV_SuccessfulImport() throws Exception {
        when(countryService.importCountriesCSV()).thenReturn(true);

        importService.importCasesPerDayCSV();

        verify(countryService).importCountriesCSV();

        verify(jdbcTemplate).execute(anyString());
        verify(jdbcTemplate).batchUpdate(anyString(), anyList(), anyInt(), any(ParameterizedPreparedStatementSetter.class));
    }

    @Test
    void importCasesPerDayCSV_FailsWhenCountriesNotImported() {
        when(countryService.importCountriesCSV()).thenReturn(false);

        Exception exception = assertThrows(Exception.class, () -> importService.importCasesPerDayCSV());

        assertEquals("Couldn't open countries CSV", exception.getMessage());

        verifyNoInteractions(vaccineManufacturerService);
        verifyNoMoreInteractions(jdbcTemplate);
    }

    @Test
    void importCasesPerDay_SuccessfulImport() {
        Country country1 = Country.of("Afghanistan", "AFG");
        Country country2 = Country.of("Albania", "ALB");

        when(countryService.getCountryByNameOrCreateCustom(anyString(), isNull())).thenReturn(country1);
        when(countryService.getCountryByNameOrCreateCustom(anyString(), isNull())).thenReturn(country2);

        Case case1 = new Case("Afghanistan", 222L, 111L, LocalDate.of(2021, 2, 2));
        Case case2 = new Case("Albania", 333L, 222L, LocalDate.of(2021, 3, 3));

        List<Case> cases = Arrays.asList(case1, case2);

        when(casesPerDayRepository.findTopByDayAndCountryId(any(LocalDate.class), isNull()))
            .thenReturn(Optional.empty());

        importService.importCasesPerDay(cases);

        verify(casesPerDayRepository, times(2)).findTopByDayAndCountryId(any(LocalDate.class), isNull());
        verify(casesPerDayRepository).saveAll(anyList());
        verifyNoMoreInteractions(jdbcTemplate);
    }

    @Test
    void importVaccinations_SuccessfulImport() {
        Country country1 = Country.of("Afghanistan", "AFG");
        Country country2 = Country.of("Albania", "ALB");

        when(countryService.getCountryByNameOrCreateCustom(anyString(), isNull())).thenReturn(country1);
        when(countryService.getCountryByNameOrCreateCustom(anyString(), isNull())).thenReturn(country2);

        VaccinationWrapper vacc1 = new VaccinationWrapper("Afghanistan", 222L, "Vaccination1", LocalDate.of(2021, 2, 2));
        VaccinationWrapper vacc2 = new VaccinationWrapper("Albania", 333L, "Vaccination2", LocalDate.of(2021, 3, 3));

        List<VaccinationWrapper> vaccinations = Arrays.asList(vacc1, vacc2);

        when(vaccineManufacturerRepository.findFirstByName(anyString()))
            .thenReturn(Optional.empty());

        when(vaccinationRepository.findTopByDayAndCountryIdAndVaccineManufacturerId(any(LocalDate.class), isNull(), isNull()))
            .thenReturn(Optional.empty());

        importService.importVaccinations(vaccinations);

        verify(vaccineManufacturerRepository, times(2)).findFirstByName(anyString());
        verify(vaccineManufacturerRepository, times(2)).saveAndFlush(any(VaccineManufacturer.class));
        verify(vaccinationRepository, times(2)).findTopByDayAndCountryIdAndVaccineManufacturerId(any(LocalDate.class), isNull(), isNull());
        verify(vaccinationRepository).saveAll(anyList());
        verifyNoMoreInteractions(jdbcTemplate);
    }
}
