package com.pollubmsmses.advjava.controllers.vaccination;

import com.pollubmsmses.advjava.services.HeavyImportService;
import com.pollubmsmses.advjava.services.VaccinationService;

import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class VaccinationController {
    private final VaccinationService vaccinationService;
    private final HeavyImportService heavyImportService;

    @PutMapping("/import/vaccinations")
    public ResponseEntity<Map<String, Object>> LEGACTimportVaccinationsCSV() {
        return vaccinationService.LEGACYimportVaccinationsCSV();
    }

    @PutMapping("test")
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
public ResponseEntity<?> getVaccinations(@RequestBody VaccinationRequest request) {
    
    try {
        VaccinationsResponse response = vaccinationService.getVaccinations(
            request.getBegin_date(), 
            request.getEnd_date(), 
            request.getCountries(), 
            request.getManufacturers()
        );
        
        if (response != null) {
            return ResponseEntity.ok(response.getData());
        } else {
            VaccinationsErrorResponse error = new VaccinationsErrorResponse(true, "Couldn't get vaccinations data");
            return ResponseEntity.internalServerError().body(error);
        }
    } catch (Exception e) {
        VaccinationsErrorResponse error = new VaccinationsErrorResponse(true, "An unexpected error occurred: " + e.getMessage());
        return ResponseEntity.internalServerError().body(error);
    }
}
}