package com.safescape.app.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.safescape.app.R;
import com.safescape.app.services.LocationTrackingService;
import com.safescape.app.utils.SharedPrefsManager;

public class AlertSettingsActivity extends AppCompatActivity {

    private Switch switchAlerts, switchBackgroundTracking;
    private SeekBar seekBarSensitivity;
    private TextView tvSensitivity, tvPersona;
    private Button btnSave;

    private SharedPrefsManager prefsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert_settings);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Alert Settings");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        prefsManager = SharedPrefsManager.getInstance(this);

        // Initialize views
        switchAlerts = findViewById(R.id.switchAlerts);
        switchBackgroundTracking = findViewById(R.id.switchBackgroundTracking);
        seekBarSensitivity = findViewById(R.id.seekBarSensitivity);
        tvSensitivity = findViewById(R.id.tvSensitivity);
        tvPersona = findViewById(R.id.tvPersona);
        btnSave = findViewById(R.id.btnSave);

        // Load current settings
        loadSettings();

        // Setup seekbar listener
        seekBarSensitivity.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateSensitivityText(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // Save button
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSettings();
            }
        });
    }

    private void loadSettings() {
        // Load persona
        String persona = prefsManager.getPersona();
        tvPersona.setText("Current Persona: " + persona);

        // Load alert settings
        boolean alertsEnabled = prefsManager.areAlertsEnabled();
        switchAlerts.setChecked(alertsEnabled);

        boolean backgroundTracking = prefsManager.isBackgroundTrackingEnabled();
        switchBackgroundTracking.setChecked(backgroundTracking);

        int sensitivity = prefsManager.getAlertSensitivity();
        seekBarSensitivity.setProgress(sensitivity);
        updateSensitivityText(sensitivity);
    }

    private void updateSensitivityText(int progress) {
        String[] levels = {"Very Low", "Low", "Medium", "High", "Very High"};
        tvSensitivity.setText("Sensitivity: " + levels[progress]);
    }

    private void saveSettings() {
        boolean alertsEnabled = switchAlerts.isChecked();
        boolean backgroundTracking = switchBackgroundTracking.isChecked();
        int sensitivity = seekBarSensitivity.getProgress();

        // Save to SharedPreferences
        prefsManager.setAlertsEnabled(alertsEnabled);
        prefsManager.setBackgroundTrackingEnabled(backgroundTracking);
        prefsManager.setAlertSensitivity(sensitivity);

        // Start or stop background service
        if (backgroundTracking && alertsEnabled) {
            startTrackingService();
            Toast.makeText(this, "Background tracking enabled", Toast.LENGTH_SHORT).show();
        } else {
            stopTrackingService();
            if (!backgroundTracking) {
                Toast.makeText(this, "Background tracking disabled", Toast.LENGTH_SHORT).show();
            }
        }

        Toast.makeText(this, "Settings saved successfully!", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void startTrackingService() {
        Intent serviceIntent = new Intent(this, LocationTrackingService.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        } else {
            startService(serviceIntent);
        }
    }

    private void stopTrackingService() {
        Intent serviceIntent = new Intent(this, LocationTrackingService.class);
        stopService(serviceIntent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}