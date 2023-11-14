package com.pollubmsmses.advjava.services.files;

import com.pollubmsmses.advjava.models.CasesPerDay;
import com.pollubmsmses.advjava.models.Country;
import com.pollubmsmses.advjava.services.CountryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.time.LocalDate;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class JsonTests {

    @Mock
    private ImportService importService;

    @Mock
    private CountryService countryService;

    @InjectMocks
    private JsonService jsonService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void exportData_SuccessfulConversion() throws IOException {
        Country country1 = Country.of("Abc", "ABC");
        CasesPerDay case1 = CasesPerDay.of(LocalDate.of(2021,2,2),222L,111L,country1);

        String expectedJson = "{\"id\":null,\"day\":\"2021-02-02\",\"newCases\":222,\"newDeaths\":111,"+
            "\"country\":{\"id\":null,\"name\":\"Abc\",\"alpha3code\":\"ABC\",\"casesPerDays\":[],\"vaccinations\":[]}}";

        String resultJson = jsonService.exportData(case1);

        assertEquals(expectedJson, resultJson);
    }
    
    @Test
    void importData_SuccessfulImport() throws IOException {
        String jsonData = "{\"cases\":[{\"country\":\"Afghanistan\",\"new_cases\":222,\"new_deaths\":111,\"day\":\"2021-02-02\"},"+
            "{\"country\":\"Albania\",\"new_cases\":333,\"new_deaths\":222,\"day\":\"2021-03-03\"}],"+
            "\"vaccinations\":[{\"country\":\"Afghanistan\",\"total\":222,\"vaccine_manufacturer\":"+
            "\"Vaccination1\",\"day\":\"2021-02-02\"},{\"country\":\"Albania\",\"total\":333,"+
            "\"vaccine_manufacturer\":\"Vaccination2\",\"day\":\"2021-03-03\"}]}";

        jsonService.importData(jsonData);

        verify(countryService).importCountriesCSV();
        verify(importService, atLeastOnce()).importCasesPerDay(anyList());
        verify(importService, atLeastOnce()).importVaccinations(anyList());
    }
    
    @Test
    void importData_IOException() {
        String invalidJsonData = "invalid json";

        assertThrows(IOException.class, () -> jsonService.importData(invalidJsonData));
    }
}

