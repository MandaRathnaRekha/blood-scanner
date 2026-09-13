package com.bloodbond.alertsystem.controller;

import com.bloodbond.alertsystem.dto.AlertRequestDto;
import com.bloodbond.alertsystem.dto.ApiResponse;
import com.bloodbond.alertsystem.entity.Alert;
import com.bloodbond.alertsystem.entity.Donor;
import com.bloodbond.alertsystem.entity.Hospital;
import com.bloodbond.alertsystem.service.AlertService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * LifePulse - Emergency Blood Alert System
 * Controller: AlertController
 * Exposes REST endpoints for broadcasting emergency alerts, 
 * retrieving active broadcasts, and querying matching blood resources.
 */
@RestController
@RequestMapping("/api/alerts")
public class AlertController {

    private static final Logger logger = LoggerFactory.getLogger(AlertController.class);

    private final AlertService alertService;
    private final com.bloodbond.alertsystem.service.NotificationService notificationService;

    public AlertController(AlertService alertService, 
                           com.bloodbond.alertsystem.service.NotificationService notificationService) {
        this.alertService = alertService;
        this.notificationService = notificationService;
    }

    /**
     * REST Endpoint: POST /api/alerts
     * Receives and validates emergency alert requests, saves them to MySQL,
     * triggers SMS/Email notifications, and returns the saved alert along with matching resources.
     * 
     * @param requestDto Validated JSON payload matching AlertRequestDto schema
     * @return HTTP 201 CREATED with ApiResponse envelope containing details and matches
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> createEmergencyAlert(
            @Valid @RequestBody AlertRequestDto requestDto) {

        logger.info("REST POST /api/alerts received for Patient: {}", requestDto.getPatientName());

        // 1. Save alert into MySQL database
        Alert savedAlert = alertService.createAlert(requestDto);

        // 2. Discover matching hospitals and voluntary donors in real-time
        List<Hospital> matchingHospitals = alertService.findMatchingHospitals(savedAlert.getBloodGroup());
        List<Donor> matchingDonors = alertService.findMatchingDonors(savedAlert.getBloodGroup());

        // 3. Multi-channel Emergency Notification Broadcast (SMS & Email)
        notificationService.broadcastAll(savedAlert, matchingDonors, matchingHospitals);

        // 3. Assemble combined payload
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("alert", savedAlert);
        responseData.put("matchingHospitals", matchingHospitals);
        responseData.put("matchingDonors", matchingDonors);
        responseData.put("compatibleGroups", alertService.getCompatibleBloodGroups(savedAlert.getBloodGroup()));

        // 4. Return HTTP 201 Created with JSON envelope
        ApiResponse<Map<String, Object>> response = ApiResponse.success(
                "Emergency alert broadcasted successfully. Nearby resources notified.",
                responseData
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * REST Endpoint: GET /api/alerts
     * Default root endpoint: fetches all currently active emergency broadcasts.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<Alert>>> getAllActiveAlerts() {
        List<Alert> activeAlerts = alertService.getActiveAlerts();
        return ResponseEntity.ok(ApiResponse.success("Active emergency broadcasts fetched.", activeAlerts));
    }

    /**
     * REST Endpoint: GET /api/alerts/active
     * Fetches all currently active emergency broadcasts.
     */
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<Alert>>> getActiveAlerts() {
        return getAllActiveAlerts();
    }

    /**
     * REST Endpoint: GET /api/alerts/stats
     * Provides aggregated statistics for the dashboard counter cards.
     */
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDashboardStatistics() {
        Map<String, Object> stats = alertService.getDashboardStatistics();
        return ResponseEntity.ok(ApiResponse.success("Dashboard statistics fetched.", stats));
    }

    /**
     * REST Endpoint: GET /api/alerts/match?bloodGroup=...
     * Previews matching hospitals and donors for any specified blood group without saving an alert.
     */
    @GetMapping("/match")
    public ResponseEntity<ApiResponse<Map<String, Object>>> previewMatches(
            @RequestParam(name = "bloodGroup") String bloodGroup) {

        List<Hospital> hospitals = alertService.findMatchingHospitals(bloodGroup);
        List<Donor> donors = alertService.findMatchingDonors(bloodGroup);

        Map<String, Object> result = new HashMap<>();
        result.put("bloodGroup", bloodGroup);
        result.put("compatibleGroups", alertService.getCompatibleBloodGroups(bloodGroup));
        result.put("matchingHospitals", hospitals);
        result.put("matchingDonors", donors);

        return ResponseEntity.ok(ApiResponse.success("Matching blood resources located.", result));
    }
}
