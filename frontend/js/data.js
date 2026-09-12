/**
 * LifePulse - Emergency Blood Alert System
 * File: frontend/js/data.js
 * Description: Mock datasets for hospitals and voluntary donors, 
 *              plus clinical blood group compatibility rules.
 */

// ==========================================================================
// 1. Clinical Blood Compatibility Rules
// Key: Recipient Blood Group
// Value: Array of Compatible Donor Blood Groups (Red Cell Transfusion)
// ==========================================================================
const BLOOD_COMPATIBILITY = {
    "O-": ["O-"],                                        // Can only receive O-
    "O+": ["O+", "O-"],                                  // Can receive O+, O-
    "A-": ["A-", "O-"],                                  // Can receive A-, O-
    "A+": ["A+", "A-", "O+", "O-"],                      // Can receive A+, A-, O+, O-
    "B-": ["B-", "O-"],                                  // Can receive B-, O-
    "B+": ["B+", "B-", "O+", "O-"],                      // Can receive B+, B-, O+, O-
    "AB-": ["AB-", "A-", "B-", "O-"],                    // Can receive AB-, A-, B-, O-
    "AB+": ["AB+", "AB-", "A+", "A-", "B+", "B-", "O+", "O-"] // Universal Recipient
};

// ==========================================================================
// 2. Sample Registered Hospitals & Blood Banks
// Represents regional healthcare facilities equipped with cold storage
// ==========================================================================
const SAMPLE_HOSPITALS = [
    {
        id: 1,
        hospitalName: "Apollo Emergency Care & Blood Bank",
        bloodGroupAvailable: ["A+", "A-", "B+", "O+", "O-"],
        contact: "+91 98765 43210",
        location: "Central Zone, Main Road",
        availableUnits: 18,
        distanceKm: "1.8 km away"
    },
    {
        id: 2,
        hospitalName: "City Red Cross Blood Center",
        bloodGroupAvailable: ["O-", "O+", "AB+", "B+", "B-"],
        contact: "+91 98111 22334",
        location: "West Wing, Civil Hospital Road",
        availableUnits: 24,
        distanceKm: "2.4 km away"
    },
    {
        id: 3,
        hospitalName: "St. Jude Super Specialty Hospital",
        bloodGroupAvailable: ["A+", "B+", "AB+", "AB-", "O+"],
        contact: "+91 97222 33445",
        location: "East Bypass, Near Metro Gate 3",
        availableUnits: 12,
        distanceKm: "3.5 km away"
    },
    {
        id: 4,
        hospitalName: "Metro Trauma Center & Blood Repository",
        bloodGroupAvailable: ["A-", "B-", "O-", "AB-"],
        contact: "+91 99000 11223",
        location: "North Corridor, Ring Road",
        availableUnits: 9,
        distanceKm: "4.1 km away"
    },
    {
        id: 5,
        hospitalName: "Fortis LifeCare Regional Hospital",
        bloodGroupAvailable: ["A+", "B+", "O+", "AB+"],
        contact: "+91 98333 44556",
        location: "Tech City Campus, Sector 5",
        availableUnits: 15,
        distanceKm: "5.2 km away"
    }
];

// ==========================================================================
// 3. Sample Voluntary Blood Donors
// Verified community volunteers ready for emergency on-call response
// ==========================================================================
const SAMPLE_DONORS = [
    {
        id: 1,
        name: "Rahul Sharma",
        bloodGroup: "O-",
        phone: "+91 98711 22334",
        location: "Central Zone, Sector 12",
        status: "Available Now",
        totalDonations: 6
    },
    {
        id: 2,
        name: "Priya Patel",
        bloodGroup: "O+",
        phone: "+91 98222 33445",
        location: "Civil Lines, Near City Hall",
        status: "Available Now",
        totalDonations: 4
    },
    {
        id: 3,
        name: "Amitabh Verma",
        bloodGroup: "A+",
        phone: "+91 98333 44556",
        location: "Greenwood Residency, Flat 402",
        status: "Available Now",
        totalDonations: 9
    },
    {
        id: 4,
        name: "Sneha Mukherjee",
        bloodGroup: "A-",
        phone: "+91 98444 55667",
        location: "East Bypass, Block B",
        status: "On Call",
        totalDonations: 3
    },
    {
        id: 5,
        name: "David D'Souza",
        bloodGroup: "B+",
        phone: "+91 98555 66778",
        location: "Railway Colony, Quarter 18",
        status: "Available Now",
        totalDonations: 5
    },
    {
        id: 6,
        name: "Ananya Iyer",
        bloodGroup: "B-",
        phone: "+91 98666 77889",
        location: "West End Avenue",
        status: "On Call",
        totalDonations: 2
    },
    {
        id: 7,
        name: "Vikram Malhotra",
        bloodGroup: "AB+",
        phone: "+91 98777 88990",
        location: "North Sector, Apartment 201",
        status: "Available Now",
        totalDonations: 8
    },
    {
        id: 8,
        name: "Fatima Sheikh",
        bloodGroup: "AB-",
        phone: "+91 98888 99001",
        location: "Central Market Road",
        status: "Available Now",
        totalDonations: 5
    },
    {
        id: 9,
        name: "Rohan Kulkarni",
        bloodGroup: "O-",
        phone: "+91 98999 00112",
        location: "University Campus Hostel",
        status: "Available Now",
        totalDonations: 7
    },
    {
        id: 10,
        name: "Meera Nair",
        bloodGroup: "A+",
        phone: "+91 97000 11223",
        location: "Tech Park Residences",
        status: "Available Now",
        totalDonations: 3
    }
];

// ==========================================================================
// 4. Clinical Matching Helper Functions
// ==========================================================================

/**
 * Returns a list of all blood groups that can safely donate to the patient.
 * @param {string} recipientBloodGroup - e.g., "A+"
 * @returns {Array<string>} - e.g., ["A+", "A-", "O+", "O-"]
 */
function getCompatibleDonorGroups(recipientBloodGroup) {
    return BLOOD_COMPATIBILITY[recipientBloodGroup] || [recipientBloodGroup];
}

/**
 * Filters hospitals that currently have the exact or compatible blood group available.
 * @param {string} requestedBloodGroup - e.g., "O-"
 * @returns {Array<Object>} Filtered list of hospitals
 */
function findMatchingHospitals(requestedBloodGroup) {
    const compatibleGroups = getCompatibleDonorGroups(requestedBloodGroup);
    
    return SAMPLE_HOSPITALS.filter(hospital => {
        // Check if the hospital holds at least one compatible blood group
        return hospital.bloodGroupAvailable.some(group => compatibleGroups.includes(group));
    });
}

/**
 * Filters voluntary donors whose blood group is compatible with the patient.
 * @param {string} requestedBloodGroup - e.g., "B+"
 * @returns {Array<Object>} Filtered list of donors
 */
function findMatchingDonors(requestedBloodGroup) {
    const compatibleGroups = getCompatibleDonorGroups(requestedBloodGroup);
    
    return SAMPLE_DONORS.filter(donor => {
        return compatibleGroups.includes(donor.bloodGroup);
    });
}
