package com.safescape.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.safescape.app.MainActivity;
import com.safescape.app.R;
import com.safescape.app.api.ApiService;
import com.safescape.app.api.ApiClient;
import com.safescape.app.models.user;
import com.safescape.app.utils.SharedPrefsManager;
import java.util.HashMap;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PersonaSelectionActivity extends AppCompatActivity {

    private CardView cardSoloFemale, cardFamily, cardBackpacker, cardBusiness;
    private SharedPrefsManager prefsManager;
    private ApiService apiService;
    private String selectedPersona = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_persona_selection);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        prefsManager = SharedPrefsManager.getInstance(this);
        apiService = ApiClient.getClient().create(ApiService.class);

        initViews();
        setupClickListeners();
    }

    private void initViews() {
        cardSoloFemale = findViewById(R.id.cardSoloFemale);
        cardFamily = findViewById(R.id.cardFamily);
        cardBackpacker = findViewById(R.id.cardBackpacker);
        cardBusiness = findViewById(R.id.cardBusiness);
    }

    private void setupClickListeners() {
        cardSoloFemale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPersona("Solo Female Traveler");
            }
        });

        cardFamily.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPersona("Family Traveler");
            }
        });

        cardBackpacker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPersona("Backpacker");
            }
        });

        cardBusiness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPersona("Business Traveler");
            }
        });
    }

    private void selectPersona(String persona) {
        selectedPersona = persona;

        // Save locally first
        prefsManager.savePersona(persona);

        // Update on server
        updatePersonaOnServer(persona);
    }

    private void updatePersonaOnServer(String persona) {
        String token = prefsManager.getAuthHeader();
        if (token == null) {
            // No token, just proceed to main screen
            goToMainScreen();
            return;
        }

        Map<String, String> personaData = new HashMap<>();
        personaData.put("persona", persona);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<user> call = apiService.updatePersona(token, personaData);

        call.enqueue(new Callback<user>() {
            @Override
            public void onResponse(Call<user> call, Response<user> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(PersonaSelectionActivity.this,
                            "Persona saved: " + selectedPersona,
                            Toast.LENGTH_SHORT).show();
                }
                goToMainScreen();
            }

            @Override
            public void onFailure(Call<user> call, Throwable t) {
                // Even if API fails, proceed with locally saved persona
                Toast.makeText(PersonaSelectionActivity.this,
                        "Persona saved locally",
                        Toast.LENGTH_SHORT).show();
                goToMainScreen();
            }
        });
    }

    private void goToMainScreen() {
        Intent intent = new Intent(PersonaSelectionActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        // Disable back button - user must select a persona
        Toast.makeText(this, "Please select a persona to continue",
                Toast.LENGTH_SHORT).show();
    }
}