package com.safescape.app.api;

import com.safescape.app.models.*;
import retrofit2.Call;
import retrofit2.http.*;
import java.util.List;
import java.util.Map;

public interface ApiService {

    // ========== AUTH APIs ==========
    @POST("auth/signup")
    Call<AuthResponse> signup(@Body AuthRequest request);

    @POST("auth/login")
    Call<AuthResponse> login(@Body AuthRequest request);

    @GET("auth/verify")
    Call<AuthResponse> verifyToken(@Header("Authorization") String token);

    @PUT("auth/updatePersona")
    Call<user> updatePersona(
            @Header("Authorization") String token,
            @Body Map<String, String> personaData
    );

    // ========== SAFETY SCORE APIs (Connected to your Python ML model) ==========
    @GET("predict")  // Your Python API endpoint
    Call<SafetyScoreResponse> getSafetyScore(
            @Query("location") String locationName
    );

    // ❌ DELETED: getSafeZones() method

    // ========== INCIDENT REPORTING APIs ==========
    @GET("incidents/nearby")  // Endpoint for incidents
    Call<List<Incident>> getNearbyIncidents(
            @Query("lat") double latitude,
            @Query("lng") double longitude,
            @Query("radius") int radiusKm,
            @Query("hours") int pastHours
    );

    @POST("incidents/report")
    Call<IncidentResponse> reportIncident(
            @Header("Authorization") String token,
            @Body IncidentRequest request
    );
}
