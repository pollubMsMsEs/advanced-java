package com.pollubmsmses.advjava.services;

import com.pollubmsmses.advjava.models.VaccineManufacturer;
import com.pollubmsmses.advjava.repositories.VaccineManufacturerRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class VaccineManufacturerService {
    private final VaccineManufacturerRepository vaccineManufacturerRepository;
    Map<String, Long> unsortedMap = new HashMap<>();

    public Map<String, Long> getSortedManufacturers(){
        try {
            for (VaccineManufacturer manufacturer : vaccineManufacturerRepository.findAll()) {
                unsortedMap.put(manufacturer.getName(), manufacturer.getId());
            }

            Map<String, Long> sortedMap = unsortedMap.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(
                    Map.Entry::getKey, 
                    Map.Entry::getValue, 
                    (e1, e2) -> e1, 
                    LinkedHashMap::new
                ));
            
            return sortedMap;
        } catch (Exception e) {
            return null;
        }
    }
}
