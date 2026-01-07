package com.safescape.app.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.safescape.app.R;
import com.safescape.app.activities.LoginActivity;
import com.safescape.app.utils.SharedPrefsManager;

public class ProfileFragment extends Fragment {

    private TextView tvUserName, tvUserEmail, tvPersona;
    private Button btnLogout;
    private SharedPrefsManager prefsManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        prefsManager = SharedPrefsManager.getInstance(requireContext());

        tvUserName = view.findViewById(R.id.tvUserName);
        tvUserEmail = view.findViewById(R.id.tvUserEmail);
        tvPersona = view.findViewById(R.id.tvPersona);
        btnLogout = view.findViewById(R.id.btnLogout);

        // Display user info
        tvUserName.setText(prefsManager.getUserName());
        tvUserEmail.setText(prefsManager.getToken() != null ? "Logged In" : "Not Logged In");
        tvPersona.setText("Persona: " + prefsManager.getPersona());

        // Logout button
        btnLogout.setOnClickListener(v -> {
            prefsManager.logout();
            Intent intent = new Intent(requireActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        return view;
    }
}