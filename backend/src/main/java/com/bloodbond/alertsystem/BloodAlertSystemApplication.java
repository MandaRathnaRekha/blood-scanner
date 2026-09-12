package com.bloodbond.alertsystem;

import com.bloodbond.alertsystem.entity.Alert;
import com.bloodbond.alertsystem.entity.Donor;
import com.bloodbond.alertsystem.entity.Hospital;
import com.bloodbond.alertsystem.repository.AlertRepository;
import com.bloodbond.alertsystem.repository.DonorRepository;
import com.bloodbond.alertsystem.repository.HospitalRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

/**
 * LifePulse - Emergency Blood Alert System
 * Main Application Class: BloodAlertSystemApplication
 * 
 * @SpringBootApplication encapsulates:
 *   - @Configuration: Tags the class as a source of bean definitions
 *   - @EnableAutoConfiguration: Tells Spring Boot to configure beans based on classpath settings
 *   - @ComponentScan: Automatically scans and detects @Controller, @Service, and @Repository beans
 */
@SpringBootApplication
public class BloodAlertSystemApplication {

    private static final Logger logger = LoggerFactory.getLogger(BloodAlertSystemApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(BloodAlertSystemApplication.class, args);
        logger.info("===============================================================");
        logger.info(" LifePulse Emergency Blood Alert System REST Backend is ACTIVE! ");
        logger.info(" Port: http://localhost:8080                                    ");
        logger.info(" REST Endpoints:                                                ");
        logger.info("   - POST /api/alerts                                           ");
        logger.info("   - GET  /api/alerts/active                                    ");
        logger.info("   - GET  /api/alerts/stats                                     ");
        logger.info("   - GET  /api/donors/search?bloodGroup=...&location=...        ");
        logger.info("   - GET  /api/hospitals/search?location=...&bloodGroup=...     ");
        logger.info("===============================================================");
    }

    /**
     * Automatic Database Seeder Bean:
     * A CommandLineRunner executes automatically right after the application context starts.
     * If the database is empty, it populates initial test donors and hospitals into MySQL.
     */
    @Bean
    public CommandLineRunner initDatabase(DonorRepository donorRepository, 
                                          HospitalRepository hospitalRepository,
                                          AlertRepository alertRepository) {
        return args -> {
            // Check if hospitals already exist
            if (hospitalRepository.count() == 0) {
                logger.info("Seeding initial hospital records into database...");
                hospitalRepository.saveAll(List.of(
                    new Hospital("Apollo Emergency Care & Blood Bank", "A+, A-, B+, O+, O-", "+91 98765 43210", "Central Zone, Main Road", 18),
                    new Hospital("City Red Cross Blood Center", "O-, O+, AB+, B+, B-", "+91 98111 22334", "West Wing, Civil Hospital Road", 24),
                    new Hospital("St. Jude Super Specialty Hospital", "A+, B+, AB+, AB-, O+", "+91 97222 33445", "East Bypass, Near Metro Gate 3", 12),
                    new Hospital("Metro Trauma Center & Blood Repository", "A-, B-, O-, AB-", "+91 99000 11223", "North Corridor, Ring Road", 9),
                    new Hospital("Fortis LifeCare Regional Hospital", "A+, B+, O+, AB+", "+91 98333 44556", "Tech City Campus, Sector 5", 15)
                ));
            }

            // Check if voluntary donors already exist
            if (donorRepository.count() == 0) {
                logger.info("Seeding initial voluntary donor records into database...");
                donorRepository.saveAll(List.of(
                    new Donor("Rahul Sharma", "O-", "+91 98711 22334", "Central Zone, Sector 12", "Available Now", 6),
                    new Donor("Priya Patel", "O+", "+91 98222 33445", "Civil Lines, Near City Hall", "Available Now", 4),
                    new Donor("Amitabh Verma", "A+", "+91 98333 44556", "Greenwood Residency, Flat 402", "Available Now", 9),
                    new Donor("Sneha Mukherjee", "A-", "+91 98444 55667", "East Bypass, Block B", "On Call", 3),
                    new Donor("David D'Souza", "B+", "+91 98555 66778", "Railway Colony, Quarter 18", "Available Now", 5),
                    new Donor("Ananya Iyer", "B-", "+91 98666 77889", "West End Avenue", "On Call", 2),
                    new Donor("Vikram Malhotra", "AB+", "+91 98777 88990", "North Sector, Apartment 201", "Available Now", 8),
                    new Donor("Fatima Sheikh", "AB-", "+91 98888 99001", "Central Market Road", "Available Now", 5),
                    new Donor("Rohan Kulkarni", "O-", "+91 98999 00112", "University Campus Hostel", "Available Now", 7),
                    new Donor("Meera Nair", "A+", "+91 97000 11223", "Tech Park Residences", "Available Now", 3)
                ));
            }

            // Seed a sample baseline emergency alert if empty
            if (alertRepository.count() == 0) {
                logger.info("Seeding initial emergency alert...");
                alertRepository.save(new Alert(
                    "Simran Kaur",
                    "O-",
                    "Metro Hospital ICU, Room 104",
                    "+91 98123 45678",
                    "Urgent requirement for trauma surgery. Need 2 units O- immediately."
                ));
            }

            logger.info("Database verification and seeding complete. Records ready for emergency matching!");
        };
    }
}
