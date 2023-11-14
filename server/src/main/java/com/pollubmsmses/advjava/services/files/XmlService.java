package com.pollubmsmses.advjava.services.files;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pollubmsmses.advjava.services.files.wrappers.DataWrapper;
import com.pollubmsmses.advjava.services.CountryService;
import com.pollubmsmses.advjava.services.files.wrappers.Case;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

@Service
@AllArgsConstructor
@Slf4j
public class XmlService {
    private final ImportService importService;
    private final CountryService countryService;

    private final XmlMapper xmlMapper = (XmlMapper) new XmlMapper()
            .configure(com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator.Feature.WRITE_XML_DECLARATION, true)
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    public String exportData(Object data) {
        try {
            ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

            DataWrapper wrapper = objectMapper.convertValue(data,DataWrapper.class);
            return xmlMapper.writeValueAsString(wrapper);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to parse data to XML", e);
        }
    }

    @Transactional
    public void importData(String xmlData) throws IOException {
        DataWrapper deserialized = xmlMapper.readValue(xmlData, DataWrapper.class);

        countryService.importCountriesCSV();
        importService.importCasesPerDay(deserialized.getCases());
        importService.importVaccinations(deserialized.getVaccinations());
    }


}
