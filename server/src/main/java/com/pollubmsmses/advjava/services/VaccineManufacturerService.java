package com.pollubmsmses.advjava.services;

import com.pollubmsmses.advjava.models.VaccineManufacturer;
import com.pollubmsmses.advjava.repositories.VaccineManufacturerRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class VaccineManufacturerService {
    private final VaccineManufacturerRepository vaccineManufacturerRepository;

    public Map<String, Long> getSortedManufacturers(){
        try {
            return vaccineManufacturerRepository.findAll().stream().collect(Collectors.toMap(
                    VaccineManufacturer::getName,
                    VaccineManufacturer::getId,
                    (e1, e2) -> e1,
                    LinkedHashMap::new
            ));
        } catch (Exception e) {
            return null;
        }
    }

    public VaccineManufacturer getManufacturerAndAddIfMissing(String manufacturerName, Map<String, VaccineManufacturer> manufacturers){
        if (!manufacturers.containsKey(manufacturerName)) {
            VaccineManufacturer manufacturer = vaccineManufacturerRepository.findFirstByName(manufacturerName);
            if (manufacturer == null) {
                manufacturer = VaccineManufacturer.of(manufacturerName);
                vaccineManufacturerRepository.saveAndFlush(manufacturer);
            }
            manufacturers.put(manufacturerName, manufacturer);
            return manufacturer;
        } else {
            return manufacturers.get(manufacturerName);
        }
    }
}
