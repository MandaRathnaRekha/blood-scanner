package com.bloodbond.alertsystem.repository;

import com.bloodbond.alertsystem.entity.Hospital;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * LifePulse - Emergency Blood Alert System
 * Repository: HospitalRepository
 * Spring Data JPA repository providing query operations on the 'hospitals' table.
 */
@Repository
public interface HospitalRepository extends JpaRepository<Hospital, Long> {

    /**
     * Finds hospitals that list a specific blood group in their available stock string.
     * Generates: SELECT * FROM hospitals WHERE blood_group_available LIKE '%' || ? || '%'
     */
    List<Hospital> findByBloodGroupAvailableContaining(String bloodGroup);

    /**
     * Searches hospitals by location name or landmark (case-insensitive).
     * Generates: SELECT * FROM hospitals WHERE UPPER(location) LIKE UPPER('%' || ? || '%')
     */
    List<Hospital> findByLocationContainingIgnoreCase(String location);

    /**
     * Searches hospitals by name (case-insensitive).
     */
    List<Hospital> findByHospitalNameContainingIgnoreCase(String hospitalName);
}
