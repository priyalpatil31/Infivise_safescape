package com.safescape.app.models;

public class AuthResponse {
    private boolean success;
    private String message;
    private String token;
    private user user;

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public user getUser() { return user; }
    public void setUser(user user) { this.user = user; }
}