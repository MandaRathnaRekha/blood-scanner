package com.bloodbond.alertsystem.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * LifePulse - Emergency Blood Alert System
 * Entity: Hospital
 * Maps to the 'hospitals' table in the MySQL database.
 * Represents a registered hospital, medical center, or regional blood bank.
 */
@Entity
@Table(name = "hospitals")
public class Hospital {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Name of the hospital or blood bank facility
    @Column(name = "hospital_name", nullable = false, length = 150)
    private String hospitalName;

    // Available blood groups stored as a comma-separated list e.g., "A+, A-, B+, O+, O-"
    @Column(name = "blood_group_available", nullable = false, length = 255)
    private String bloodGroupAvailable;

    // Emergency blood bank hotline or reception phone
    @Column(name = "contact", nullable = false, length = 30)
    private String contact;

    // Physical street address, district, or landmark
    @Column(name = "location", nullable = false, length = 255)
    private String location;

    // Approximate units stocked
    @Column(name = "available_units")
    private Integer availableUnits = 10;

    // Facility onboarding timestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Default No-Args Constructor (Required by JPA)
     */
    public Hospital() {
    }

    /**
     * Parameterized Constructor
     */
    public Hospital(String hospitalName, String bloodGroupAvailable, String contact, String location, Integer availableUnits) {
        this.hospitalName = hospitalName;
        this.bloodGroupAvailable = bloodGroupAvailable;
        this.contact = contact;
        this.location = location;
        this.availableUnits = availableUnits != null ? availableUnits : 10;
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.availableUnits == null) {
            this.availableUnits = 0;
        }
    }

    // =========================================================================
    // Getters and Setters
    // =========================================================================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    public String getBloodGroupAvailable() {
        return bloodGroupAvailable;
    }

    public void setBloodGroupAvailable(String bloodGroupAvailable) {
        this.bloodGroupAvailable = bloodGroupAvailable;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getAvailableUnits() {
        return availableUnits;
    }

    public void setAvailableUnits(Integer availableUnits) {
        this.availableUnits = availableUnits;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Hospital{" +
                "id=" + id +
                ", hospitalName='" + hospitalName + '\'' +
                ", bloodGroupAvailable='" + bloodGroupAvailable + '\'' +
                ", contact='" + contact + '\'' +
                ", location='" + location + '\'' +
                ", availableUnits=" + availableUnits +
                '}';
    }
}
