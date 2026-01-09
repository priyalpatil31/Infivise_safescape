package com.safescape.app.api;

import com.safescape.app.models.*;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.*;

public interface ApiService {

    // ========== AUTH ==========
    @POST("auth/signup")
    Call<AuthResponse> signup(@Body AuthRequest request);

    @POST("auth/login")
    Call<AuthResponse> login(@Body AuthRequest request);

    @GET("auth/verify")
    Call<AuthResponse> verifyToken(
            @Header("Authorization") String token
    );

    @PUT("auth/updatePersona")
    Call<user> updatePersona(
            @Header("Authorization") String token,
            @Body Map<String, String> personaData
    );

    // ========== ML (Python server) ==========
    @GET("predict")
    Call<SafetyScoreResponse> getSafetyScore(
            @Query("location") String location
    );

    // ========== INCIDENTS ==========
    @GET("incidents/nearby")
    Call<List<Incident>> getNearbyIncidents(
            @Query("lat") double latitude,
            @Query("lng") double longitude,
            @Query("radius") int radiusKm,
            @Query("hours") int pastHours
    );

    // ========== TEST ==========
    @GET("/")
    Call<TestResponse> testBackend();
}


