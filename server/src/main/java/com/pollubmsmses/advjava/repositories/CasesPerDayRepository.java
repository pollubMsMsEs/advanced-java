package com.pollubmsmses.advjava.repositories;

import com.pollubmsmses.advjava.models.CasesPerDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CasesPerDayRepository extends JpaRepository<CasesPerDay, Long> {
    @Query("SELECT v FROM CasesPerDay v WHERE (v.day BETWEEN :begin_date AND :end_date) AND v.country.id IN :country")
    List<CasesPerDay> filter(
            @Param("begin_date") LocalDate begin_date,
            @Param("end_date") LocalDate end_date,
            @Param("country") List<Long> country
    );

    @Query("SELECT v FROM CasesPerDay v WHERE v.day = :day AND v.country.id = :country")
    Optional<CasesPerDay> findTopByDayAndCountryId(
            @Param("day") LocalDate day,
            @Param("country") Long country
    );
}
