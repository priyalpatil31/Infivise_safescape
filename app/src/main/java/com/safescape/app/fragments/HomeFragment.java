package com.safescape.app.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.safescape.app.R;
import com.safescape.app.api.ApiClient;
import com.safescape.app.api.ApiService;
import com.safescape.app.api.TestResponse;
import com.safescape.app.models.SafetyScore;
import com.safescape.app.models.SafetyScoreResponse;
import com.safescape.app.utils.SharedPrefsManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private static final String TAG = "HOME_FRAGMENT";

    private TextView tvWelcome;
    private TextView tvSafetyScore;
    private EditText etLocationSearch;
    private CardView cardSafeZones;

    private SharedPrefsManager prefsManager;
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
        tvSafetyScore = view.findViewById(R.id.tvSafetyScore);
        etLocationSearch = view.findViewById(R.id.etLocationSearch);
        cardSafeZones = view.findViewById(R.id.cardSafeZones);

        String userName = prefsManager.getUserName();
        String persona = prefsManager.getPersona();
        tvWelcome.setText("Welcome, " + userName + "!\nPersona: " + persona);

        return view;
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        apiService = ApiClient.getClient().create(ApiService.class);

        testBackendConnection();

        etLocationSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String location = etLocationSearch.getText().toString().trim();
                if (!location.isEmpty()) {
                    fetchSafetyScore(location);
                }
                return true;
            }
            return false;
        });
    }

    private void testBackendConnection() {
        apiService.testBackend().enqueue(new Callback<TestResponse>() {
            @Override
            public void onResponse(
                    Call<TestResponse> call,
                    Response<TestResponse> response) {

                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Backend OK");
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

    private void fetchSafetyScore(String location) {
        tvSafetyScore.setText("Safety Score: Loading...");

        apiService.getSafetyScore(location)
                .enqueue(new Callback<SafetyScoreResponse>() {
                    @Override
                    public void onResponse(
                            Call<SafetyScoreResponse> call,
                            Response<SafetyScoreResponse> response) {

                        if (response.isSuccessful()
                                && response.body() != null
                                && response.body().isSuccess()) {

                            SafetyScore score = response.body().getData();

                            tvSafetyScore.setText(
                                    "Safety Score: " +
                                            score.getSafetyScore() +
                                            "/100 (" +
                                            score.getRiskRating() + ")"
                            );
                        } else {
                            tvSafetyScore.setText("Location not found");
                        }
                    }

                    @Override
                    public void onFailure(Call<SafetyScoreResponse> call, Throwable t) {
                        tvSafetyScore.setText("Server not reachable");
                    }
                });
    }
}

