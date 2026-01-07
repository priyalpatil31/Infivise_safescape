package com.safescape.app.models;

public class SafetyScore {
    private String location;
    private double latitude;
    private double longitude;
    private double safety_score; // 0-100 from your ML model
    private String risk_rating; // "Positive", "Negative", "Neutral" from your ML model
    private double confidence;
    private int total_crimes;
    private int violent_crimes;
    private String most_common_crime;

    public SafetyScore() {
    }

    public SafetyScore(String location, double latitude, double longitude,
                       double safety_score, String risk_rating) {
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
        this.safety_score = safety_score;
        this.risk_rating = risk_rating;
    }

    // Getters and Setters
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public double getSafety_score() { return safety_score; }
    public void setSafety_score(double safety_score) { this.safety_score = safety_score; }

    public String getRisk_rating() { return risk_rating; }
    public void setRisk_rating(String risk_rating) { this.risk_rating = risk_rating; }

    public double getConfidence() { return confidence; }
    public void setConfidence(double confidence) { this.confidence = confidence; }

    public int getTotal_crimes() { return total_crimes; }
    public void setTotal_crimes(int total_crimes) { this.total_crimes = total_crimes; }

    public int getViolent_crimes() { return violent_crimes; }
    public void setViolent_crimes(int violent_crimes) { this.violent_crimes = violent_crimes; }

    public String getMost_common_crime() { return most_common_crime; }
    public void setMost_common_crime(String most_common_crime) {
        this.most_common_crime = most_common_crime;
    }

    // Helper method to get color based on YOUR ML model's scoring
    // Higher score = safer (your model: 75+ = safe, 45-75 = moderate, <45 = unsafe)
    public int getColor() {
        if (safety_score >= 75) {
            return 0x8800FF00; // Green with transparency (Safe)
        } else if (safety_score >= 45) {
            return 0x88FFFF00; // Yellow with transparency (Moderate)
        } else {
            return 0x88FF0000; // Red with transparency (High Risk)
        }
    }

    // Get display text for risk rating
    public String getRiskDisplayText() {
        switch (risk_rating) {
            case "Neutral":
                return "Safe Area ✓";
            case "Negative":
                return "Moderate Risk ⚠";
            case "Positive":
                return "High Risk ⚠️";
            default:
                return "Unknown";
        }
    }
}
