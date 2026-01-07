package com.safescape.app.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.safescape.app.MainActivity;
import com.safescape.app.R;
import com.safescape.app.utils.SharedPrefsManager;

public class FCMService extends FirebaseMessagingService {

    private static final String TAG = "FCMService";
    private static final String CHANNEL_ID = "safety_alerts";
    private static final String CHANNEL_NAME = "Safety Alerts";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.d(TAG, "Message received from: " + remoteMessage.getFrom());

        // Check if alerts are enabled
        SharedPrefsManager prefsManager = SharedPrefsManager.getInstance(this);
        if (!prefsManager.areAlertsEnabled()) {
            Log.d(TAG, "Alerts disabled by user");
            return;
        }

        // Check if message contains a notification payload
        if (remoteMessage.getNotification() != null) {
            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();

            Log.d(TAG, "Notification Title: " + title);
            Log.d(TAG, "Notification Body: " + body);

            sendNotification(title, body);
        }

        // Check if message contains a data payload
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            String alertType = remoteMessage.getData().get("alert_type");
            String safetyScore = remoteMessage.getData().get("safety_score");
            String location = remoteMessage.getData().get("location");

            handleDataPayload(alertType, safetyScore, location);
        }
    }

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        Log.d(TAG, "New FCM token: " + token);

        // Save token to SharedPreferences
        SharedPrefsManager prefsManager = SharedPrefsManager.getInstance(this);
        prefsManager.setFCMToken(token);

        // TODO: Send token to your backend server
    }

    private void handleDataPayload(String alertType, String safetyScore, String location) {
        String title = "⚠️ Safety Alert";
        String body = "You are entering an unsafe area";

        if (alertType != null) {
            switch (alertType) {
                case "high_risk":
                    title = "🚨 High Risk Area";
                    body = "Warning: You are entering a high-risk area. Stay alert!";
                    break;
                case "moderate_risk":
                    title = "⚠️ Moderate Risk Area";
                    body = "Caution: This area has moderate safety concerns.";
                    break;
                case "crime_reported":
                    title = "📢 Recent Crime Alert";
                    body = "Crime reported nearby in the last 24 hours.";
                    break;
            }
        }

        if (location != null) {
            body += "\nLocation: " + location;
        }

        if (safetyScore != null) {
            body += "\nSafety Score: " + safetyScore + "/100";
        }

        sendNotification(title, body);
    }

    private void sendNotification(String title, String messageBody) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentTitle(title)
                        .setContentText(messageBody)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(messageBody))
                        .setAutoCancel(true)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setContentIntent(pendingIntent)
                        .setVibrate(new long[]{0, 500, 200, 500});

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Create notification channel for Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Notifications for safety alerts");
            channel.enableVibration(true);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify((int) System.currentTimeMillis(), notificationBuilder.build());
    }
}