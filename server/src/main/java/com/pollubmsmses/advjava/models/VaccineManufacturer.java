package com.pollubmsmses.advjava.models;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VaccineManufacturer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    @OneToMany(fetch = FetchType.LAZY,cascade = CascadeType.ALL,mappedBy = "vaccineManufacturer")
    private List<Vaccination> vaccinations = new ArrayList<>();

    public static VaccineManufacturer of(String name) {
        return new VaccineManufacturer(null,name, new ArrayList<>());
    }
}
