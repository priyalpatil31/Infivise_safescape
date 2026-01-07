package com.safescape.app.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefsManager {
    private static SharedPrefsManager instance;
    private SharedPreferences prefs;

    private SharedPrefsManager(Context context) {
        prefs = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized SharedPrefsManager getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPrefsManager(context.getApplicationContext());
        }
        return instance;
    }

    // ---------------- EXISTING METHODS (UNCHANGED) ----------------

    public void saveLoginData(String token, String userId, String email, String name) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.KEY_TOKEN, token);
        editor.putString(Constants.KEY_USER_ID, userId);
        editor.putString(Constants.KEY_USER_EMAIL, email);
        editor.putString(Constants.KEY_USER_NAME, name);
        editor.putBoolean(Constants.KEY_IS_LOGGED_IN, true);
        editor.apply();
    }

    public String getToken() {
        return prefs.getString(Constants.KEY_TOKEN, null);
    }

    public String getAuthHeader() {
        String token = getToken();
        return token != null ? "Bearer " + token : null;
    }

    public boolean isLoggedIn() {
        return prefs.getBoolean(Constants.KEY_IS_LOGGED_IN, false);
    }

    public void savePersona(String persona) {
        prefs.edit().putString(Constants.KEY_PERSONA, persona).apply();
    }

    public String getPersona() {
        return prefs.getString(Constants.KEY_PERSONA, "General");
    }

    public String getUserName() {
        return prefs.getString(Constants.KEY_USER_NAME, "User");
    }

    public void logout() {
        prefs.edit().clear().apply();
    }

    // ---------------- NEW ALERT-RELATED METHODS (STEP 2) ----------------

    public boolean areAlertsEnabled() {
        return prefs.getBoolean("alerts_enabled", true);
    }

    public void setAlertsEnabled(boolean enabled) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("alerts_enabled", enabled);
        editor.apply();
    }

    public boolean isBackgroundTrackingEnabled() {
        return prefs.getBoolean("background_tracking", false);
    }

    public void setBackgroundTrackingEnabled(boolean enabled) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("background_tracking", enabled);
        editor.apply();
    }

    public int getAlertSensitivity() {
        // 0–4 scale, default = Medium (2)
        return prefs.getInt("alert_sensitivity", 2);
    }

    public void setAlertSensitivity(int sensitivity) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("alert_sensitivity", sensitivity);
        editor.apply();
    }

    public String getFCMToken() {
        return prefs.getString("fcm_token", "");
    }

    public void setFCMToken(String token) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("fcm_token", token);
        editor.apply();
    }
}
