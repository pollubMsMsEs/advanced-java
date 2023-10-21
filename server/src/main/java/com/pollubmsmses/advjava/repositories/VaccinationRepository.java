package com.pollubmsmses.advjava.repositories;

import com.pollubmsmses.advjava.models.Vaccination;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VaccinationRepository extends JpaRepository<Vaccination, Long> {
    @Query("SELECT v FROM Vaccination v WHERE (v.day BETWEEN :begin_date AND :end_date) AND v.country.id IN :country AND v.vaccineManufacturer.id IN :vaccine_manufacturer")
    List<Vaccination> filter(
        @Param("begin_date") LocalDate begin_date,
        @Param("end_date") LocalDate end_date,
        @Param("country") List<Long> country,
        @Param("vaccine_manufacturer") List<Long> vaccineManufacturer
    );
}
