-- ===================================================================
-- LifePulse - Emergency Blood Alert System
-- File: database/setup.sql
-- Description: MySQL Database Schema, Table DDLs, Indexes, 
--              and Seed Data for Hospitals, Donors, and Emergency Alerts.
-- ===================================================================

-- 1. CREATE DATABASE
-- Character set utf8mb4 supports international names, symbols, and emojis
CREATE DATABASE IF NOT EXISTS blood_alert_db
    CHARACTER SET utf8mb4 
    COLLATE utf8mb4_unicode_ci;

USE blood_alert_db;

-- -------------------------------------------------------------------
-- 2. CREATE TABLE: donors
-- Stores verified voluntary blood donors ready for on-call dispatch
-- -------------------------------------------------------------------
DROP TABLE IF EXISTS donors;

CREATE TABLE donors (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    blood_group VARCHAR(5) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    location VARCHAR(255) NOT NULL,
    status VARCHAR(30) DEFAULT 'Available Now',
    total_donations INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Indexing for rapid emergency matching by blood group
    INDEX idx_donor_blood_group (blood_group),
    INDEX idx_donor_location (location)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -------------------------------------------------------------------
-- 3. CREATE TABLE: hospitals
-- Stores medical centers and blood banks with available blood inventories
-- -------------------------------------------------------------------
DROP TABLE IF EXISTS hospitals;

CREATE TABLE hospitals (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    hospital_name VARCHAR(150) NOT NULL,
    blood_group_available VARCHAR(255) NOT NULL, -- Stored as comma-separated or JSON list e.g. "A+, A-, B+, O+, O-"
    contact VARCHAR(30) NOT NULL,
    location VARCHAR(255) NOT NULL,
    available_units INT DEFAULT 10,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_hospital_name (hospital_name),
    INDEX idx_hospital_location (location)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -------------------------------------------------------------------
-- 4. CREATE TABLE: alerts
-- Stores emergency requests broadcasted by patients/hospital staff
-- -------------------------------------------------------------------
DROP TABLE IF EXISTS alerts;

CREATE TABLE alerts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_name VARCHAR(100) NOT NULL,
    blood_group VARCHAR(5) NOT NULL,
    location VARCHAR(255) NOT NULL,
    contact_number VARCHAR(20) NOT NULL,
    message TEXT NOT NULL,
    status VARCHAR(20) DEFAULT 'ACTIVE', -- ACTIVE, FULFILLED, CANCELLED
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_alert_blood_group (blood_group),
    INDEX idx_alert_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ===================================================================
-- 5. INITIAL SEED DATA
-- Populate realistic records for immediate verification
-- ===================================================================

-- Insert sample hospitals
INSERT INTO hospitals (hospital_name, blood_group_available, contact, location, available_units)
VALUES 
('Apollo Emergency Care & Blood Bank', 'A+, A-, B+, O+, O-', '+91 98765 43210', 'Central Zone, Main Road', 18),
('City Red Cross Blood Center', 'O-, O+, AB+, B+, B-', '+91 98111 22334', 'West Wing, Civil Hospital Road', 24),
('St. Jude Super Specialty Hospital', 'A+, B+, AB+, AB-, O+', '+91 97222 33445', 'East Bypass, Near Metro Gate 3', 12),
('Metro Trauma Center & Blood Repository', 'A-, B-, O-, AB-', '+91 99000 11223', 'North Corridor, Ring Road', 9),
('Fortis LifeCare Regional Hospital', 'A+, B+, O+, AB+', '+91 98333 44556', 'Tech City Campus, Sector 5', 15);

-- Insert sample voluntary donors
INSERT INTO donors (name, blood_group, phone, location, status, total_donations)
VALUES 
('Rahul Sharma', 'O-', '+91 98711 22334', 'Central Zone, Sector 12', 'Available Now', 6),
('Priya Patel', 'O+', '+91 98222 33445', 'Civil Lines, Near City Hall', 'Available Now', 4),
('Amitabh Verma', 'A+', '+91 98333 44556', 'Greenwood Residency, Flat 402', 'Available Now', 9),
('Sneha Mukherjee', 'A-', '+91 98444 55667', 'East Bypass, Block B', 'On Call', 3),
('David D''Souza', 'B+', '+91 98555 66778', 'Railway Colony, Quarter 18', 'Available Now', 5),
('Ananya Iyer', 'B-', '+91 98666 77889', 'West End Avenue', 'On Call', 2),
('Vikram Malhotra', 'AB+', '+91 98777 88990', 'North Sector, Apartment 201', 'Available Now', 8),
('Fatima Sheikh', 'AB-', '+91 98888 99001', 'Central Market Road', 'Available Now', 5),
('Rohan Kulkarni', 'O-', '+91 98999 00112', 'University Campus Hostel', 'Available Now', 7),
('Meera Nair', 'A+', '+91 97000 11223', 'Tech Park Residences', 'Available Now', 3);

-- Insert a baseline active emergency alert
INSERT INTO alerts (patient_name, blood_group, location, contact_number, message, status)
VALUES 
('Simran Kaur', 'O-', 'Metro Hospital ICU, Room 104', '+91 98123 45678', 'Urgent requirement for trauma surgery. Need 2 units O- immediately.', 'ACTIVE');
