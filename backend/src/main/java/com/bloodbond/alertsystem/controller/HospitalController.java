package com.bloodbond.alertsystem.controller;

import com.bloodbond.alertsystem.dto.ApiResponse;
import com.bloodbond.alertsystem.entity.Hospital;
import com.bloodbond.alertsystem.repository.HospitalRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * LifePulse - Emergency Blood Alert System
 * Controller: HospitalController
 * Exposes REST endpoints for querying, searching, and registering hospitals.
 */
@RestController
@RequestMapping("/api/hospitals")
@CrossOrigin(origins = "*")
public class HospitalController {

    private final HospitalRepository hospitalRepository;

    public HospitalController(HospitalRepository hospitalRepository) {
        this.hospitalRepository = hospitalRepository;
    }

    /**
     * REST Endpoint: GET /api/hospitals
     * Retrieves all registered hospitals and blood repositories.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<Hospital>>> getAllHospitals() {
        List<Hospital> hospitals = hospitalRepository.findAll();
        return ResponseEntity.ok(ApiResponse.success("Hospitals fetched successfully.", hospitals));
    }

    /**
     * REST Endpoint: GET /api/hospitals/search?location=...&bloodGroup=...
     * Searches hospitals by location or blood group availability.
     * Fulfills Phase 6: Search hospitals by location.
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<Hospital>>> searchHospitals(
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String bloodGroup) {

        List<Hospital> results;

        if (location != null && !location.trim().isEmpty()) {
            results = hospitalRepository.findByLocationContainingIgnoreCase(location.trim());
        } else if (bloodGroup != null && !bloodGroup.trim().isEmpty()) {
            results = hospitalRepository.findByBloodGroupAvailableContaining(bloodGroup.trim());
        } else {
            results = hospitalRepository.findAll();
        }

        return ResponseEntity.ok(ApiResponse.success("Hospital search query completed.", results));
    }

    /**
     * REST Endpoint: POST /api/hospitals
     * Onboards a new hospital or blood bank into the network.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Hospital>> registerHospital(@RequestBody Hospital hospital) {
        Hospital saved = hospitalRepository.save(hospital);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Hospital registered successfully.", saved));
    }
}
