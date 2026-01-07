package com.safescape.app.models;

public class SafetyScoreResponse {
    private boolean success;
    private SafetyScore data;  // Your Python API returns this
    private String message;

    // Getters and Setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public SafetyScore getData() { return data; }
    public void setData(SafetyScore data) { this.data = data; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}