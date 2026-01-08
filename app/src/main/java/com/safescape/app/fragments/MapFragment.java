package com.safescape.app.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import com.safescape.app.R;
import com.safescape.app.activities.SOSActivity;
import com.safescape.app.activities.AlertSettingsActivity;
import com.safescape.app.api.ApiClient;
import com.safescape.app.api.ApiService;
import com.safescape.app.models.*;
import com.safescape.app.utils.MapHelper;
import com.safescape.app.utils.PermissionHelper;
import com.safescape.app.utils.SharedPrefsManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private Location currentLocation;
    private ApiService apiService;
    private SharedPrefsManager prefsManager;
    private Geocoder geocoder;

    // UI Elements
    private CardView safetyScoreCard;
    private TextView tvSafetyScore, tvRiskLevel, tvLocationName, tvCrimeCount;
    private FloatingActionButton fabSOS;
    private Button btnAlertSettings;

    // Map overlays
    private List<Circle> safetyCircles = new ArrayList<>();
    private List<Marker> safeZoneMarkers = new ArrayList<>();
    private List<Marker> incidentMarkers = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_map, container, false);

        // Initialize API (Python ML server)
        apiService = ApiClient.getMLClient().create(ApiService.class);
        prefsManager = SharedPrefsManager.getInstance(requireContext());
        geocoder = new Geocoder(requireContext(), Locale.getDefault());

        // UI bindings
        safetyScoreCard = view.findViewById(R.id.safetyScoreCard);
        tvSafetyScore = view.findViewById(R.id.tvSafetyScore);
        tvRiskLevel = view.findViewById(R.id.tvRiskLevel);
        tvLocationName = view.findViewById(R.id.tvLocationName);
        tvCrimeCount = view.findViewById(R.id.tvCrimeCount);

        // Alert Settings Button
        btnAlertSettings = view.findViewById(R.id.btnAlertSettings);
        btnAlertSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AlertSettingsActivity.class);
                startActivity(intent);
            }
        });

        // SOS Button
        fabSOS = view.findViewById(R.id.fabSOS);
        fabSOS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireActivity(), SOSActivity.class);
                startActivity(intent);
            }
        });

        // Location client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        // Permission check
        if (PermissionHelper.hasLocationPermission(requireContext())) {
            getCurrentLocation();
        } else {
            PermissionHelper.requestLocationPermission(requireActivity());
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager()
                        .findFragmentById(R.id.map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setScrollGesturesEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        if (PermissionHelper.hasLocationPermission(requireContext())) {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
            }
        }

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                getLocationNameAndFetchScore(latLng.latitude, latLng.longitude);
            }
        });

        if (currentLocation != null) {
            showLocationOnMap();
            getLocationNameAndFetchScore(
                    currentLocation.getLatitude(),
                    currentLocation.getLongitude()
            );
        } else {
            LatLng pune = new LatLng(18.5204, 73.8567);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pune, 12));
        }
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        Task<Location> task = fusedLocationClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location;
                    if (mMap != null) {
                        showLocationOnMap();
                        getLocationNameAndFetchScore(
                                location.getLatitude(),
                                location.getLongitude()
                        );
                    }
                }
            }
        });
    }

    private void showLocationOnMap() {
        if (currentLocation != null && mMap != null) {
            LatLng latLng = new LatLng(
                    currentLocation.getLatitude(),
                    currentLocation.getLongitude()
            );
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));
        }
    }

    private void getLocationNameAndFetchScore(double latitude, double longitude) {
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);

                String locationName = address.getLocality();
                if (locationName == null || locationName.isEmpty()) {
                    locationName = address.getSubAdminArea();
                }
                if (locationName == null || locationName.isEmpty()) {
                    locationName = address.getAdminArea();
                }

                if (locationName != null && !locationName.isEmpty()) {
                    fetchSafetyScore(locationName, latitude, longitude);
                }
            }
        } catch (IOException e) {
            Toast.makeText(requireContext(),
                    "Geocoding error: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchSafetyScore(String locationName,
                                  double latitude,
                                  double longitude) {

        safetyScoreCard.setVisibility(View.VISIBLE);
        tvSafetyScore.setText("...");
        tvRiskLevel.setText("Loading...");
        tvLocationName.setText(locationName);

        Call<SafetyScoreResponse> call =
                apiService.getSafetyScore(locationName);

        call.enqueue(new Callback<SafetyScoreResponse>() {
            @Override
            public void onResponse(Call<SafetyScoreResponse> call,
                                   Response<SafetyScoreResponse> response) {

                if (response.isSuccessful() && response.body() != null) {
                    SafetyScore score = response.body().getData();
                    if (score != null) {
                        score.setLatitude(latitude);
                        score.setLongitude(longitude);
                        displaySafetyScore(score);
                        drawSafetyCircle(score);
                    } else {
                        safetyScoreCard.setVisibility(View.GONE);
                    }
                } else {
                    safetyScoreCard.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<SafetyScoreResponse> call, Throwable t) {
                Toast.makeText(requireContext(),
                        "Error: " + t.getMessage() +
                                "\nMake sure Python server is running!",
                        Toast.LENGTH_LONG).show();
                safetyScoreCard.setVisibility(View.GONE);
            }
        });
    }

    private void displaySafetyScore(SafetyScore score) {
        safetyScoreCard.setVisibility(View.VISIBLE);
        tvSafetyScore.setText(String.format("%.1f", score.getSafety_score()));
        tvRiskLevel.setText(score.getRiskDisplayText());
        tvRiskLevel.setTextColor(
                MapHelper.getRiskColor(score.getSafety_score())
        );
        tvLocationName.setText(score.getLocation());
        tvCrimeCount.setText(
                score.getTotal_crimes() + " total crimes\n" +
                        score.getViolent_crimes() + " violent crimes"
        );
    }

    private void drawSafetyCircle(SafetyScore score) {
        for (Circle circle : safetyCircles) {
            circle.remove();
        }
        safetyCircles.clear();
        safetyCircles.add(
                mMap.addCircle(
                        MapHelper.createSafetyCircle(score, 500)
                )
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == PermissionHelper.LOCATION_PERMISSION_CODE &&
                grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            getCurrentLocation();
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
            }
        }
    }
}
