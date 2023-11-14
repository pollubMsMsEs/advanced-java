package com.pollubmsmses.advjava.services.files;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.pollubmsmses.advjava.models.CasesPerDay;
import com.pollubmsmses.advjava.models.Country;
import com.pollubmsmses.advjava.models.Vaccination;
import com.pollubmsmses.advjava.models.VaccineManufacturer;
import com.pollubmsmses.advjava.repositories.CasesPerDayRepository;
import com.pollubmsmses.advjava.repositories.VaccinationRepository;

import java.time.LocalDate;
import java.util.*;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ExportTests {

    @Mock
    private CasesPerDayRepository casesPerDayRepository;

    @Mock
    private VaccinationRepository vaccinationRepository;

    @InjectMocks
    private ExportService exportService;

    private LocalDate beginDate;
    private LocalDate endDate;
    private List<Long> countryIds;

    @BeforeEach
    void setUp() {
        beginDate = LocalDate.of(2021, 1, 1);
        endDate = LocalDate.of(2021, 12, 31);
        countryIds = Arrays.asList(1L, 2L);
    }

    @Test
    void collectData_returns_correct_data_format_on_call(){
        Country country1 = Country.of("Abc", "ABC");
        Country country2 = Country.of("Def", "DEF");

        CasesPerDay case1 = CasesPerDay.of(LocalDate.of(2021,2,2),222L,111L,country1);
        CasesPerDay case2 = CasesPerDay.of(LocalDate.of(2021,3,3),333L,222L,country2);

        List<CasesPerDay> mockCaseEntities = Arrays.asList(
            case1,
            case2
        );

        when(casesPerDayRepository.filter(eq(beginDate), eq(endDate), eq(countryIds))).thenReturn(mockCaseEntities);


        VaccineManufacturer man1 = VaccineManufacturer.of("manufacturer1");
        VaccineManufacturer man2 = VaccineManufacturer.of("manufacturer2");
        Vaccination vacc1 = Vaccination.of(LocalDate.of(2021,4,4), 11L, country1, man1);
        Vaccination vacc2 = Vaccination.of(LocalDate.of(2021,5,5), 22L, country2, man2);

        List<Vaccination> mockVaccinations = Arrays.asList(
            vacc1,
            vacc2
        );
        
        when(vaccinationRepository.filter(eq(beginDate), eq(endDate), eq(countryIds), anyList())).thenReturn(mockVaccinations);

        Map<String, List<Map<String, Object>>> result = exportService.collectData(beginDate, endDate, countryIds);

        assertTrue(result.containsKey("cases"), "Result should contain 'cases' key");
        assertTrue(result.containsKey("vaccinations"), "Result should contain 'vaccinations' key");

        List<Map<String, Object>> cases = result.get("cases");
        assertNotNull(cases, "Cases list should not be null");
        assertEquals(mockCaseEntities.size(), cases.size(), "Cases list size should match the number of mock CaseEntities");

        List<Map<String, Object>> vaccinations = result.get("vaccinations");
        assertNotNull(vaccinations, "Vaccinations list should not be null");
        assertEquals(mockVaccinations.size(), vaccinations.size(), "Vaccinations list size should match the number of mock Vaccinations");

        if (!cases.isEmpty()) {
            assertTrue(cases.get(0).containsKey("day"), "Case entries should contain 'day'");
            assertTrue(cases.get(0).containsKey("country"), "Case entries should contain 'country'");
            assertTrue(cases.get(0).containsKey("new_cases"), "Case entries should contain 'new_cases'");
            assertTrue(cases.get(0).containsKey("new_deaths"), "Case entries should contain 'new_deaths'");
        }

        if (!vaccinations.isEmpty()) {
            assertTrue(vaccinations.get(0).containsKey("day"), "Vaccination entries should contain 'day'");
            assertTrue(vaccinations.get(0).containsKey("country"), "Vaccination entries should contain 'country'");
            assertTrue(vaccinations.get(0).containsKey("vaccine_manufacturer"), "Vaccination entries should contain 'vaccine_manufacturer'");
            assertTrue(vaccinations.get(0).containsKey("total"), "Vaccination entries should contain 'total'");
        }
    }

    @Test
    void collectData_returns_proper_response_when_given_no_data_on_call(){

        List<CasesPerDay> mockCaseEntities = Collections.emptyList();

        when(casesPerDayRepository.filter(eq(beginDate), eq(endDate), eq(countryIds))).thenReturn(mockCaseEntities);

        List<Vaccination> mockVaccinations = Collections.emptyList();
        
        when(vaccinationRepository.filter(eq(beginDate), eq(endDate), eq(countryIds), anyList())).thenReturn(mockVaccinations);

        Map<String, List<Map<String, Object>>> result = exportService.collectData(beginDate, endDate, countryIds);

        assertTrue(result.containsKey("cases"), "Result should contain 'cases' key with an empty list");
        assertTrue(result.containsKey("vaccinations"), "Result should contain 'vaccinations' key with an empty list");

        List<Map<String, Object>> cases = result.get("cases");
        assertNotNull(cases, "Cases list should not be null");
        assertTrue(cases.isEmpty(), "Cases list should be empty");

        List<Map<String, Object>> vaccinations = result.get("vaccinations");
        assertNotNull(vaccinations, "Vaccinations list should not be null");
        assertTrue(vaccinations.isEmpty(), "Vaccinations list should be empty");
    }
}
