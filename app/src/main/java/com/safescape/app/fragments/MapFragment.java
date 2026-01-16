package com.safescape.app.fragments;

import android.content.Intent;
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
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.tasks.Task;

import com.safescape.app.R;
import com.safescape.app.activities.SOSActivity;
import com.safescape.app.activities.AlertSettingsActivity;
import com.safescape.app.api.ApiClient;
import com.safescape.app.api.ApiService;
import com.safescape.app.models.SafetyScore;
import com.safescape.app.models.SafetyScoreResponse;
import com.safescape.app.utils.MapHelper;
import com.safescape.app.utils.PermissionHelper;

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
    private ApiService apiService;
    private Geocoder geocoder;

    // UI
    private CardView safetyScoreCard;
    private TextView tvSafetyScore, tvRiskLevel, tvLocationName, tvCrimeCount;
    private FloatingActionButton fabSOS;
    private Button btnAlertSettings;

    private final List<Circle> safetyCircles = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {

        View view = inflater.inflate(R.layout.fragment_map, container, false);

        apiService = ApiClient.getMLClient().create(ApiService.class);
        geocoder = new Geocoder(requireContext(), Locale.getDefault());

        safetyScoreCard = view.findViewById(R.id.safetyScoreCard);
        tvSafetyScore = view.findViewById(R.id.tvSafetyScore);
        tvRiskLevel = view.findViewById(R.id.tvRiskLevel);
        tvLocationName = view.findViewById(R.id.tvLocationName);
        tvCrimeCount = view.findViewById(R.id.tvCrimeCount);

        btnAlertSettings = view.findViewById(R.id.btnAlertSettings);
        btnAlertSettings.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), AlertSettingsActivity.class))
        );

        fabSOS = view.findViewById(R.id.fabSOS);
        fabSOS.setOnClickListener(v ->
                startActivity(new Intent(requireActivity(), SOSActivity.class))
        );

        fusedLocationClient =
                LocationServices.getFusedLocationProviderClient(requireActivity());

        if (PermissionHelper.hasLocationPermission(requireContext())) {
            getCurrentLocation();
        } else {
            PermissionHelper.requestLocationPermission(requireActivity());
        }

        return view;
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
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

        if (PermissionHelper.hasLocationPermission(requireContext())) {
            mMap.setMyLocationEnabled(true);
        }

        mMap.setOnMapClickListener(latLng ->
                getLocationNameAndFetchScore(latLng.latitude, latLng.longitude)
        );
    }

    private void getCurrentLocation() {
        if (!PermissionHelper.hasLocationPermission(requireContext())) return;

        Task<Location> task = fusedLocationClient.getLastLocation();
        task.addOnSuccessListener(location -> {
            if (location != null && mMap != null) {
                LatLng latLng =
                        new LatLng(location.getLatitude(), location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));
                getLocationNameAndFetchScore(latLng.latitude, latLng.longitude);
            }
        });
    }

    private void getLocationNameAndFetchScore(double lat, double lng) {
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            if (!addresses.isEmpty()) {
                String locationName = addresses.get(0).getLocality();
                if (locationName != null && !locationName.isEmpty()) {
                    fetchSafetyScore(locationName, lat, lng);
                }
            }
        } catch (IOException e) {
            Toast.makeText(requireContext(),
                    "Unable to get location name",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchSafetyScore(String locationName, double lat, double lng) {

        safetyScoreCard.setVisibility(View.VISIBLE);
        tvLocationName.setText(locationName);

        Call<SafetyScoreResponse> call =
                apiService.getSafetyScore(locationName);

        call.enqueue(new Callback<SafetyScoreResponse>() {
            @Override
            public void onResponse(
                    Call<SafetyScoreResponse> call,
                    Response<SafetyScoreResponse> response
            ) {
                if (response.isSuccessful()
                        && response.body() != null
                        && response.body().getData() != null) {

                    SafetyScore score = response.body().getData();
                    displaySafetyScore(score);
                    drawCircle(score, lat, lng);
                }
            }

            @Override
            public void onFailure(
                    Call<SafetyScoreResponse> call,
                    Throwable t
            ) {
                Toast.makeText(requireContext(),
                        "ML server not reachable",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void displaySafetyScore(SafetyScore score) {
        tvSafetyScore.setText(
                String.valueOf(score.getSafetyScore())
        );
        tvRiskLevel.setText(
                score.getRiskDisplayText()
        );
        tvCrimeCount.setText(
                score.getTotalCrimes() + " crimes"
        );
    }

    private void drawCircle(SafetyScore score, double lat, double lng) {
        for (Circle c : safetyCircles) c.remove();
        safetyCircles.clear();

        safetyCircles.add(
                mMap.addCircle(
                        MapHelper.createSafetyCircle(
                                lat,
                                lng,
                                score.getColor(),
                                500
                        )
                )
        );
    }
}
