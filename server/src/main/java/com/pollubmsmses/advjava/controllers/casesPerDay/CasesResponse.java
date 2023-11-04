package com.pollubmsmses.advjava.controllers.casesPerDay;

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

public class CasesResponse {

        private Map<LocalDate, Long> data;
        public Map<LocalDate, Long> getData() {
            return data;
        }

}
