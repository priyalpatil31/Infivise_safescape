package com.safescape.app.services;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.safescape.app.MainActivity;
import com.safescape.app.R;
import com.safescape.app.utils.SharedPrefsManager;

public class LocationTrackingService extends Service {

    private static final String TAG = "LocationTrackingService";
    private static final String CHANNEL_ID = "location_tracking";
    private static final int NOTIFICATION_ID = 1001;

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private SharedPrefsManager prefsManager;

    private Location lastLocation;
    private double lastSafetyScore = -1;

    @Override
    public void onCreate() {
        super.onCreate();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        prefsManager = SharedPrefsManager.getInstance(this);

        setupLocationCallback();
        createNotificationChannel();
        startForeground(NOTIFICATION_ID, createNotification());
        startLocationUpdates();

        Log.d(TAG, "Location tracking service started");
    }

    private void setupLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }

                for (Location location : locationResult.getLocations()) {
                    handleNewLocation(location);
                }
            }
        };
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "Location permission not granted");
            stopSelf();
            return;
        }

        // Get persona for alert sensitivity
        String persona = prefsManager.getPersona();
        long updateInterval = getUpdateIntervalForPersona(persona);

        LocationRequest locationRequest = new LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY,
                updateInterval
        )
                .setMinUpdateIntervalMillis(updateInterval / 2)
                .setMaxUpdateDelayMillis(updateInterval * 2)
                .build();

        fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
        );

        Log.d(TAG, "Location updates started with interval: " + updateInterval + "ms");
    }

    private long getUpdateIntervalForPersona(String persona) {
        // Different personas get different update frequencies
        switch (persona) {
            case "Solo Female Traveler":
                return 30000; // 30 seconds - most frequent
            case "Backpacker":
                return 45000; // 45 seconds
            case "Family Traveler":
                return 60000; // 1 minute
            case "Business Traveler":
                return 90000; // 1.5 minutes
            default:
                return 60000; // 1 minute default
        }
    }

    private void handleNewLocation(Location location) {
        Log.d(TAG, "New location: " + location.getLatitude() + ", " + location.getLongitude());

        // Check if location changed significantly
        if (lastLocation != null) {
            float distance = lastLocation.distanceTo(location);

            // Only check if moved more than 50 meters
            if (distance < 50) {
                return;
            }
        }

        lastLocation = location;

        // Check if alerts are enabled
        if (!prefsManager.areAlertsEnabled()) {
            return;
        }

        // Check safety at new location
        checkSafetyAtLocation(location);
    }

    private void checkSafetyAtLocation(Location location) {
        // Mock safety check - replace with actual API call later
        double mockSafetyScore = 40 + (Math.random() * 60); // Random 40-100

        String persona = prefsManager.getPersona();
        int sensitivity = prefsManager.getAlertSensitivity();

        double alertThreshold = getAlertThresholdForPersonaAndSensitivity(persona, sensitivity);

        // Send alert if safety score is below threshold
        if (mockSafetyScore < alertThreshold) {
            sendSafetyAlert(mockSafetyScore, location);
        }

        lastSafetyScore = mockSafetyScore;
    }

    private double getAlertThresholdForPersonaAndSensitivity(String persona, int sensitivity) {
        // Base thresholds for different personas
        double baseThreshold;
        switch (persona) {
            case "Solo Female Traveler":
                baseThreshold = 60.0; // Most sensitive
                break;
            case "Backpacker":
                baseThreshold = 55.0;
                break;
            case "Family Traveler":
                baseThreshold = 50.0;
                break;
            case "Business Traveler":
                baseThreshold = 45.0;
                break;
            default:
                baseThreshold = 50.0;
        }

        // Adjust based on user's sensitivity setting (0-4)
        // 0 = Very Low, 1 = Low, 2 = Medium, 3 = High, 4 = Very High
        double adjustment = (sensitivity - 2) * 10; // -20 to +20

        return baseThreshold + adjustment;
    }

    private void sendSafetyAlert(double safetyScore, Location location) {
        String title = "⚠️ Safety Alert";
        String message = "You are entering an area with safety score: "
                + String.format("%.1f/100", safetyScore);

        if (safetyScore < 45) {
            title = "🚨 High Risk Area";
            message = "Warning: High risk area detected! Stay alert and consider alternate routes.";
        } else if (safetyScore < 60) {
            title = "⚠️ Moderate Risk Area";
            message = "Caution: Moderate risk area. Stay aware of your surroundings.";
        }

        // Create alert notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "safety_alerts")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setVibrate(new long[]{0, 500, 200, 500});

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Create channel if needed
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "safety_alerts",
                    "Safety Alerts",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.enableVibration(true);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify((int) System.currentTimeMillis(), builder.build());

        Log.d(TAG, "Safety alert sent: " + message);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Location Tracking",
                    NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("Continuous location monitoring for safety");

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private Notification createNotification() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("SafeScape Protection Active")
                .setContentText("Monitoring your location for safety")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .build();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (fusedLocationClient != null && locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }

        Log.d(TAG, "Location tracking service stopped");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}