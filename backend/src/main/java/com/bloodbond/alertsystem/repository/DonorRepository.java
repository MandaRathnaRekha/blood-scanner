package com.bloodbond.alertsystem.repository;

import com.bloodbond.alertsystem.entity.Donor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

/**
 * LifePulse - Emergency Blood Alert System
 * Repository: DonorRepository
 * Spring Data JPA repository providing query operations for voluntary donors.
 */
@Repository
public interface DonorRepository extends JpaRepository<Donor, Long> {

    /**
     * Finds all donors having an exact blood group.
     * Generates: SELECT * FROM donors WHERE blood_group = ?
     */
    List<Donor> findByBloodGroup(String bloodGroup);

    /**
     * Finds donors whose blood group is in a compatible collection (e.g. ['A+', 'A-', 'O+', 'O-']).
     * Generates: SELECT * FROM donors WHERE blood_group IN (?, ?, ?, ?)
     */
    List<Donor> findByBloodGroupIn(Collection<String> bloodGroups);

    /**
     * Search donors by geographical location or neighborhood substring (case-insensitive).
     * Generates: SELECT * FROM donors WHERE UPPER(location) LIKE UPPER('%' || ? || '%')
     */
    List<Donor> findByLocationContainingIgnoreCase(String location);

    /**
     * Combines blood group match with location search.
     */
    List<Donor> findByBloodGroupAndLocationContainingIgnoreCase(String bloodGroup, String location);
}
