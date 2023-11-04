package com.pollubmsmses.advjava.controllers.manufacturer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ManufacturersErrorResponse {
    private Boolean error;
    private String msg;
}
