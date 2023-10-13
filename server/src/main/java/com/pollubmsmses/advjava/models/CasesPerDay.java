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
public class CasesPerDay {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private LocalDate day;

    @ManyToOne
    @JoinColumn(name = "country_id", nullable = false)
    private Country country;

    public static CasesPerDay of(Country country){
        return new CasesPerDay(null,LocalDate.now(),country);
    }
}
