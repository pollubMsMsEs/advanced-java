package com.pollubmsmses.advjava.models;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(indexes = {
        @Index(columnList = "day, country_id, vaccine_manufacturer_id",unique = true)
})
public class Vaccination {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private LocalDate day;
    
    private Long total;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "country_id")
    private Country country;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "vaccine_manufacturer_id")
    private VaccineManufacturer vaccineManufacturer;

    public static Vaccination of(LocalDate day, Long total, Country country, VaccineManufacturer vaccineManufacturer) {
        return new Vaccination(null, day, total, country, vaccineManufacturer);
    }
}
