package com.bloodbond.alertsystem;

import com.bloodbond.alertsystem.repository.AlertRepository;
import com.bloodbond.alertsystem.repository.DonorRepository;
import com.bloodbond.alertsystem.repository.HospitalRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifies that the Spring Boot application context loads cleanly
 * and the database seeder populates initial test data.
 */
@SpringBootTest
class BloodAlertSystemApplicationTests {

    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private DonorRepository donorRepository;

    @Autowired
    private AlertRepository alertRepository;

    @Test
    @DisplayName("Application context loads and initial seed data is populated")
    void contextLoads() {
        // Verified database seeder ran successfully
        assertTrue(hospitalRepository.count() >= 5, "Hospitals should be seeded");
        assertTrue(donorRepository.count() >= 10, "Donors should be seeded");
        assertTrue(alertRepository.count() >= 1, "Alerts should be seeded");
    }
}
