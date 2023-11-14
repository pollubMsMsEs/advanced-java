package com.pollubmsmses.advjava.services.files;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.pollubmsmses.advjava.services.CountryService;
import com.pollubmsmses.advjava.services.files.wrappers.Case;
import com.pollubmsmses.advjava.services.files.wrappers.DataWrapper;
import com.pollubmsmses.advjava.services.files.wrappers.VaccinationWrapper;

public class XmlTests {
    @Mock
    private ImportService importService;

    @Mock
    private CountryService countryService;

    @InjectMocks
    private XmlService xmlService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void exportData_SuccessfulConversion() throws IOException {
        VaccinationWrapper vacc1 = new VaccinationWrapper("Afghanistan", 222L, "Vaccination1", LocalDate.of(2021, 2, 2));
        List<VaccinationWrapper> vaccinations = Arrays.asList(vacc1);
        Case case1 = new Case("Afghanistan", 222L, 111L, LocalDate.of(2021, 2, 2));
        List<Case> cases = Arrays.asList(case1);
        DataWrapper data1 = new DataWrapper(cases, vaccinations);

        String expectedXml = "<?xml version='1.0' encoding='UTF-8'?>" +
            "<DataWrapper>" +
            "<id/>" +
            "<cases>" +
            "<cases>" +
            "<country>Afghanistan</country>" +
            "<new_cases>222</new_cases>" +
            "<new_deaths>111</new_deaths>" +
            "<day>2021-02-02</day>" +
            "</cases>" +
            "</cases>" +
            "<vaccinations>" +
            "<vaccinations>" +
            "<country>Afghanistan</country>" +
            "<total>222</total>" +
            "<vaccine_manufacturer>Vaccination1</vaccine_manufacturer>" +
            "<day>2021-02-02</day>" +
            "</vaccinations>" +
            "</vaccinations>" +
            "</DataWrapper>";

        String resultXml = xmlService.exportData(data1);

        assertEquals(expectedXml, resultXml);
    }
    
    @Test
    void importData_SuccessfulImport() throws IOException {
        String xmlData = "<?xml version='1.0' encoding='UTF-8'?>" +
            "<DataWrapper>" +
            "<id/>" +
            "<cases>" +
            "<cases>" +
            "<country>Afghanistan</country>" +
            "<new_cases>222</new_cases>" +
            "<new_deaths>111</new_deaths>" +
            "<day>2021-02-02</day>" +
            "</cases>" +
            "</cases>" +
            "<vaccinations>" +
            "<vaccinations>" +
            "<country>Afghanistan</country>" +
            "<total>222</total>" +
            "<vaccine_manufacturer>Vaccination1</vaccine_manufacturer>" +
            "<day>2021-02-02</day>" +
            "</vaccinations>" +
            "</vaccinations>" +
            "</DataWrapper>";

        xmlService.importData(xmlData);

        verify(countryService).importCountriesCSV();
        verify(importService, atLeastOnce()).importCasesPerDay(anyList());
        verify(importService, atLeastOnce()).importVaccinations(anyList());
    }
    
    @Test
    void importData_IOException() {
        String invalidJsonData = "invalid json";

        assertThrows(IOException.class, () -> xmlService.importData(invalidJsonData));
    }
}
