package com.pollubmsmses.advjava.controllers.vaccination;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VaccinationsErrorResponse {
    private Boolean error;
    private String msg;
}
