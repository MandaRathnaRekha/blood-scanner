package com.bloodbond.alertsystem.repository;

import com.bloodbond.alertsystem.entity.Alert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * LifePulse - Emergency Blood Alert System
 * Repository: AlertRepository
 * Spring Data JPA repository providing CRUD and query operations on the 'alerts' table.
 */
@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {

    /**
     * Finds all alerts matching a specific blood group.
     * Spring Data automatically parses the method name into:
     * SELECT * FROM alerts WHERE blood_group = ?
     */
    List<Alert> findByBloodGroup(String bloodGroup);

    /**
     * Finds all alerts with a given status (e.g. 'ACTIVE') sorted newest first.
     * Generates: SELECT * FROM alerts WHERE status = ? ORDER BY created_at DESC
     */
    List<Alert> findByStatusOrderByCreatedAtDesc(String status);

    /**
     * Returns all historical alerts sorted newest first.
     * Generates: SELECT * FROM alerts ORDER BY created_at DESC
     */
    List<Alert> findAllByOrderByCreatedAtDesc();

    /**
     * Counts how many alerts currently have a given status (e.g., for dashboard KPI metrics).
     * Generates: SELECT COUNT(*) FROM alerts WHERE status = ?
     */
    long countByStatus(String status);
}
