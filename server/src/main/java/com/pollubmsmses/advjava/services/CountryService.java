package com.pollubmsmses.advjava.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pollubmsmses.advjava.models.Country;
import com.pollubmsmses.advjava.repositories.CountryRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@RequiredArgsConstructor
@Service
@Slf4j
public class CountryService {
    private final CountryRepository countryRepository;
    private final String LOCATIONS_PATH = "/importData/locations.csv";
    private final String ISO_MAPPING_PATH = "/importData/countryISOMapping.json";

    private Map<String, String> code3to2;

    @PostConstruct
    public void loadISOMapping() {
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<Map<String, String>> typeReference = new TypeReference<Map<String, String>>() {};

        try (InputStream inputStream = CountryService.class.getResourceAsStream(ISO_MAPPING_PATH)) {
            code3to2 = mapper.readValue(inputStream,typeReference);
            log.info("Country codes read succesfuly, example: POL=" + code3to2.get("POL"));
        }catch (IOException e) {
            e.printStackTrace();
        }

    }

    public boolean importCountriesCSV(){
        List<Country> countries = new ArrayList<>();

        String line;
        String csvSplitBy = ",";

        try (
                InputStream is = CountryService.class.getResourceAsStream(LOCATIONS_PATH);
                BufferedReader br = new BufferedReader(new InputStreamReader(is))
        ) {
            while ((line = br.readLine()) != null) {
                String[] country = line.split(csvSplitBy);
                if (
                        country[1].length() != 3
                                || country[0].equals("location")
                                || countryRepository.findFirstByName(country[0]) != null
                ) {
                    continue;
                }

                countries.add(Country.of(country[0],country[1]));
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        countryRepository.saveAll(countries);
        return true;
    }

    public Map<String, String> LEGACYgetCountriesCSV() {
        Map<String, String> countries = new TreeMap<>();

        String line;
        String csvSplitBy = ",";

        try (InputStream is = CountryService.class.getResourceAsStream(LOCATIONS_PATH);

                BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            while ((line = br.readLine()) != null) {
                String[] country = line.split(csvSplitBy);
                if (country[1].length() != 3) {
                    continue;
                }
                countries.put(country[0], country[1]);
            }
        } catch (IOException e) {
            e.printStackTrace();
            countries = null;
        }

        if (countries != null) {
            countries.remove("location");
        }

        return countries;
    }
    public Map<String, Long> getAllAsMap(){
        try {
            return countryRepository
                    .findAll()
                    .stream()
                    .collect(
                            Collectors.toMap(
                                    Country::getName,
                                    Country::getId,
                                    (oldValue,newValue) -> oldValue,
                                    TreeMap::new
                            )
                    );
        } catch (Exception e) {
            return null;
        }
    }

    public String getCountryFlag(Long countryId) throws Exception {
        Country country = countryRepository.getCountryById(countryId);
        String countryCode = country.getAlpha3code();
        String code2 = code3to2.get(countryCode);

        String body = createSoapRequest(code2);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_XML);

        HttpEntity<String> request = new HttpEntity<>(body, headers);

        String url = "http://webservices.oorsprong.org/websamples.countryinfo/CountryInfoService.wso";
        
        RestTemplate restTemplate = new RestTemplate();
        
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
        
        return extractFlagUrlFromResponse(response.getBody());
    }

    private String createSoapRequest(String code2) {
        return "<?xml version='1.0' encoding='utf-8'?>" +
                "<soap:Envelope xmlns:soap='http://schemas.xmlsoap.org/soap/envelope/'>" +
                "<soap:Body>" +
                "<CountryFlag xmlns='http://www.oorsprong.org/websamples.countryinfo'>" +
                "<sCountryISOCode>" + code2 + "</sCountryISOCode>" +
                "</CountryFlag>" +
                "</soap:Body>" +
                "</soap:Envelope>";
    }

    private String extractFlagUrlFromResponse(String response) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        org.w3c.dom.Document document = builder.parse(new InputSource(new StringReader(response)));

        document.getDocumentElement().normalize();

        NodeList nodeList = document.getElementsByTagName("m:CountryFlagResult");
        if (nodeList.getLength() > 0) {
            Node node = nodeList.item(0);
            return node.getTextContent();
        } else {
            throw new Exception("Cannot find the CountryFlagResult in the SOAP response.");
        }
    }
}
