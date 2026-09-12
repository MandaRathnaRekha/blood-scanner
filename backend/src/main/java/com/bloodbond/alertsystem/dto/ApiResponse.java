package com.bloodbond.alertsystem.dto;

import java.time.LocalDateTime;

/**
 * LifePulse - Emergency Blood Alert System
 * DTO: ApiResponse<T>
 * A generic, standardized response envelope returned by all REST controllers.
 * 
 * @param <T> The type of the payload object contained in the 'data' field.
 */
public class ApiResponse<T> {

    // Indicates whether the operation succeeded or failed
    private boolean success;

    // Human-readable status or informational message
    private String message;

    // The actual response payload (e.g., Alert object, List of Donors, etc.)
    private T data;

    // Timestamp when the response was generated on the server
    private LocalDateTime timestamp;

    /**
     * Default No-Args Constructor
     */
    public ApiResponse() {
        this.timestamp = LocalDateTime.now();
    }

    /**
     * Parameterized Constructor
     */
    public ApiResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }

    // =========================================================================
    // Static Convenience Factory Methods
    // Simplifies returning standard responses in Controllers
    // =========================================================================

    /**
     * Generates a successful response with payload
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }

    /**
     * Generates a failure or error response without payload
     */
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null);
    }

    // =========================================================================
    // Getters and Setters
    // =========================================================================

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
