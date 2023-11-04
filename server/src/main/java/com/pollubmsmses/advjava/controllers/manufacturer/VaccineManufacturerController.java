package com.pollubmsmses.advjava.controllers.manufacturer;

import com.pollubmsmses.advjava.services.VaccineManufacturerService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class VaccineManufacturerController {
    private final VaccineManufacturerService manufacturerService;

    @GetMapping("manufacturers")
    public ResponseEntity<?> index() {
        Map<String, Long> manufacturers = manufacturerService.getSortedManufacturers();

        if(manufacturers != null){
                return ResponseEntity.ok(
                    ManufacturersResponse.builder()
                        .data(manufacturers)
                        .build()
                );
            }
            else{
                return ResponseEntity.internalServerError()
                    .body(
                        ManufacturersErrorResponse.builder()
                            .msg("Couldn't get manufacturers")
                            .error(true)
                            .build()
                    );
            }
    }
}