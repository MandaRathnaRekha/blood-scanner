package com.bloodbond.alertsystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * LifePulse - Emergency Blood Alert System
 * DTO: AlertRequestDto
 * Data Transfer Object representing the incoming JSON payload for POST /api/alerts.
 * Contains declarative validation annotations to reject malformed requests.
 */
public class AlertRequestDto {

    @NotBlank(message = "Patient name is required and cannot be empty.")
    @Size(min = 3, max = 100, message = "Patient name must be between 3 and 100 characters.")
    private String patientName;

    @NotBlank(message = "Blood group is required.")
    @Pattern(
        regexp = "^(A|B|AB|O)[+-]$",
        message = "Invalid blood group. Permitted values: A+, A-, B+, B-, AB+, AB-, O+, O-."
    )
    private String bloodGroup;

    @NotBlank(message = "Location is required.")
    @Size(min = 3, max = 255, message = "Location must be between 3 and 255 characters.")
    private String location;

    @NotBlank(message = "Emergency contact number is required.")
    @Pattern(
        regexp = "^[0-9+\\-\\s]{10,20}$",
        message = "Contact number must contain 10 to 20 valid telephone digits/characters."
    )
    private String contactNumber;

    @NotBlank(message = "Emergency clinical message is required.")
    @Size(min = 10, max = 1000, message = "Emergency message must be at least 10 characters long.")
    private String message;

    /**
     * Default No-Args Constructor
     * Used by Jackson JSON deserializer
     */
    public AlertRequestDto() {
    }

    /**
     * Parameterized Constructor
     */
    public AlertRequestDto(String patientName, String bloodGroup, String location, String contactNumber, String message) {
        this.patientName = patientName;
        this.bloodGroup = bloodGroup;
        this.location = location;
        this.contactNumber = contactNumber;
        this.message = message;
    }

    // =========================================================================
    // Getters and Setters
    // =========================================================================

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "AlertRequestDto{" +
                "patientName='" + patientName + '\'' +
                ", bloodGroup='" + bloodGroup + '\'' +
                ", location='" + location + '\'' +
                ", contactNumber='" + contactNumber + '\'' +
                '}';
    }
}
