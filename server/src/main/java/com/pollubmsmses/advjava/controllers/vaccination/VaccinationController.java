package com.pollubmsmses.advjava.controllers.vaccination;

import com.pollubmsmses.advjava.controllers.responses.ErrorResponse;
import com.pollubmsmses.advjava.services.HeavyImportService;
import com.pollubmsmses.advjava.services.VaccinationService;

import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class VaccinationController {
    private final VaccinationService vaccinationService;
    private final HeavyImportService heavyImportService;

    @PutMapping("/import/vaccinations")
    public ResponseEntity<Map<String,Object>> importVaccinationsCSV(){
        ResponseEntity<Map<String,Object>> response;
        Map<String,Object> body = new HashMap<>();

        try {
            heavyImportService.importVaccinationsCSV();

            body.put("acknowledged", true);
            response = ResponseEntity.ok(body);
        } catch (Exception e){
            body.put("error", true);
            body.put("msg", e.getMessage());
            response = ResponseEntity.badRequest().body(body);
        }

        return response;
    }
    
    @GetMapping("/vaccinations")
public ResponseEntity<?> getVaccinations(@RequestParam() LocalDate begin_date, @RequestParam() LocalDate end_date, @RequestParam() List<Long> countries) {
    
    try {
        VaccinationsResponse response = vaccinationService.getVaccinations(
            begin_date,
            end_date,
            countries,
            new ArrayList<>()
        );
        
        if (response != null) {
            return ResponseEntity.ok(response.getData());
        } else {
            ErrorResponse error = new ErrorResponse(true, "Couldn't get vaccinations data");
            return ResponseEntity.internalServerError().body(error);
        }
    } catch (Exception e) {
        ErrorResponse error = new ErrorResponse(true, "An unexpected error occurred: " + e.getMessage());
        return ResponseEntity.internalServerError().body(error);
    }
}
}