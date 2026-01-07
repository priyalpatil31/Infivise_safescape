package com.safescape.app.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.safescape.app.models.EmergencyContact;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EmergencyContactsManager {
    private static final String PREF_NAME = "EmergencyContacts";
    private static final String KEY_CONTACTS = "contacts_list";

    private SharedPreferences prefs;
    private Gson gson;

    public EmergencyContactsManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    // Get all emergency contacts
    public List<EmergencyContact> getContacts() {
        String json = prefs.getString(KEY_CONTACTS, null);
        if (json != null) {
            Type type = new TypeToken<List<EmergencyContact>>(){}.getType();
            return gson.fromJson(json, type);
        }
        return new ArrayList<>();
    }

    // Add new contact
    public boolean addContact(EmergencyContact contact) {
        List<EmergencyContact> contacts = getContacts();

        // Generate ID
        contact.setId(UUID.randomUUID().toString());

        // Check if phone already exists
        for (EmergencyContact c : contacts) {
            if (c.getPhone().equals(contact.getPhone())) {
                return false; // Duplicate
            }
        }

        contacts.add(contact);
        saveContacts(contacts);
        return true;
    }

    // Update existing contact
    public boolean updateContact(EmergencyContact contact) {
        List<EmergencyContact> contacts = getContacts();

        for (int i = 0; i < contacts.size(); i++) {
            if (contacts.get(i).getId().equals(contact.getId())) {
                contacts.set(i, contact);
                saveContacts(contacts);
                return true;
            }
        }
        return false;
    }

    // Delete contact
    public boolean deleteContact(String contactId) {
        List<EmergencyContact> contacts = getContacts();

        for (int i = 0; i < contacts.size(); i++) {
            if (contacts.get(i).getId().equals(contactId)) {
                contacts.remove(i);
                saveContacts(contacts);
                return true;
            }
        }
        return false;
    }

    // Get primary contact
    public EmergencyContact getPrimaryContact() {
        List<EmergencyContact> contacts = getContacts();
        for (EmergencyContact contact : contacts) {
            if (contact.isPrimary()) {
                return contact;
            }
        }
        // Return first contact if no primary set
        return contacts.isEmpty() ? null : contacts.get(0);
    }

    // Set primary contact
    public void setPrimaryContact(String contactId) {
        List<EmergencyContact> contacts = getContacts();

        // Remove primary from all
        for (EmergencyContact contact : contacts) {
            contact.setPrimary(false);
        }

        // Set new primary
        for (EmergencyContact contact : contacts) {
            if (contact.getId().equals(contactId)) {
                contact.setPrimary(true);
                break;
            }
        }

        saveContacts(contacts);
    }

    // Save contacts to SharedPreferences
    private void saveContacts(List<EmergencyContact> contacts) {
        String json = gson.toJson(contacts);
        prefs.edit().putString(KEY_CONTACTS, json).apply();
    }

    // Check if any contacts exist
    public boolean hasContacts() {
        return !getContacts().isEmpty();
    }

    // Get contacts count
    public int getContactsCount() {
        return getContacts().size();
    }
}