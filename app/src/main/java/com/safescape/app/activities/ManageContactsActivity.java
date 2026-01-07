package com.safescape.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.safescape.app.R;
import com.safescape.app.adapters.EmergencyContactAdapter;
import com.safescape.app.models.EmergencyContact;
import com.safescape.app.utils.EmergencyContactsManager;
import java.util.List;

public class ManageContactsActivity extends AppCompatActivity
        implements EmergencyContactAdapter.OnContactActionListener {

    private RecyclerView recyclerView;
    private EmergencyContactAdapter adapter;
    private EmergencyContactsManager contactsManager;
    private FloatingActionButton fabAddContact;
    private TextView tvEmptyState;
    private List<EmergencyContact> contacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_contacts);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Emergency Contacts");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialize
        contactsManager = new EmergencyContactsManager(this);

        // Initialize views
        recyclerView = findViewById(R.id.recyclerViewContacts);
        fabAddContact = findViewById(R.id.fabAddContact);
        tvEmptyState = findViewById(R.id.tvEmptyState);

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Load contacts
        loadContacts();

        // Add contact button
        fabAddContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManageContactsActivity.this,
                        AddContactActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadContacts();
    }

    private void loadContacts() {
        contacts = contactsManager.getContacts();

        if (contacts.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            tvEmptyState.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            tvEmptyState.setVisibility(View.GONE);

            adapter = new EmergencyContactAdapter(contacts, this);
            recyclerView.setAdapter(adapter);
        }
    }

    @Override
    public void onCallContact(EmergencyContact contact) {
        // Dial the contact
        com.safescape.app.utils.SOSHelper.dialNumber(this, contact.getPhone());
    }

    @Override
    public void onEditContact(EmergencyContact contact) {
        Intent intent = new Intent(this, AddContactActivity.class);
        intent.putExtra("contact_id", contact.getId());
        intent.putExtra("contact_name", contact.getName());
        intent.putExtra("contact_phone", contact.getPhone());
        intent.putExtra("contact_relationship", contact.getRelationship());
        intent.putExtra("is_edit", true);
        startActivity(intent);
    }

    @Override
    public void onDeleteContact(EmergencyContact contact) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Contact")
                .setMessage("Are you sure you want to delete " + contact.getName() + "?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    if (contactsManager.deleteContact(contact.getId())) {
                        Toast.makeText(this, "Contact deleted", Toast.LENGTH_SHORT).show();
                        loadContacts();
                    } else {
                        Toast.makeText(this, "Failed to delete contact", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onSetPrimary(EmergencyContact contact) {
        contactsManager.setPrimaryContact(contact.getId());
        Toast.makeText(this, contact.getName() + " set as primary contact",
                Toast.LENGTH_SHORT).show();
        loadContacts();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}