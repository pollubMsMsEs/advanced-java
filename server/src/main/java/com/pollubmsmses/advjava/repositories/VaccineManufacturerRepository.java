package com.pollubmsmses.advjava.repositories;

import com.pollubmsmses.advjava.models.VaccineManufacturer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VaccineManufacturerRepository extends JpaRepository<VaccineManufacturer, Long> {
    Optional<VaccineManufacturer> findFirstByName(String name);
}