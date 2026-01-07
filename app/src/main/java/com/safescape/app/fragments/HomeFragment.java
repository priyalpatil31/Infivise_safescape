package com.safescape.app.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.safescape.app.R;
import com.safescape.app.utils.SharedPrefsManager;

public class HomeFragment extends Fragment {

    private TextView tvWelcome;
    private SharedPrefsManager prefsManager;

    // Safe Zones Card
    private CardView cardSafeZones;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        prefsManager = SharedPrefsManager.getInstance(requireContext());

        tvWelcome = view.findViewById(R.id.tvWelcome);

        // Display welcome message
        String userName = prefsManager.getUserName();
        String persona = prefsManager.getPersona();
        tvWelcome.setText("Welcome, " + userName + "!\nPersona: " + persona);

        // Safe Zones Card
        cardSafeZones = view.findViewById(R.id.cardSafeZones);
        cardSafeZones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireActivity(), SafeZonesActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }
}
