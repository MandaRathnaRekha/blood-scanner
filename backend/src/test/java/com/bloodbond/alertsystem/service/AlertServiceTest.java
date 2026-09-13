package com.bloodbond.alertsystem.service;

import com.bloodbond.alertsystem.dto.AlertRequestDto;
import com.bloodbond.alertsystem.entity.Alert;
import com.bloodbond.alertsystem.entity.Donor;
import com.bloodbond.alertsystem.entity.Hospital;
import com.bloodbond.alertsystem.repository.AlertRepository;
import com.bloodbond.alertsystem.repository.DonorRepository;
import com.bloodbond.alertsystem.repository.HospitalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AlertService business logic:
 * - Blood compatibility logic
 * - Alert entity persistence
 * - Matching donors & hospitals resolution
 * - Dashboard statistics aggregation
 */
@ExtendWith(MockitoExtension.class)
class AlertServiceTest {

    @Mock
    private AlertRepository alertRepository;

    @Mock
    private DonorRepository donorRepository;

    @Mock
    private HospitalRepository hospitalRepository;

    @InjectMocks
    private AlertService alertService;

    private AlertRequestDto sampleRequest;

    @BeforeEach
    void setUp() {
        sampleRequest = new AlertRequestDto();
        sampleRequest.setPatientName("Aarav Gupta");
        sampleRequest.setBloodGroup("O-");
        sampleRequest.setLocation("City Hospital Trauma Ward");
        sampleRequest.setContactNumber("+91 98765 00001");
        sampleRequest.setMessage("Critical condition after road accident. Immediate transfusion needed.");
    }

    @Test
    @DisplayName("Should create and persist alert successfully")
    void shouldCreateAlertSuccessfully() {
        Alert mockSavedAlert = new Alert("Aarav Gupta", "O-", "City Hospital Trauma Ward", "+91 98765 00001", "Critical condition");
        when(alertRepository.save(any(Alert.class))).thenReturn(mockSavedAlert);

        Alert created = alertService.createAlert(sampleRequest);

        assertNotNull(created);
        assertEquals("Aarav Gupta", created.getPatientName());
        assertEquals("O-", created.getBloodGroup());
        verify(alertRepository, times(1)).save(any(Alert.class));
    }

    @Test
    @DisplayName("Should return exact compatibility rules for all 8 blood groups")
    void shouldReturnCorrectCompatibleBloodGroups() {
        // O- can only receive O-
        List<String> oNeg = alertService.getCompatibleBloodGroups("O-");
        assertEquals(List.of("O-"), oNeg);

        // O+ can receive O+, O-
        List<String> oPos = alertService.getCompatibleBloodGroups("O+");
        assertTrue(oPos.containsAll(List.of("O+", "O-")));
        assertEquals(2, oPos.size());

        // A- can receive A-, O-
        List<String> aNeg = alertService.getCompatibleBloodGroups("A-");
        assertTrue(aNeg.containsAll(List.of("A-", "O-")));

        // A+ can receive A+, A-, O+, O-
        List<String> aPos = alertService.getCompatibleBloodGroups("A+");
        assertEquals(4, aPos.size());

        // B- can receive B-, O-
        List<String> bNeg = alertService.getCompatibleBloodGroups("B-");
        assertTrue(bNeg.containsAll(List.of("B-", "O-")));

        // B+ can receive B+, B-, O+, O-
        List<String> bPos = alertService.getCompatibleBloodGroups("B+");
        assertEquals(4, bPos.size());

        // AB- can receive AB-, A-, B-, O-
        List<String> abNeg = alertService.getCompatibleBloodGroups("AB-");
        assertEquals(4, abNeg.size());

        // AB+ universal recipient receives all 8 groups
        List<String> abPos = alertService.getCompatibleBloodGroups("AB+");
        assertEquals(8, abPos.size());
    }

    @Test
    @DisplayName("Should query donors using compatible blood groups")
    void shouldFindMatchingDonors() {
        Donor donor = new Donor("Test Donor", "O-", "+91 98765 43210", "Downtown", "Available Now", 5);
        when(donorRepository.findByBloodGroupIn(List.of("O-"))).thenReturn(List.of(donor));

        List<Donor> donors = alertService.findMatchingDonors("O-");

        assertNotNull(donors);
        assertEquals(1, donors.size());
        assertEquals("Test Donor", donors.get(0).getName());
        verify(donorRepository, times(1)).findByBloodGroupIn(List.of("O-"));
    }

    @Test
    @DisplayName("Should query hospitals that stock compatible blood groups")
    void shouldFindMatchingHospitals() {
        Hospital hospital = new Hospital("Life Hospital", "O-, O+", "+91 98765 00000", "Downtown", 10);
        when(hospitalRepository.findByBloodGroupAvailableContaining("O-")).thenReturn(List.of(hospital));

        List<Hospital> hospitals = alertService.findMatchingHospitals("O-");

        assertNotNull(hospitals);
        assertEquals(1, hospitals.size());
        assertEquals("Life Hospital", hospitals.get(0).getHospitalName());
    }

    @Test
    @DisplayName("Should return aggregated dashboard statistics")
    void shouldReturnDashboardStatistics() {
        when(alertRepository.countByStatus("ACTIVE")).thenReturn(3L);
        when(donorRepository.count()).thenReturn(10L);
        when(hospitalRepository.count()).thenReturn(5L);

        Map<String, Object> stats = alertService.getDashboardStatistics();

        assertEquals(3L, stats.get("activeAlerts"));
        assertEquals(10L, stats.get("totalDonors"));
        assertEquals(5L, stats.get("totalHospitals"));
    }
}
