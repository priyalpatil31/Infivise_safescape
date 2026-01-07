package com.safescape.app.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.telephony.SmsManager;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.safescape.app.models.EmergencyContact;
import java.util.List;

public class SOSHelper {

    // Send SOS SMS to all emergency contacts
    public static void sendSOSMessages(Context context, Location location,
                                       EmergencyContactsManager contactsManager) {
        // Check SMS permission
        if (!PermissionHelper.hasSmsPermission(context)) {
            Toast.makeText(context,
                    "SMS permission required to send SOS",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Get emergency contacts
        List<EmergencyContact> contacts = contactsManager.getContacts();

        if (contacts.isEmpty()) {
            Toast.makeText(context,
                    "No emergency contacts added! Please add contacts first.",
                    Toast.LENGTH_LONG).show();
            return;
        }

        // Prepare SOS message
        String message = buildSOSMessage(context, location);

        // Send SMS to each contact
        SmsManager smsManager = SmsManager.getDefault();
        int sentCount = 0;

        for (EmergencyContact contact : contacts) {
            try {
                smsManager.sendTextMessage(
                        contact.getPhone(),
                        null,
                        message,
                        null,
                        null
                );
                sentCount++;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (sentCount > 0) {
            Toast.makeText(context,
                    "SOS sent to " + sentCount + " contact(s)!",
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context,
                    "Failed to send SOS. Please try again.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    // Build SOS message with location
    private static String buildSOSMessage(Context context, Location location) {
        StringBuilder message = new StringBuilder();
        message.append("🚨 EMERGENCY ALERT from SafeScape App!\n\n");
        message.append("I need help! This is an automated emergency message.\n\n");

        if (location != null) {
            double lat = location.getLatitude();
            double lng = location.getLongitude();

            message.append("📍 My Location:\n");
            message.append("Latitude: ").append(lat).append("\n");
            message.append("Longitude: ").append(lng).append("\n\n");

            // Google Maps link
            String mapsUrl = "https://www.google.com/maps?q=" + lat + "," + lng;
            message.append("🗺️ View on Map:\n").append(mapsUrl).append("\n\n");
        } else {
            message.append("⚠️ Location unavailable\n\n");
        }

        message.append("Please contact me immediately!");

        return message.toString();
    }

    // Make emergency call
    public static void makeEmergencyCall(Context context, String phoneNumber) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + phoneNumber));

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context,
                    "Phone call permission required",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        context.startActivity(callIntent);
    }

    // Dial number (opens dialer, doesn't auto-call)
    public static void dialNumber(Context context, String phoneNumber) {
        Intent dialIntent = new Intent(Intent.ACTION_DIAL);
        dialIntent.setData(Uri.parse("tel:" + phoneNumber));
        context.startActivity(dialIntent);
    }
}