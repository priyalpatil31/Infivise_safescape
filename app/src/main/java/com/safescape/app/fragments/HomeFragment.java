package com.safescape.app.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.safescape.app.R;
import com.safescape.app.api.ApiClient;
import com.safescape.app.api.ApiService;
import com.safescape.app.api.TestResponse;
import com.safescape.app.utils.SharedPrefsManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private static final String TAG = "API_TEST";

    private TextView tvWelcome;
    private SharedPrefsManager prefsManager;
    private CardView cardSafeZones;

    private ApiService apiService;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        prefsManager = SharedPrefsManager.getInstance(requireContext());
        tvWelcome = view.findViewById(R.id.tvWelcome);
        cardSafeZones = view.findViewById(R.id.cardSafeZones);

        // Welcome text
        String userName = prefsManager.getUserName();
        String persona = prefsManager.getPersona();
        tvWelcome.setText("Welcome, " + userName + "!\nPersona: " + persona);

        // SafeZones card — disabled for now (NO ERRORS)
        cardSafeZones.setOnClickListener(v -> {
            // intentionally empty
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // ✅ Initialize Retrofit API service
        apiService = ApiClient.getClient().create(ApiService.class);

        // ✅ Test backend connection
        testBackendConnection();
    }

    private void testBackendConnection() {
        apiService.testBackend().enqueue(new Callback<TestResponse>() {
            @Override
            public void onResponse(Call<TestResponse> call, Response<TestResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Backend OK: " + response.body().getStatus());
                } else {
                    Log.e(TAG, "Backend response error");
                }
            }

            @Override
            public void onFailure(Call<TestResponse> call, Throwable t) {
                Log.e(TAG, "Backend NOT reachable", t);
            }
        });
    }
}

