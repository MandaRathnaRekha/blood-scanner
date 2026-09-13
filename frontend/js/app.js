/**
 * LifePulse - Emergency Blood Alert System
 * File: frontend/js/app.js
 * Description: Core frontend controller coordinating UI interactions, GPS detection,
 *              client-side validation, and Spring Boot REST API integration (fetch).
 */

// ==========================================================================
// 1. Configuration & API Endpoints
// ==========================================================================
const CONFIG = {
    // Dynamic integration: reads from window.APP_CONFIG if available, else falls back to Railway backend
    USE_BACKEND_API: (typeof window !== "undefined" && window.APP_CONFIG && typeof window.APP_CONFIG.USE_BACKEND_API !== "undefined")
        ? window.APP_CONFIG.USE_BACKEND_API 
        : true,
    API_BASE_URL: (typeof window !== "undefined" && window.APP_CONFIG && window.APP_CONFIG.API_BASE_URL)
        ? window.APP_CONFIG.API_BASE_URL 
        : "https://blood-scanner-production.up.railway.app/api"
};

// ==========================================================================
// 2. Application Initialization
// ==========================================================================
document.addEventListener("DOMContentLoaded", () => {
    console.log("LifePulse Emergency System Initialized.");

    const emergencyForm = document.getElementById("emergency-alert-form");
    const gpsButton = document.getElementById("btn-detect-gps");
    const bloodGroupSelect = document.getElementById("bloodGroup");

    // Initialize real-time validation feedback
    if (typeof setupLiveValidation === "function") {
        setupLiveValidation(emergencyForm);
    }

    // Attach GPS location detection listener
    if (gpsButton) {
        gpsButton.addEventListener("click", handleGpsDetection);
    }

    // Attach form submission listener
    if (emergencyForm) {
        emergencyForm.addEventListener("submit", handleEmergencyFormSubmit);
    }

    // Live preview matching when blood group dropdown changes
    if (bloodGroupSelect) {
        bloodGroupSelect.addEventListener("change", (e) => {
            const selectedGroup = e.target.value;
            if (selectedGroup) {
                previewMatches(selectedGroup);
            }
        });
    }

    // Fetch live system statistics from Spring Boot backend (or fallback to defaults)
    syncDashboardStats();
});

// ==========================================================================
// 3. Emergency Form Submission Workflow (Phase 5 REST Integration)
// ==========================================================================

/**
 * Intercepts form submit, performs validation, transmits payload via fetch()
 * to Spring Boot POST /api/alerts, and updates the UI with the saved record.
 * 
 * @param {Event} event 
 */
async function handleEmergencyFormSubmit(event) {
    event.preventDefault();

    const form = event.target;
    const submitBtn = document.getElementById("btn-submit-alert");

    // 1. Run client-side validation
    const validation = validateEmergencyForm(form);

    if (!validation.isValid) {
        showSystemAlert(
            "Please resolve highlighted validation errors before broadcasting.",
            "warning"
        );
        return;
    }

    // 2. Visual feedback: Disable button and show loading spinner
    const originalBtnHtml = submitBtn.innerHTML;
    submitBtn.disabled = true;
    submitBtn.innerHTML = `<span class="spinner-border spinner-border-sm me-2" role="status"></span> Transmitting to Emergency Network...`;

    const requestPayload = validation.data;

    try {
        let alertRecord = null;
        let matchedHospitals = [];
        let matchedDonors = [];
        let isLiveBackend = false;

        // 3. Attempt Spring Boot REST API transmission
        if (CONFIG.USE_BACKEND_API) {
            try {
                const response = await fetch(`${CONFIG.API_BASE_URL}/alerts`, {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json",
                        "Accept": "application/json"
                    },
                    body: JSON.stringify(requestPayload)
                });

                if (response.ok) {
                    const jsonResult = await response.json();
                    const payload = jsonResult.data;
                    
                    alertRecord = payload.alert;
                    matchedHospitals = payload.matchingHospitals || [];
                    matchedDonors = payload.matchingDonors || [];
                    isLiveBackend = true;
                } else {
                    const errorJson = await response.json().catch(() => null);
                    const errorMsg = errorJson ? errorJson.message : `HTTP ${response.status}`;
                    console.warn("Backend rejected request:", errorMsg);
                    throw new Error(errorMsg);
                }
            } catch (networkError) {
                console.warn("Backend unavailable or network error. Using simulated engine:", networkError.message);
                // Graceful fallback to local mock engine if backend server is not running
                alertRecord = {
                    id: Math.floor(Math.random() * 9000) + 1000,
                    ...requestPayload,
                    status: "ACTIVE",
                    createdAt: new Date().toISOString()
                };
                matchedHospitals = findMatchingHospitals(requestPayload.bloodGroup);
                matchedDonors = findMatchingDonors(requestPayload.bloodGroup);
            }
        } else {
            // Local mode
            alertRecord = {
                id: Math.floor(Math.random() * 9000) + 1000,
                ...requestPayload,
                status: "ACTIVE",
                createdAt: new Date().toISOString()
            };
            matchedHospitals = findMatchingHospitals(requestPayload.bloodGroup);
            matchedDonors = findMatchingDonors(requestPayload.bloodGroup);
        }

        // 4. Render Success Alert Banner
        const modeBadge = isLiveBackend 
            ? `<span class="badge bg-success ms-2"><i class="fa-solid fa-server me-1"></i> Saved to MySQL Database (ID: #${alertRecord.id})</span>`
            : `<span class="badge bg-secondary ms-2"><i class="fa-solid fa-laptop me-1"></i> Standalone Browser Mode</span>`;

        showSystemAlert(
            `Emergency Broadcast Active for <strong>${escapeHtml(alertRecord.patientName)}</strong>! ${modeBadge}`,
            "danger"
        );

        // 5. Render Active Broadcast Card
        renderActiveAlertCard(alertRecord);

        // 6. Render Matching Results
        renderHospitalsList(matchedHospitals, alertRecord.bloodGroup);
        renderDonorsList(matchedDonors, alertRecord.bloodGroup);

        // 7. Increment active alert statistics
        incrementStatCounter("stat-active-alerts");

        // 8. Reset form inputs for next emergency
        form.reset();
        resetFormValidation(form);

        // 9. Smoothly scroll to results
        const resultsEl = document.getElementById("active-alert-container");
        if (resultsEl) {
            resultsEl.scrollIntoView({ behavior: "smooth", block: "start" });
        }

    } catch (err) {
        console.error("Emergency Alert Transmission Failed:", err);
        showSystemAlert(
            `Error broadcasting alert: ${escapeHtml(err.message)}. For immediate assistance, please call national helpline 108.`,
            "danger"
        );
    } finally {
        submitBtn.disabled = false;
        submitBtn.innerHTML = originalBtnHtml;
    }
}

// ==========================================================================
// 4. Live Match Preview (Triggered on Blood Group Select)
// ==========================================================================

async function previewMatches(bloodGroup) {
    if (CONFIG.USE_BACKEND_API) {
        try {
            const res = await fetch(`${CONFIG.API_BASE_URL}/alerts/match?bloodGroup=${encodeURIComponent(bloodGroup)}`);
            if (res.ok) {
                const json = await res.json();
                renderHospitalsList(json.data.matchingHospitals, bloodGroup);
                renderDonorsList(json.data.matchingDonors, bloodGroup);
                return;
            }
        } catch (e) {
            // Fall back to local rules if backend is not running
        }
    }
    renderHospitalsList(findMatchingHospitals(bloodGroup), bloodGroup);
    renderDonorsList(findMatchingDonors(bloodGroup), bloodGroup);
}

// ==========================================================================
// 5. Dynamic UI Renderers
// ==========================================================================

/**
 * Displays the active broadcast card above results
 */
function renderActiveAlertCard(alert) {
    const container = document.getElementById("active-alert-container");
    const summaryEl = document.getElementById("active-alert-summary");
    const timestampEl = document.getElementById("alert-timestamp");

    if (!container || !summaryEl) return;

    const timeString = new Date().toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" });
    if (timestampEl) timestampEl.textContent = `Broadcasted at ${timeString}`;

    summaryEl.innerHTML = `
        <div class="row g-3">
            <div class="col-md-6">
                <p class="mb-1 text-muted small">PATIENT NAME</p>
                <h5 class="fw-bold mb-2 text-dark">${escapeHtml(alert.patientName)}</h5>
                <p class="mb-1 text-muted small">REQUIRED BLOOD GROUP</p>
                <span class="badge bg-danger fs-6 px-3 py-2 fw-bold">${escapeHtml(alert.bloodGroup)}</span>
                <span class="badge bg-dark-navy text-white ms-2">Alert ID: #${alert.id}</span>
            </div>
            <div class="col-md-6">
                <p class="mb-1 text-muted small">LOCATION / HOSPITAL</p>
                <p class="fw-semibold mb-2"><i class="fa-solid fa-location-dot text-danger me-1"></i> ${escapeHtml(alert.location)}</p>
                <p class="mb-1 text-muted small">ATTENDANT EMERGENCY CONTACT</p>
                <p class="fw-semibold mb-0">
                    <i class="fa-solid fa-phone text-secondary me-1"></i> 
                    <a href="tel:${escapeHtml(alert.contactNumber)}" class="text-decoration-none text-dark fw-bold">${escapeHtml(alert.contactNumber)}</a>
                </p>
            </div>
            <div class="col-12 mt-2 pt-2 border-top">
                <p class="mb-1 text-muted small">CLINICAL EMERGENCY NOTE</p>
                <p class="fst-italic text-dark mb-0 bg-light p-2 rounded border">${escapeHtml(alert.message)}</p>
            </div>
        </div>
    `;

    container.classList.remove("d-none");
}

/**
 * Renders hospital cards into the DOM
 */
function renderHospitalsList(hospitals, bloodGroup) {
    const container = document.getElementById("hospitals-list");
    const countBadge = document.getElementById("hospitals-count");
    if (!container) return;

    if (countBadge) {
        countBadge.textContent = `${hospitals.length} Available`;
    }

    if (!hospitals || hospitals.length === 0) {
        container.innerHTML = `
            <div class="col-12 text-center text-muted py-4">
                <i class="fa-solid fa-triangle-exclamation fa-2x mb-2 text-warning"></i>
                <p class="mb-0">No registered hospitals currently report stock for <strong>${escapeHtml(bloodGroup)}</strong>.</p>
                <small>Voluntary donors below are being alerted immediately.</small>
            </div>
        `;
        return;
    }

    container.innerHTML = hospitals.map(h => {
        // Support both array or comma-separated string from backend MySQL entity
        const groups = Array.isArray(h.bloodGroupAvailable) 
            ? h.bloodGroupAvailable 
            : (h.bloodGroupAvailable || "").split(",").map(s => s.trim());

        return `
            <div class="col-12">
                <div class="result-card hospital-card d-flex flex-column flex-md-row justify-content-between align-items-md-center gap-3">
                    <div>
                        <div class="d-flex align-items-center gap-2 mb-1">
                            <h5 class="fw-bold mb-0 text-dark">${escapeHtml(h.hospitalName)}</h5>
                            <span class="badge bg-info-subtle text-info border border-info-subtle small">${escapeHtml(h.distanceKm || "Regional Center")}</span>
                        </div>
                        <p class="text-muted small mb-1">
                            <i class="fa-solid fa-map-pin me-1 text-danger"></i> ${escapeHtml(h.location)}
                        </p>
                        <div class="d-flex flex-wrap gap-1 align-items-center">
                            <small class="text-secondary me-1">Available Groups:</small>
                            ${groups.map(bg => `
                                <span class="badge ${bg === bloodGroup ? 'bg-danger text-white' : 'bg-light text-dark border'}">${escapeHtml(bg)}</span>
                            `).join("")}
                        </div>
                    </div>
                    <div class="d-flex flex-column align-items-md-end gap-2">
                        <span class="badge bg-success-subtle text-success border border-success-subtle px-2 py-1">
                            <i class="fa-solid fa-cubes-stacked me-1"></i> ${h.availableUnits || 10} Units Stocked
                        </span>
                        <a href="tel:${escapeHtml(h.contact)}" class="btn btn-outline-primary btn-sm px-3 rounded-pill">
                            <i class="fa-solid fa-phone me-1"></i> Call Blood Bank
                        </a>
                    </div>
                </div>
            </div>
        `;
    }).join("");
}

/**
 * Renders voluntary donor cards into the DOM
 */
function renderDonorsList(donors, bloodGroup) {
    const container = document.getElementById("donors-list");
    const countBadge = document.getElementById("donors-count");
    if (!container) return;

    if (countBadge) {
        countBadge.textContent = `${donors.length} Found`;
    }

    if (!donors || donors.length === 0) {
        container.innerHTML = `
            <div class="col-12 text-center text-muted py-4">
                <i class="fa-solid fa-circle-exclamation fa-2x mb-2 text-secondary"></i>
                <p class="mb-0">No active voluntary donors found matching <strong>${escapeHtml(bloodGroup)}</strong>.</p>
            </div>
        `;
        return;
    }

    container.innerHTML = donors.map(d => `
        <div class="col-12 col-md-6">
            <div class="result-card h-100 d-flex flex-column justify-content-between">
                <div>
                    <div class="d-flex justify-content-between align-items-start mb-2">
                        <div>
                            <h6 class="fw-bold mb-0 text-dark">${escapeHtml(d.name)}</h6>
                            <small class="text-muted"><i class="fa-solid fa-location-dot text-danger me-1"></i>${escapeHtml(d.location)}</small>
                        </div>
                        <span class="blood-badge blood-badge-danger">${escapeHtml(d.bloodGroup)}</span>
                    </div>
                    <div class="d-flex align-items-center gap-2 mb-3">
                        <span class="badge bg-success-subtle text-success border border-success-subtle" style="font-size: 0.75rem;">
                            <i class="fa-solid fa-check-circle me-1"></i> ${escapeHtml(d.status || "Available Now")}
                        </span>
                        <small class="text-muted" style="font-size: 0.75rem;">
                            <i class="fa-solid fa-heart text-danger me-1"></i> ${d.totalDonations || 0} Donations
                        </small>
                    </div>
                </div>
                <div>
                    <a href="tel:${escapeHtml(d.phone)}" class="btn btn-danger btn-sm w-100 rounded-pill">
                        <i class="fa-solid fa-phone me-1"></i> Call ${escapeHtml(d.phone)}
                    </a>
                </div>
            </div>
        </div>
    `).join("");
}

// ==========================================================================
// 6. GPS Geolocation Feature
// ==========================================================================

function handleGpsDetection() {
    const locationInput = document.getElementById("location");
    const gpsStatusEl = document.getElementById("gps-status");
    const gpsBtn = document.getElementById("btn-detect-gps");

    if (!navigator.geolocation) {
        if (gpsStatusEl) gpsStatusEl.textContent = "Geolocation is not supported by your browser.";
        return;
    }

    gpsBtn.disabled = true;
    if (gpsStatusEl) {
        gpsStatusEl.innerHTML = `<span class="spinner-border spinner-border-sm me-1"></span> Detecting GPS coordinates...`;
    }

    navigator.geolocation.getCurrentPosition(
        (position) => {
            const lat = position.coords.latitude.toFixed(4);
            const lng = position.coords.longitude.toFixed(4);
            
            locationInput.value = `GPS Coordinates: ${lat}, ${lng} (Emergency Location)`;
            markFieldValid(locationInput);

            if (gpsStatusEl) {
                gpsStatusEl.innerHTML = `<span class="text-success"><i class="fa-solid fa-check me-1"></i> Coordinates detected!</span>`;
            }
            gpsBtn.disabled = false;
        },
        (err) => {
            console.warn("Geolocation notice:", err.message);
            if (gpsStatusEl) {
                gpsStatusEl.innerHTML = `<span class="text-muted"><i class="fa-solid fa-circle-info me-1"></i> GPS permission skipped. Please enter hospital name manually.</span>`;
            }
            gpsBtn.disabled = false;
        },
        { timeout: 8000, enableHighAccuracy: true }
    );
}

// ==========================================================================
// 7. Backend Stats Synchronization & Utilities
// ==========================================================================

async function syncDashboardStats() {
    if (!CONFIG.USE_BACKEND_API) {
        updateDashboardStats(1, SAMPLE_HOSPITALS.length, SAMPLE_DONORS.length);
        return;
    }

    try {
        const response = await fetch(`${CONFIG.API_BASE_URL}/alerts/stats`);
        if (response.ok) {
            const json = await response.json();
            const stats = json.data;
            updateDashboardStats(stats.activeAlerts, stats.totalHospitals, stats.totalDonors);
            return;
        }
    } catch (e) {
        // If backend is not currently running, use fallback counts
    }
    updateDashboardStats(1, SAMPLE_HOSPITALS.length, SAMPLE_DONORS.length);
}

function updateDashboardStats(alerts, hospitals, donors) {
    const alertEl = document.getElementById("stat-active-alerts");
    const hospitalEl = document.getElementById("stat-hospitals");
    const donorEl = document.getElementById("stat-donors");

    if (alertEl) alertEl.textContent = alerts;
    if (hospitalEl) hospitalEl.textContent = hospitals;
    if (donorEl) donorEl.textContent = donors;
}

function incrementStatCounter(elementId) {
    const el = document.getElementById(elementId);
    if (el) {
        const current = parseInt(el.textContent, 10) || 0;
        el.textContent = current + 1;
    }
}

function showSystemAlert(message, type = "info") {
    const placeholder = document.getElementById("system-alert-placeholder");
    if (!placeholder) return;

    placeholder.innerHTML = `
        <div class="alert alert-${type} alert-dismissible fade show shadow-sm d-flex align-items-center gap-2" role="alert">
            <i class="fa-solid fa-triangle-exclamation fs-5"></i>
            <div>${message}</div>
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    `;
}

function escapeHtml(str) {
    if (!str) return "";
    return String(str)
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#039;");
}
