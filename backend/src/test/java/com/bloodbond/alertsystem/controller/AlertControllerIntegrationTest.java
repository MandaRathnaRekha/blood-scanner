package com.bloodbond.alertsystem.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for AlertController REST API:
 * - GET /api/alerts/active
 * - GET /api/alerts/stats
 * - GET /api/alerts/match?bloodGroup=O-
 * - POST /api/alerts (Success & Validation Failure)
 */
@SpringBootTest
@AutoConfigureMockMvc
class AlertControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("GET /api/alerts/active should return 200 OK and list of active alerts")
    void shouldReturnActiveAlerts() throws Exception {
        mockMvc.perform(get("/api/alerts/active")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.data[0].bloodGroup").isNotEmpty());
    }

    @Test
    @DisplayName("GET /api/alerts root endpoint should return 200 OK and active alerts")
    void shouldReturnActiveAlertsFromRoot() throws Exception {
        mockMvc.perform(get("/api/alerts")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    @DisplayName("GET /api/alerts/stats should return dashboard counters")
    void shouldReturnDashboardStats() throws Exception {
        mockMvc.perform(get("/api/alerts/stats")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.activeAlerts", notNullValue()))
                .andExpect(jsonPath("$.data.totalDonors", greaterThanOrEqualTo(10)))
                .andExpect(jsonPath("$.data.totalHospitals", greaterThanOrEqualTo(5)));
    }

    @Test
    @DisplayName("GET /api/alerts/match?bloodGroup=O- should return compatible resources")
    void shouldPreviewMatches() throws Exception {
        mockMvc.perform(get("/api/alerts/match")
                .param("bloodGroup", "O-")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.bloodGroup").value("O-"))
                .andExpect(jsonPath("$.data.compatibleGroups", hasItem("O-")))
                .andExpect(jsonPath("$.data.matchingHospitals").isArray())
                .andExpect(jsonPath("$.data.matchingDonors").isArray());
    }

    @Test
    @DisplayName("POST /api/alerts should broadcast alert and return 201 Created")
    void shouldBroadcastAlertSuccessfully() throws Exception {
        String payload = """
                {
                    "patientName": "Aarav Sharma",
                    "bloodGroup": "O-",
                    "location": "Trauma Center Ward 4",
                    "contactNumber": "+91 98765 12345",
                    "message": "Immediate blood needed for trauma surgery."
                }
                """;

        mockMvc.perform(post("/api/alerts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.alert.id", notNullValue()))
                .andExpect(jsonPath("$.data.alert.patientName").value("Aarav Sharma"))
                .andExpect(jsonPath("$.data.alert.bloodGroup").value("O-"))
                .andExpect(jsonPath("$.data.matchingDonors").isArray())
                .andExpect(jsonPath("$.data.matchingHospitals").isArray());
    }

    @Test
    @DisplayName("POST /api/alerts with invalid data should return 400 Bad Request via GlobalExceptionHandler")
    void shouldReturnBadRequestForInvalidAlert() throws Exception {
        String invalidPayload = """
                {
                    "patientName": "",
                    "bloodGroup": "INVALID",
                    "location": "",
                    "contactNumber": "123",
                    "message": ""
                }
                """;

        mockMvc.perform(post("/api/alerts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidPayload)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message", containsString("Validation failed")))
                .andExpect(jsonPath("$.data.patientName", notNullValue()))
                .andExpect(jsonPath("$.data.bloodGroup", notNullValue()));
    }
}
