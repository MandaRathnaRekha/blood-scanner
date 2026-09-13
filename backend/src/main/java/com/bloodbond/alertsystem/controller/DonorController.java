package com.bloodbond.alertsystem.controller;

import com.bloodbond.alertsystem.dto.ApiResponse;
import com.bloodbond.alertsystem.entity.Donor;
import com.bloodbond.alertsystem.service.DonorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * LifePulse - Emergency Blood Alert System
 * Controller: DonorController
 * Exposes REST endpoints for querying, searching, and registering voluntary donors.
 */
@RestController
@RequestMapping("/api/donors")
public class DonorController {

    private final DonorService donorService;

    public DonorController(DonorService donorService) {
        this.donorService = donorService;
    }

    /**
     * REST Endpoint: GET /api/donors
     * Retrieves all registered voluntary blood donors.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<Donor>>> getAllDonors() {
        List<Donor> donors = donorService.getAllDonors();
        return ResponseEntity.ok(ApiResponse.success("All donors fetched successfully.", donors));
    }

    /**
     * REST Endpoint: GET /api/donors/search?bloodGroup=...&location=...
     * Searches donors by blood group and/or geographic location.
     * Fulfills Phase 6: Search donors by blood group & location.
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<Donor>>> searchDonors(
            @RequestParam(required = false) String bloodGroup,
            @RequestParam(required = false) String location) {

        List<Donor> matchingDonors = donorService.searchDonors(bloodGroup, location);
        return ResponseEntity.ok(ApiResponse.success("Donor search query completed.", matchingDonors));
    }

    /**
     * REST Endpoint: POST /api/donors
     * Onboards a new voluntary blood donor into the emergency registry.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Donor>> registerDonor(@RequestBody Donor donor) {
        Donor savedDonor = donorService.registerDonor(donor);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Donor registered successfully.", savedDonor));
    }
}
