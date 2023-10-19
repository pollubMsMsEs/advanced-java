package com.pollubmsmses.advjava.controllers.country;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CountriesResponse {
    private Map<String, Long> data;
}
