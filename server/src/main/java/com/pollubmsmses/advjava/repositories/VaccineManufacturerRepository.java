package com.pollubmsmses.advjava.repositories;

import com.pollubmsmses.advjava.models.VaccineManufacturer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VaccineManufacturerRepository extends JpaRepository<VaccineManufacturer, Long> {
}