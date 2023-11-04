package com.pollubmsmses.advjava.controllers.vaccination;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VaccinationsResponse {
    private Map<LocalDate, Long> data;

    // Getter
    public Map<LocalDate, Long> getData() {
        return data;
    }
}
