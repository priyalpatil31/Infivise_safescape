package com.safescape.app.models;

public class AuthRequest {
    private String email;
    private String password;
    private String name;
    private String phone;

    // Login constructor
    public AuthRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // Signup constructor
    public AuthRequest(String email, String password, String name, String phone) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.phone = phone;
    }

    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getName() { return name; }
    public String getPhone() { return phone; }
}