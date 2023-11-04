package com.pollubmsmses.advjava.controllers.casesPerDay;

import com.pollubmsmses.advjava.controllers.vaccination.ErrorResponse;
import com.pollubmsmses.advjava.services.CasesPerDayService;
import com.pollubmsmses.advjava.services.HeavyImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CasesPerDayController {
    private final HeavyImportService heavyImportService;
    private final CasesPerDayService casesPerDayService;
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

    @GetMapping("/cases")
    public ResponseEntity<?> getCases(@RequestBody CasesRequest request) {

        try {
            CasesResponse response = casesPerDayService.getCases(
                    request.getBegin_date(),
                    request.getEnd_date(),
                    request.getCountries()
            );

            if (response != null) {
                return ResponseEntity.ok(response.getData());
            } else {
                ErrorResponse error = new ErrorResponse(true, "Couldn't retrieve cases data");
                return ResponseEntity.internalServerError().body(error);
            }
        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse(true, "An unexpected error occurred: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }
    @GetMapping("/deaths")
    public ResponseEntity<?> getDeaths(@RequestBody CasesRequest request) {

        try {
            CasesResponse response = casesPerDayService.getDeaths(
                    request.getBegin_date(),
                    request.getEnd_date(),
                    request.getCountries()
            );

            if (response != null) {
                return ResponseEntity.ok(response.getData());
            } else {
                ErrorResponse error = new ErrorResponse(true, "Couldn't retrieve deaths data");
                return ResponseEntity.internalServerError().body(error);
            }
        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse(true, "An unexpected error occurred: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }
}

