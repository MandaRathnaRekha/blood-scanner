/**
 * LifePulse - Emergency Blood Alert System
 * File: frontend/js/validation.js
 * Description: Client-side input validation and real-time visual feedback
 *              using Bootstrap 5 validation styles.
 */

// ==========================================================================
// 1. Validation Rules & Regular Expressions
// ==========================================================================
const VALIDATION_RULES = {
    // Allows alphabetic characters, spaces, periods, and hyphens (min 3, max 60 chars)
    patientNameRegex: /^[A-Za-z\s.\-']{3,60}$/,
    
    // Valid telephone numbers: 10 to 15 characters, allowing +, spaces, and dashes
    phoneRegex: /^[0-9+\-\s]{10,15}$/,
    
    // Allowed blood groups
    allowedBloodGroups: ["A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"],
    
    // Minimum character lengths
    minLocationLength: 3,
    minMessageLength: 10
};

// ==========================================================================
// 2. Visual Feedback Helpers
// Adds Bootstrap `.is-valid` or `.is-invalid` classes dynamically to inputs
// ==========================================================================

/**
 * Marks an input element as valid by applying Bootstrap green outline
 * @param {HTMLElement} inputElement 
 */
function markFieldValid(inputElement) {
    inputElement.classList.remove("is-invalid");
    inputElement.classList.add("is-valid");
}

/**
 * Marks an input element as invalid by applying Bootstrap red outline & error message
 * @param {HTMLElement} inputElement 
 * @param {string} customErrorMessage Optional custom message to show
 */
function markFieldInvalid(inputElement, customErrorMessage) {
    inputElement.classList.remove("is-valid");
    inputElement.classList.add("is-invalid");

    // If an invalid-feedback container exists next to the input, update its text
    const feedbackEl = inputElement.parentElement.querySelector(".invalid-feedback") || 
                       inputElement.parentElement.parentElement.querySelector(".invalid-feedback");
    if (feedbackEl && customErrorMessage) {
        feedbackEl.textContent = customErrorMessage;
    }
}

/**
 * Resets all validation styling back to normal clean state
 * @param {HTMLFormElement} formElement 
 */
function resetFormValidation(formElement) {
    const inputs = formElement.querySelectorAll(".form-control, .form-select");
    inputs.forEach(input => {
        input.classList.remove("is-valid", "is-invalid");
    });
}

// ==========================================================================
// 3. Field-Level Validation Functions
// Each function returns { isValid: boolean, message: string }
// ==========================================================================

function validatePatientName(nameValue) {
    const trimmed = (nameValue || "").trim();
    if (!trimmed) {
        return { isValid: false, message: "Patient name is required." };
    }
    if (trimmed.length < 3) {
        return { isValid: false, message: "Name must be at least 3 characters long." };
    }
    if (!VALIDATION_RULES.patientNameRegex.test(trimmed)) {
        return { isValid: false, message: "Name can only contain letters, spaces, and hyphens." };
    }
    return { isValid: true, message: "" };
}

function validateBloodGroup(groupValue) {
    if (!groupValue || !VALIDATION_RULES.allowedBloodGroups.includes(groupValue)) {
        return { isValid: false, message: "Please select a valid blood group." };
    }
    return { isValid: true, message: "" };
}

function validateLocation(locationValue) {
    const trimmed = (locationValue || "").trim();
    if (!trimmed) {
        return { isValid: false, message: "Location / Hospital name is required." };
    }
    if (trimmed.length < VALIDATION_RULES.minLocationLength) {
        return { isValid: false, message: "Please enter a more descriptive location." };
    }
    return { isValid: true, message: "" };
}

function validateContactNumber(phoneValue) {
    const trimmed = (phoneValue || "").trim();
    // Strip common separators to count purely digits
    const digitsOnly = trimmed.replace(/\D/g, "");
    
    if (!trimmed) {
        return { isValid: false, message: "Emergency contact number is required." };
    }
    if (digitsOnly.length < 10 || digitsOnly.length > 15 || !VALIDATION_RULES.phoneRegex.test(trimmed)) {
        return { isValid: false, message: "Enter a valid phone number (10 to 15 digits)." };
    }
    return { isValid: true, message: "" };
}

function validateEmergencyMessage(messageValue) {
    const trimmed = (messageValue || "").trim();
    if (!trimmed) {
        return { isValid: false, message: "Emergency message / clinical note is required." };
    }
    if (trimmed.length < VALIDATION_RULES.minMessageLength) {
        return { isValid: false, message: `Please provide more details (min ${VALIDATION_RULES.minMessageLength} characters).` };
    }
    return { isValid: true, message: "" };
}

// ==========================================================================
// 4. Form-Wide Validator & Real-Time Event Attachment
// ==========================================================================

/**
 * Validates the entire emergency form before submission
 * @param {HTMLFormElement} formElement
 * @returns { { isValid: boolean, data: Object, errors: Array<string> } }
 */
function validateEmergencyForm(formElement) {
    const nameInput = formElement.querySelector("#patientName");
    const bloodGroupSelect = formElement.querySelector("#bloodGroup");
    const locationInput = formElement.querySelector("#location");
    const contactInput = formElement.querySelector("#contactNumber");
    const messageInput = formElement.querySelector("#emergencyMessage");

    const nameRes = validatePatientName(nameInput.value);
    const bgRes = validateBloodGroup(bloodGroupSelect.value);
    const locRes = validateLocation(locationInput.value);
    const contactRes = validateContactNumber(contactInput.value);
    const msgRes = validateEmergencyMessage(messageInput.value);

    // Apply UI feedback to each field
    nameRes.isValid ? markFieldValid(nameInput) : markFieldInvalid(nameInput, nameRes.message);
    bgRes.isValid ? markFieldValid(bloodGroupSelect) : markFieldInvalid(bloodGroupSelect, bgRes.message);
    locRes.isValid ? markFieldValid(locationInput) : markFieldInvalid(locationInput, locRes.message);
    contactRes.isValid ? markFieldValid(contactInput) : markFieldInvalid(contactInput, contactRes.message);
    msgRes.isValid ? markFieldValid(messageInput) : markFieldInvalid(messageInput, msgRes.message);

    const isAllValid = nameRes.isValid && bgRes.isValid && locRes.isValid && contactRes.isValid && msgRes.isValid;

    const errors = [];
    if (!nameRes.isValid) errors.push(nameRes.message);
    if (!bgRes.isValid) errors.push(bgRes.message);
    if (!locRes.isValid) errors.push(locRes.message);
    if (!contactRes.isValid) errors.push(contactRes.message);
    if (!msgRes.isValid) errors.push(msgRes.message);

    return {
        isValid: isAllValid,
        data: {
            patientName: nameInput.value.trim(),
            bloodGroup: bloodGroupSelect.value,
            location: locationInput.value.trim(),
            contactNumber: contactInput.value.trim(),
            message: messageInput.value.trim()
        },
        errors: errors
    };
}

/**
 * Attaches live validation listeners to inputs so feedback appears as the user types or leaves a field.
 * @param {HTMLFormElement} formElement 
 */
function setupLiveValidation(formElement) {
    const nameInput = formElement.querySelector("#patientName");
    const bloodGroupSelect = formElement.querySelector("#bloodGroup");
    const locationInput = formElement.querySelector("#location");
    const contactInput = formElement.querySelector("#contactNumber");
    const messageInput = formElement.querySelector("#emergencyMessage");

    nameInput.addEventListener("blur", () => {
        const res = validatePatientName(nameInput.value);
        res.isValid ? markFieldValid(nameInput) : markFieldInvalid(nameInput, res.message);
    });

    bloodGroupSelect.addEventListener("change", () => {
        const res = validateBloodGroup(bloodGroupSelect.value);
        res.isValid ? markFieldValid(bloodGroupSelect) : markFieldInvalid(bloodGroupSelect, res.message);
    });

    locationInput.addEventListener("blur", () => {
        const res = validateLocation(locationInput.value);
        res.isValid ? markFieldValid(locationInput) : markFieldInvalid(locationInput, res.message);
    });

    contactInput.addEventListener("blur", () => {
        const res = validateContactNumber(contactInput.value);
        res.isValid ? markFieldValid(contactInput) : markFieldInvalid(contactInput, res.message);
    });

    messageInput.addEventListener("blur", () => {
        const res = validateEmergencyMessage(messageInput.value);
        res.isValid ? markFieldValid(messageInput) : markFieldInvalid(messageInput, res.message);
    });
}
