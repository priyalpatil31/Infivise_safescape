package com.safescape.app.models;

import com.google.gson.annotations.SerializedName;

public class SafetyScore {

    @SerializedName("location")
    private String location;

    @SerializedName("safety_score")
    private double safetyScore;

    @SerializedName("risk_rating")
    private String riskRating;

    @SerializedName("total_crimes")
    private int totalCrimes;

    @SerializedName("violent_crimes")
    private int violentCrimes;

    // -------- GETTERS --------
    public String getLocation() {
        return location;
    }

    public double getSafetyScore() {
        return safetyScore;
    }

    public String getRiskRating() {
        return riskRating;
    }

    public int getTotalCrimes() {
        return totalCrimes;
    }

    public int getViolentCrimes() {
        return violentCrimes;
    }

    // -------- UI HELPERS --------
    // Matches YOUR ML logic (Safe / Moderate / Unsafe)
    public int getColor() {
        if (safetyScore >= 70) {
            return 0x8800FF00; // Green
        } else if (safetyScore >= 40) {
            return 0x88FFFF00; // Yellow
        } else {
            return 0x88FF0000; // Red
        }
    }

    public String getRiskDisplayText() {
        switch (riskRating) {
            case "Safe":
                return "Safe Area ✓";
            case "Moderate":
                return "Moderate Risk ⚠";
            case "Unsafe":
                return "High Risk ⚠️";
            default:
                return "Unknown";
        }
    }
}
