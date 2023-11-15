package com.pollubmsmses.advjava.controllers.casesPerDay;

import com.pollubmsmses.advjava.controllers.responses.ErrorResponse;
import com.pollubmsmses.advjava.services.CasesPerDayService;
import com.pollubmsmses.advjava.services.files.ImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CasesPerDayController {
    private final ImportService importService;
    private final CasesPerDayService casesPerDayService;

    @Secured("ADMIN")
    @PutMapping("/import/cases")
    public ResponseEntity<Map<String, Object>> importCasesCSV() {
        ResponseEntity<Map<String,Object>> response;
        Map<String,Object> body = new HashMap<>();

        try {
            importService.importCasesPerDayCSV();

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
    public ResponseEntity<?> getCases(@RequestParam() LocalDate begin_date, @RequestParam() LocalDate end_date, @RequestParam() List<Long> countries) {

        try {
            CasesResponse response = casesPerDayService.getCases(
                    begin_date,
                    end_date,
                    countries
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
    public ResponseEntity<?> getDeaths(@RequestParam() LocalDate begin_date, @RequestParam() LocalDate end_date, @RequestParam() List<Long> countries) {

        try {
            CasesResponse response = casesPerDayService.getDeaths(
                    begin_date,
                    end_date,
                    countries
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

