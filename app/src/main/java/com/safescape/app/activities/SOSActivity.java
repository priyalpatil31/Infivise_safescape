package com.safescape.app.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.safescape.app.R;
import com.safescape.app.models.EmergencyContact;
import com.safescape.app.utils.EmergencyContactsManager;
import com.safescape.app.utils.PermissionHelper;
import com.safescape.app.utils.SOSHelper;

public class SOSActivity extends AppCompatActivity {

    private Button btnSendSOS, btnCallPrimary, btnManageContacts;
    private TextView tvContactsCount, tvInstructions;

    private EmergencyContactsManager contactsManager;
    private FusedLocationProviderClient fusedLocationClient;
    private Location currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sos);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Initialize
        contactsManager = new EmergencyContactsManager(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Initialize views
        btnSendSOS = findViewById(R.id.btnSendSOS);
        btnCallPrimary = findViewById(R.id.btnCallPrimary);
        btnManageContacts = findViewById(R.id.btnManageContacts);
        tvContactsCount = findViewById(R.id.tvContactsCount);
        tvInstructions = findViewById(R.id.tvInstructions);

        // Update UI
        updateContactsCount();

        // Get current location
        getCurrentLocation();

        // Set up button listeners
        btnSendSOS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSOSAlert();
            }
        });

        btnCallPrimary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callPrimaryContact();
            }
        });

        // UPDATED: Manage Contacts button
        btnManageContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SOSActivity.this, ManageContactsActivity.class);
                startActivity(intent);
            }
        });
    }

    private void getCurrentLocation() {
        if (!PermissionHelper.hasLocationPermission(this)) {
            PermissionHelper.requestLocationPermission(this);
            return;
        }

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            Task<Location> task = fusedLocationClient.getLastLocation();
            task.addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    currentLocation = location;
                }
            });
        }
    }

    private void sendSOSAlert() {
        if (!contactsManager.hasContacts()) {
            Toast.makeText(this,
                    "Please add emergency contacts first!",
                    Toast.LENGTH_LONG).show();
            return;
        }

        if (!PermissionHelper.hasSmsPermission(this)) {
            PermissionHelper.requestSmsPermission(this);
            return;
        }

        SOSHelper.sendSOSMessages(this, currentLocation, contactsManager);
    }

    private void callPrimaryContact() {
        EmergencyContact primary = contactsManager.getPrimaryContact();

        if (primary == null) {
            Toast.makeText(this,
                    "No emergency contacts added!",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        SOSHelper.dialNumber(this, primary.getPhone());
    }

    private void updateContactsCount() {
        int count = contactsManager.getContactsCount();
        tvContactsCount.setText(count + " emergency contact(s) added");

        if (count == 0) {
            tvInstructions.setText("⚠️ Add emergency contacts to use SOS features");
            btnSendSOS.setEnabled(false);
            btnCallPrimary.setEnabled(false);
        } else {
            tvInstructions.setText("✓ Emergency contacts configured");
            btnSendSOS.setEnabled(true);
            btnCallPrimary.setEnabled(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateContactsCount();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PermissionHelper.LOCATION_PERMISSION_CODE) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            }
        } else if (requestCode == PermissionHelper.SMS_PERMISSION_CODE) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this,
                        "SMS permission granted. You can now send SOS.",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}
