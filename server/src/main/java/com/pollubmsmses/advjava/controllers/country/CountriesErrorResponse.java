package com.pollubmsmses.advjava.controllers.country;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CountriesErrorResponse {
    private Boolean error;
    private String msg;
}
