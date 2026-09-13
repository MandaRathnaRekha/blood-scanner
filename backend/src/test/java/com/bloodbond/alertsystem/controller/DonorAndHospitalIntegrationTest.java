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
 * Integration tests for DonorController, HospitalController, and centralized CORS lockdown.
 */
@SpringBootTest
@AutoConfigureMockMvc
class DonorAndHospitalIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("GET /api/donors returns all seeded donors")
    void shouldReturnAllDonors() throws Exception {
        mockMvc.perform(get("/api/donors")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data", hasSize(greaterThanOrEqualTo(10))));
    }

    @Test
    @DisplayName("GET /api/donors/search?bloodGroup=O- returns matching donors")
    void shouldSearchDonorsByBloodGroup() throws Exception {
        mockMvc.perform(get("/api/donors/search")
                .param("bloodGroup", "O-")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.data[0].bloodGroup").value("O-"));
    }

    @Test
    @DisplayName("GET /api/hospitals returns all seeded hospitals")
    void shouldReturnAllHospitals() throws Exception {
        mockMvc.perform(get("/api/hospitals")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data", hasSize(greaterThanOrEqualTo(5))));
    }

    @Test
    @DisplayName("GET /api/hospitals/search?location=Central returns matching hospital")
    void shouldSearchHospitalsByLocation() throws Exception {
        mockMvc.perform(get("/api/hospitals/search")
                .param("location", "Central")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    @DisplayName("CORS Preflight OPTIONS request should return Access-Control-Allow-Origin for allowed origin")
    void shouldRespondToCorsPreflight() throws Exception {
        mockMvc.perform(options("/api/alerts")
                .header("Origin", "http://localhost:3000")
                .header("Access-Control-Request-Method", "POST")
                .header("Access-Control-Request-Headers", "Content-Type,Accept"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:3000"))
                .andExpect(header().string("Access-Control-Allow-Methods", containsString("POST")));
    }
}
