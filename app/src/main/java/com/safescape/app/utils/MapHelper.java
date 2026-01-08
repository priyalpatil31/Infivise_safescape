package com.safescape.app.utils;

import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.safescape.app.models.Incident;
import com.safescape.app.models.SafetyScore;

public class MapHelper {

    // Create circle overlay for safety score (from ML backend)
    public static CircleOptions createSafetyCircle(SafetyScore safetyScore, int radiusMeters) {
        LatLng center = new LatLng(
                safetyScore.getLatitude(),
                safetyScore.getLongitude()
        );

        return new CircleOptions()
                .center(center)
                .radius(radiusMeters)
                .fillColor(safetyScore.getColor())
                .strokeColor(safetyScore.getColor())
                .strokeWidth(2);
    }

    // Create marker for incident
    public static MarkerOptions createIncidentMarker(Incident incident) {
        LatLng position = new LatLng(
                incident.getLatitude(),
                incident.getLongitude()
        );

        return new MarkerOptions()
                .position(position)
                .title(incident.getCategory().toUpperCase())
                .snippet(incident.getDescription())
                .icon(BitmapDescriptorFactory.defaultMarker(incident.getMarkerHue()))
                .alpha(0.8f);
    }

    // Get risk color (for text, UI elements)
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
