package com.safescape.app.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.safescape.app.R;
import com.safescape.app.models.EmergencyContact;
import com.safescape.app.utils.EmergencyContactsManager;

public class AddContactActivity extends AppCompatActivity {

    private EditText etName, etPhone;
    private Spinner spinnerRelationship;
    private Button btnSave;

    private EmergencyContactsManager contactsManager;
    private boolean isEditMode = false;
    private String contactId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        // Initialize
        contactsManager = new EmergencyContactsManager(this);

        // Check if edit mode
        if (getIntent().hasExtra("is_edit")) {
            isEditMode = true;
            contactId = getIntent().getStringExtra("contact_id");
        }

        // Setup action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(isEditMode ? "Edit Contact" : "Add Contact");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialize views
        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        spinnerRelationship = findViewById(R.id.spinnerRelationship);
        btnSave = findViewById(R.id.btnSave);

        // Setup relationship spinner
        setupRelationshipSpinner();

        // If edit mode, load contact data
        if (isEditMode) {
            loadContactData();
            btnSave.setText("Update Contact");
        }

        // Save button listener
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveContact();
            }
        });
    }

    private void setupRelationshipSpinner() {
        String[] relationships = {"Family", "Friend", "Colleague", "Neighbor", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, relationships);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRelationship.setAdapter(adapter);
    }

    private void loadContactData() {
        etName.setText(getIntent().getStringExtra("contact_name"));
        etPhone.setText(getIntent().getStringExtra("contact_phone"));

        String relationship = getIntent().getStringExtra("contact_relationship");
        String[] relationships = {"Family", "Friend", "Colleague", "Neighbor", "Other"};
        for (int i = 0; i < relationships.length; i++) {
            if (relationships[i].equals(relationship)) {
                spinnerRelationship.setSelection(i);
                break;
            }
        }
    }

    private void saveContact() {
        // Get values
        String name = etName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String relationship = spinnerRelationship.getSelectedItem().toString();

        // Validate
        if (TextUtils.isEmpty(name)) {
            etName.setError("Name is required");
            etName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(phone)) {
            etPhone.setError("Phone number is required");
            etPhone.requestFocus();
            return;
        }

        if (phone.length() < 10) {
            etPhone.setError("Please enter a valid phone number");
            etPhone.requestFocus();
            return;
        }

        // Create/update contact
        EmergencyContact contact = new EmergencyContact(name, phone, relationship);

        if (isEditMode) {
            contact.setId(contactId);
            if (contactsManager.updateContact(contact)) {
                Toast.makeText(this, "Contact updated!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to update contact", Toast.LENGTH_SHORT).show();
            }
        } else {
            if (contactsManager.addContact(contact)) {
                Toast.makeText(this, "Contact added!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Contact already exists", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
