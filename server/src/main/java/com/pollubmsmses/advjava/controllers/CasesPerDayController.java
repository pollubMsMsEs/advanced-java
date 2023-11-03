package com.pollubmsmses.advjava.controllers;

import com.pollubmsmses.advjava.services.HeavyImportService;
import com.pollubmsmses.advjava.services.VaccinationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CasesPerDayController {
    private final HeavyImportService heavyImportService;
    @PutMapping("/import/cases")
    public ResponseEntity<Map<String, Object>> importCasesCSV() {
        ResponseEntity<Map<String,Object>> response;
        Map<String,Object> body = new HashMap<>();

        try {
            heavyImportService.importCasesPerDayCSV();

            body.put("acknowledged", true);
            response = ResponseEntity.ok(body);
        } catch (Exception e){
            body.put("error", true);
            body.put("msg", e.getMessage());
            response = ResponseEntity.badRequest().body(body);
        }

        return response;
    }
}

