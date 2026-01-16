package com.safescape.app.utils;

import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.safescape.app.models.Incident;
import com.safescape.app.models.SafetyScore;

public class MapHelper {

    // Create circle overlay using MAP coordinates, not API
    public static CircleOptions createSafetyCircle(
            double lat,
            double lng,
            int color,
            int radiusMeters
    ) {
        LatLng center = new LatLng(lat, lng);

        return new CircleOptions()
                .center(center)
                .radius(radiusMeters)
                .fillColor(color)
                .strokeColor(color)
                .strokeWidth(2f);
    }

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

    public static int getRiskColor(double score) {
        if (score >= 75) {
            return 0x5500AA00; // translucent green
        } else if (score >= 45) {
            return 0x55FF8800; // translucent orange
        } else {
            return 0x55CC0000; // translucent red
        }
    }
}
