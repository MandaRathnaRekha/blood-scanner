package com.bloodbond.alertsystem.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * LifePulse - Emergency Blood Alert System
 * Entity: Donor
 * Maps to the 'donors' table in the MySQL database.
 * Represents a registered voluntary blood donor available for emergency alerts.
 */
@Entity
@Table(name = "donors")
public class Donor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Full name of the voluntary donor
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    // Donor's blood group: A+, A-, B+, B-, AB+, AB-, O+, O-
    @Column(name = "blood_group", nullable = false, length = 5)
    private String bloodGroup;

    // Contact telephone number
    @Column(name = "phone", nullable = false, length = 20)
    private String phone;

    // Residential area, district, or city
    @Column(name = "location", nullable = false, length = 255)
    private String location;

    // Availability status: 'Available Now', 'On Call', 'Inactive'
    @Column(name = "status", length = 30)
    private String status;

    // Lifetime donation count
    @Column(name = "total_donations")
    private Integer totalDonations = 0;

    // Registration timestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Default No-Args Constructor (Required by JPA)
     */
    public Donor() {
    }

    /**
     * Parameterized Constructor
     */
    public Donor(String name, String bloodGroup, String phone, String location, String status, Integer totalDonations) {
        this.name = name;
        this.bloodGroup = bloodGroup;
        this.phone = phone;
        this.location = location;
        this.status = status != null ? status : "Available Now";
        this.totalDonations = totalDonations != null ? totalDonations : 0;
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.status == null || this.status.trim().isEmpty()) {
            this.status = "Available Now";
        }
        if (this.totalDonations == null) {
            this.totalDonations = 0;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getTotalDonations() {
        return totalDonations;
    }

    public void setTotalDonations(Integer totalDonations) {
        this.totalDonations = totalDonations;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Donor{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", bloodGroup='" + bloodGroup + '\'' +
                ", phone='" + phone + '\'' +
                ", location='" + location + '\'' +
                ", status='" + status + '\'' +
                ", totalDonations=" + totalDonations +
                '}';
    }
}
