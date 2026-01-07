package com.safescape.app.utils;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.safescape.app.models.Incident;
import com.safescape.app.models.SafetyScore;

public class MapHelper {

    // Create circle overlay for safety zone (based on YOUR ML model's scoring)
    public static CircleOptions createSafetyCircle(SafetyScore safetyScore, int radiusMeters) {
        LatLng center = new LatLng(safetyScore.getLatitude(), safetyScore.getLongitude());

        return new CircleOptions()
                .center(center)
                .radius(radiusMeters)
                .fillColor(safetyScore.getColor())
                .strokeColor(safetyScore.getColor())
                .strokeWidth(2);
    }

    // Create marker for safe zone
    public static MarkerOptions createSafeZoneMarker(SafeZone safeZone) {
        LatLng position = new LatLng(safeZone.getLatitude(), safeZone.getLongitude());

        float markerColor;
        switch (safeZone.getType()) {
            case "hospital":
                markerColor = BitmapDescriptorFactory.HUE_RED;
                break;
            case "police_station":
                markerColor = BitmapDescriptorFactory.HUE_BLUE;
                break;
            case "cafe":
                markerColor = BitmapDescriptorFactory.HUE_GREEN;
                break;
            default:
                markerColor = BitmapDescriptorFactory.HUE_CYAN;
        }

        return new MarkerOptions()
                .position(position)
                .title(safeZone.getName())
                .snippet(safeZone.getType().replace("_", " ").toUpperCase())
                .icon(BitmapDescriptorFactory.defaultMarker(markerColor));
    }

    // Create marker for incident
    public static MarkerOptions createIncidentMarker(Incident incident) {
        LatLng position = new LatLng(incident.getLatitude(), incident.getLongitude());

        return new MarkerOptions()
                .position(position)
                .title(incident.getCategory().toUpperCase())
                .snippet(incident.getDescription())
                .icon(BitmapDescriptorFactory.defaultMarker(incident.getMarkerHue()))
                .alpha(0.8f);
    }

    // Get risk color (solid, not transparent - for text)
    public static int getRiskColor(double score) {
        if (score >= 75) {
            return 0xFF00AA00; // Dark Green
        } else if (score >= 45) {
            return 0xFFFF8800; // Orange
        } else {
            return 0xFFCC0000; // Dark Red
        }
    }
}