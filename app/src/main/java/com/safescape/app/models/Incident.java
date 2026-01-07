package com.safescape.app.models;

public class Incident {
    private String id;
    private String category; // "theft", "harassment", "scam", "unsafe_area"
    private String description;
    private double latitude;
    private double longitude;
    private String reportedBy;
    private String timestamp;
    private String imageUrl;
    private int trustScore; // 0-100
    private boolean isVerified;

    public Incident() {
    }

    public Incident(String category, String description, double latitude, double longitude) {
        this.category = category;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public String getReportedBy() { return reportedBy; }
    public void setReportedBy(String reportedBy) { this.reportedBy = reportedBy; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public int getTrustScore() { return trustScore; }
    public void setTrustScore(int trustScore) { this.trustScore = trustScore; }

    public boolean isVerified() { return isVerified; }
    public void setVerified(boolean verified) { isVerified = verified; }

    // Get marker color based on category
    public float getMarkerHue() {
        switch (category) {
            case "theft":
                return 0f; // Red
            case "harassment":
                return 330f; // Pink
            case "scam":
                return 30f; // Orange
            case "unsafe_area":
                return 15f; // Dark orange
            default:
                return 0f;
        }
    }
}