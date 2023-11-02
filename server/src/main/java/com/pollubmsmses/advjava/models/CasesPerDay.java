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
        @Index(columnList = "day, country_id",unique = true)
})
public class CasesPerDay {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private LocalDate day;

    private Double newCases;
    private Double newDeaths;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "country_id")
    private Country country;

    public static CasesPerDay of(LocalDate day,Double newCases, Double newDeaths,Country country) {
        return new CasesPerDay(null, day, newCases, newDeaths, country);
    }
}
