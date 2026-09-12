package com.bloodbond.alertsystem.service;

import com.bloodbond.alertsystem.dto.AlertRequestDto;
import com.bloodbond.alertsystem.entity.Alert;
import com.bloodbond.alertsystem.entity.Donor;
import com.bloodbond.alertsystem.entity.Hospital;
import com.bloodbond.alertsystem.repository.AlertRepository;
import com.bloodbond.alertsystem.repository.DonorRepository;
import com.bloodbond.alertsystem.repository.HospitalRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * LifePulse - Emergency Blood Alert System
 * Service: AlertService
 * Orchestrates emergency alert persistence, clinical blood group matching,
 * and hospital/donor resource coordination.
 */
@Service
public class AlertService {

    private static final Logger logger = LoggerFactory.getLogger(AlertService.class);

    private final AlertRepository alertRepository;
    private final DonorRepository donorRepository;
    private final HospitalRepository hospitalRepository;

    // Clinical red-blood-cell compatibility rules
    private static final Map<String, List<String>> COMPATIBILITY_MAP = new HashMap<>();

    static {
        COMPATIBILITY_MAP.put("O-", List.of("O-"));
        COMPATIBILITY_MAP.put("O+", List.of("O+", "O-"));
        COMPATIBILITY_MAP.put("A-", List.of("A-", "O-"));
        COMPATIBILITY_MAP.put("A+", List.of("A+", "A-", "O+", "O-"));
        COMPATIBILITY_MAP.put("B-", List.of("B-", "O-"));
        COMPATIBILITY_MAP.put("B+", List.of("B+", "B-", "O+", "O-"));
        COMPATIBILITY_MAP.put("AB-", List.of("AB-", "A-", "B-", "O-"));
        COMPATIBILITY_MAP.put("AB+", List.of("AB+", "AB-", "A+", "A-", "B+", "B-", "O+", "O-"));
    }

    /**
     * Constructor Injection (Industry Standard):
     * Spring automatically provides the repository beans at application startup.
     */
    public AlertService(AlertRepository alertRepository, 
                        DonorRepository donorRepository, 
                        HospitalRepository hospitalRepository) {
        this.alertRepository = alertRepository;
        this.donorRepository = donorRepository;
        this.hospitalRepository = hospitalRepository;
    }

    /**
     * Broadcasts a new emergency alert, saves it to the MySQL database,
     * and logs the critical event.
     * 
     * @param requestDto The incoming validated request payload
     * @return The persisted Alert entity containing the database-generated ID
     */
    @Transactional
    public Alert createAlert(AlertRequestDto requestDto) {
        logger.warn("CRITICAL ALERT INITIATED for Patient: {} | Blood Group: {} | Location: {}",
                requestDto.getPatientName(), requestDto.getBloodGroup(), requestDto.getLocation());

        // Map DTO to Entity
        Alert alert = new Alert(
                requestDto.getPatientName().trim(),
                requestDto.getBloodGroup().toUpperCase().trim(),
                requestDto.getLocation().trim(),
                requestDto.getContactNumber().trim(),
                requestDto.getMessage().trim()
        );

        // Save into MySQL database via Spring Data JPA
        Alert savedAlert = alertRepository.save(alert);
        logger.info("Emergency Alert saved successfully with ID: {}", savedAlert.getId());

        return savedAlert;
    }

    /**
     * Resolves all compatible blood groups for a given patient recipient blood group.
     */
    public List<String> getCompatibleBloodGroups(String recipientGroup) {
        if (recipientGroup == null) return Collections.emptyList();
        String group = recipientGroup.toUpperCase().trim();
        return COMPATIBILITY_MAP.getOrDefault(group, List.of(group));
    }

    /**
     * Finds matching registered voluntary donors based on blood compatibility.
     */
    public List<Donor> findMatchingDonors(String bloodGroup) {
        List<String> compatibleGroups = getCompatibleBloodGroups(bloodGroup);
        return donorRepository.findByBloodGroupIn(compatibleGroups);
    }

    /**
     * Finds hospitals that stock the patient's requested blood group or compatible groups.
     */
    public List<Hospital> findMatchingHospitals(String bloodGroup) {
        List<String> compatibleGroups = getCompatibleBloodGroups(bloodGroup);
        Set<Hospital> matchedSet = new LinkedHashSet<>();

        for (String group : compatibleGroups) {
            matchedSet.addAll(hospitalRepository.findByBloodGroupAvailableContaining(group));
        }

        return new ArrayList<>(matchedSet);
    }

    /**
     * Fetches all active emergency alerts sorted newest first.
     */
    public List<Alert> getActiveAlerts() {
        return alertRepository.findByStatusOrderByCreatedAtDesc("ACTIVE");
    }

    /**
     * Returns system-wide statistics for the dashboard.
     */
    public Map<String, Object> getDashboardStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("activeAlerts", alertRepository.countByStatus("ACTIVE"));
        stats.put("totalDonors", donorRepository.count());
        stats.put("totalHospitals", hospitalRepository.count());
        return stats;
    }
}
