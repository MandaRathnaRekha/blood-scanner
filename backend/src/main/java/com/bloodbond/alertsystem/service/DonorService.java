package com.bloodbond.alertsystem.service;

import com.bloodbond.alertsystem.entity.Donor;
import com.bloodbond.alertsystem.repository.DonorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * LifePulse - Emergency Blood Alert System
 * Service: DonorService
 * Manages donor registrations, queries, and geographic search filters.
 */
@Service
@Transactional(readOnly = true)
public class DonorService {

    private static final Logger logger = LoggerFactory.getLogger(DonorService.class);

    private final DonorRepository donorRepository;

    public DonorService(DonorRepository donorRepository) {
        this.donorRepository = donorRepository;
    }

    /**
     * Fetches all registered voluntary donors.
     */
    public List<Donor> getAllDonors() {
        return donorRepository.findAll();
    }

    /**
     * Filters donors by an exact blood group (e.g. 'O-').
     */
    public List<Donor> getDonorsByBloodGroup(String bloodGroup) {
        if (bloodGroup == null || bloodGroup.trim().isEmpty()) {
            return getAllDonors();
        }
        return donorRepository.findByBloodGroup(bloodGroup.toUpperCase().trim());
    }

    /**
     * Searches donors by blood group and/or location keyword.
     */
    public List<Donor> searchDonors(String bloodGroup, String location) {
        boolean hasBloodGroup = bloodGroup != null && !bloodGroup.trim().isEmpty();
        boolean hasLocation = location != null && !location.trim().isEmpty();

        if (hasBloodGroup && hasLocation) {
            return donorRepository.findByBloodGroupAndLocationContainingIgnoreCase(
                    bloodGroup.toUpperCase().trim(), 
                    location.trim()
            );
        } else if (hasBloodGroup) {
            return donorRepository.findByBloodGroup(bloodGroup.toUpperCase().trim());
        } else if (hasLocation) {
            return donorRepository.findByLocationContainingIgnoreCase(location.trim());
        } else {
            return donorRepository.findAll();
        }
    }

    /**
     * Registers a new voluntary donor into the MySQL database.
     */
    @Transactional
    public Donor registerDonor(Donor donor) {
        logger.info("Registering new donor: {} | Blood Group: {}", donor.getName(), donor.getBloodGroup());
        return donorRepository.save(donor);
    }

    /**
     * Returns total verified donor count.
     */
    public long getTotalDonorCount() {
        return donorRepository.count();
    }
}
