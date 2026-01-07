package com.safescape.app.models;

public class user {
    private String id;
    private String email;
    private String name;
    private String phone;
    private String gender;
    private String persona;
    private String profileImageUrl;
    private boolean emailVerified;
    private String emergencyContact1;
    private String emergencyContact2;
    private String language;
    private boolean notificationsEnabled;
    private int alertSensitivity;

    public user() {
    }

    public user(String email, String name, String phone) {
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.notificationsEnabled = true;
        this.alertSensitivity = 3;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getPersona() { return persona; }
    public void setPersona(String persona) { this.persona = persona; }

    public String getProfileImageUrl() { return profileImageUrl; }
    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public boolean isEmailVerified() { return emailVerified; }
    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public String getEmergencyContact1() { return emergencyContact1; }
    public void setEmergencyContact1(String emergencyContact1) {
        this.emergencyContact1 = emergencyContact1;
    }

    public String getEmergencyContact2() { return emergencyContact2; }
    public void setEmergencyContact2(String emergencyContact2) {
        this.emergencyContact2 = emergencyContact2;
    }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    public boolean isNotificationsEnabled() { return notificationsEnabled; }
    public void setNotificationsEnabled(boolean notificationsEnabled) {
        this.notificationsEnabled = notificationsEnabled;
    }

    public int getAlertSensitivity() { return alertSensitivity; }
    public void setAlertSensitivity(int alertSensitivity) {
        this.alertSensitivity = alertSensitivity;
    }
}