package com.safescape.app.utils;

public class Constants {
    // API Base URLs - CHANGE THESE TO YOUR SERVER IPs
    public static final String BASE_URL = "http://10.0.2.2:8080/api/";  // For emulator
    // If testing on real phone, use: "http://YOUR_COMPUTER_IP:8080/api/"
    public static final String PYTHON_ML_URL = "http://192.168.1.35:5000/";


    // Shared Preferences Keys
    public static final String PREF_NAME = "SafeScapePrefs";
    public static final String KEY_TOKEN = "jwt_token";
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_USER_EMAIL = "user_email";
    public static final String KEY_USER_NAME = "user_name";
    public static final String KEY_PERSONA = "user_persona";
    public static final String KEY_IS_LOGGED_IN = "is_logged_in";

    // Safety Score Thresholds
    public static final int SAFE_SCORE_MIN = 70;
    public static final int MODERATE_SCORE_MIN = 40;

    // Request Codes
    public static final int REQUEST_LOCATION_PERMISSION = 100;
    public static final int REQUEST_CAMERA_PERMISSION = 101;
    public static final int REQUEST_SMS_PERMISSION = 102;
    public static final int REQUEST_IMAGE_CAPTURE = 103;
    public static final int REQUEST_IMAGE_PICK = 104;

    // Map Colors (with transparency)
    public static final int COLOR_SAFE = 0x8800FF00;      // Green
    public static final int COLOR_MODERATE = 0x88FFFF00;  // Yellow
    public static final int COLOR_UNSAFE = 0x88FF0000;    // Red
}