package com.pollubmsmses.advjava.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Country {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;

    @Column(length = 3, unique = true)
    private String alpha3code;

    @OneToMany(cascade = CascadeType.ALL,mappedBy = "country")
    private List<CasesPerDay> casesPerDays = new ArrayList<>();

    public static Country of(String name, String alpha3code){
        return new Country(null,name,alpha3code,new ArrayList<>());
    }
}
