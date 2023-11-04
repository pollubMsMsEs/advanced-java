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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate day;

    private Long newCases;
    private Long newDeaths;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id", nullable = false)
    private Country country;

    public static CasesPerDay of(LocalDate day,Long newCases, Long newDeaths,Country country) {
        return new CasesPerDay(null, day, newCases, newDeaths, country);
    }
}
