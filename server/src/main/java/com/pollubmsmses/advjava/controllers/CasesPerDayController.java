package com.pollubmsmses.advjava.controllers;

import com.pollubmsmses.advjava.services.CasesPerDayService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CasesPerDayController {
    private final CasesPerDayService casesService;
    @PutMapping("/import/cases")
    public ResponseEntity<Map<String, Object>> importCasesCSV() {
        return casesService.importCasesCSV();
    }
}