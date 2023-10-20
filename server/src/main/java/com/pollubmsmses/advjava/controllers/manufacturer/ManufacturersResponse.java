package com.pollubmsmses.advjava.controllers.manufacturer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ManufacturersResponse {
    private Map<String, Long> data;
}
