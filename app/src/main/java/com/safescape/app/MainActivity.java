package com.safescape.app;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.safescape.app.network.ApiClient;
import com.safescape.app.network.ApiService;
import com.safescape.app.network.TestResponse;
import com.safescape.app.fragments.HomeFragment;
import com.safescape.app.fragments.MapFragment;
import com.safescape.app.fragments.ProfileFragment;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;
    private FragmentManager fragmentManager;

    // 🔹 Retrofit service
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Hide action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Initialize views
        bottomNav = findViewById(R.id.bottom_navigation);
        fragmentManager = getSupportFragmentManager();

        // Setup bottom navigation
        setupBottomNavigation();

        // Load default fragment
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }

        // 🔹 Initialize Retrofit
        apiService = ApiClient.getClient().create(ApiService.class);

        // 🔹 Test backend connection
        testBackendConnection();
    }

    private void setupBottomNavigation() {
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.nav_map) {
                selectedFragment = new MapFragment();
            } else if (itemId == R.id.nav_profile) {
                selectedFragment = new ProfileFragment();
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
            }
            return true;
        });
    }

    private void loadFragment(Fragment fragment) {
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    // 🔹 BACKEND TEST CALL
    private void testBackendConnection() {
        apiService.testBackend().enqueue(new Callback<TestResponse>() {
            @Override
            public void onResponse(Call<TestResponse> call, Response<TestResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("API_TEST", "Backend says: " + response.body().getStatus());
                } else {
                    Log.e("API_TEST", "Response error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<TestResponse> call, Throwable t) {
                Log.e("API_TEST", "Backend connection failed", t);
            }
        });
    }
}
