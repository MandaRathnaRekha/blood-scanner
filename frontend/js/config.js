/**
 * LifePulse - Emergency Blood Alert System
 * File: frontend/js/config.js
 * Description: Dynamic runtime environment configuration for frontend portal.
 *              Allows setting or switching between local backend, Railway, Render,
 *              or custom cloud API URLs without editing code.
 */
window.APP_CONFIG = {
    // Dynamically resolves API URL:
    // 1. Checks localStorage ('LIFEPULSE_API_URL') for quick runtime switching / testing
    // 2. Falls back to window environment or deployed Railway Spring Boot backend
    API_BASE_URL: window.localStorage.getItem("LIFEPULSE_API_URL") || "https://blood-scanner-production.up.railway.app/api",
    
    // Flag to enable live REST fetch() requests against the Spring Boot backend
    USE_BACKEND_API: true,

    /**
     * Helper to update the backend API URL dynamically from the browser console or settings
     * Example: setBackendUrl("https://lifepulse-backend.up.railway.app/api")
     */
    setBackendUrl: function(url) {
        if (!url) {
            localStorage.removeItem("LIFEPULSE_API_URL");
        } else {
            localStorage.setItem("LIFEPULSE_API_URL", url.replace(/\/+$/, ""));
        }
        window.location.reload();
    }
};
