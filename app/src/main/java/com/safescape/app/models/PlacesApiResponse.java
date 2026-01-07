package com.safescape.app.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class PlacesApiResponse {
    @SerializedName("results")
    private List<PlaceResult> results;

    @SerializedName("status")
    private String status;

    public List<PlaceResult> getResults() {
        return results;
    }

    public String getStatus() {
        return status;
    }

    public static class PlaceResult {
        @SerializedName("place_id")
        private String placeId;

        @SerializedName("name")
        private String name;

        @SerializedName("vicinity")
        private String vicinity;

        @SerializedName("geometry")
        private Geometry geometry;

        @SerializedName("types")
        private List<String> types;

        @SerializedName("opening_hours")
        private OpeningHours openingHours;

        @SerializedName("formatted_phone_number")
        private String phoneNumber;

        public String getPlaceId() { return placeId; }
        public String getName() { return name; }
        public String getVicinity() { return vicinity; }
        public Geometry getGeometry() { return geometry; }
        public List<String> getTypes() { return types; }
        public OpeningHours getOpeningHours() { return openingHours; }
        public String getPhoneNumber() { return phoneNumber; }
    }

    public static class Geometry {
        @SerializedName("location")
        private LocationCoords location;

        public LocationCoords getLocation() { return location; }
    }

    public static class LocationCoords {
        @SerializedName("lat")
        private double lat;

        @SerializedName("lng")
        private double lng;

        public double getLat() { return lat; }
        public double getLng() { return lng; }
    }

    public static class OpeningHours {
        @SerializedName("open_now")
        private boolean openNow;

        public boolean isOpenNow() { return openNow; }
    }
}