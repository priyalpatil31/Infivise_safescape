package com.safescape.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.safescape.app.R;
import com.safescape.app.models.EmergencyContact;
import java.util.List;

public class EmergencyContactAdapter extends RecyclerView.Adapter<EmergencyContactAdapter.ContactViewHolder> {

    private List<EmergencyContact> contacts;
    private OnContactActionListener listener;

    public interface OnContactActionListener {
        void onCallContact(EmergencyContact contact);
        void onEditContact(EmergencyContact contact);
        void onDeleteContact(EmergencyContact contact);
        void onSetPrimary(EmergencyContact contact);
    }

    public EmergencyContactAdapter(List<EmergencyContact> contacts, OnContactActionListener listener) {
        this.contacts = contacts;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_emergency_contact, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        EmergencyContact contact = contacts.get(position);
        holder.bind(contact);
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    class ContactViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName, tvPhone, tvRelationship, tvPrimaryBadge;
        private ImageButton btnCall, btnEdit, btnDelete, btnSetPrimary;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvContactName);
            tvPhone = itemView.findViewById(R.id.tvContactPhone);
            tvRelationship = itemView.findViewById(R.id.tvContactRelationship);
            tvPrimaryBadge = itemView.findViewById(R.id.tvPrimaryBadge);
            btnCall = itemView.findViewById(R.id.btnCall);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnSetPrimary = itemView.findViewById(R.id.btnSetPrimary);
        }

        public void bind(EmergencyContact contact) {
            tvName.setText(contact.getName());
            tvPhone.setText(contact.getPhone());
            tvRelationship.setText(contact.getRelationship());

            // Show/hide primary badge
            if (contact.isPrimary()) {
                tvPrimaryBadge.setVisibility(View.VISIBLE);
                btnSetPrimary.setVisibility(View.GONE);
            } else {
                tvPrimaryBadge.setVisibility(View.GONE);
                btnSetPrimary.setVisibility(View.VISIBLE);
            }

            // Button listeners
            btnCall.setOnClickListener(v -> listener.onCallContact(contact));
            btnEdit.setOnClickListener(v -> listener.onEditContact(contact));
            btnDelete.setOnClickListener(v -> listener.onDeleteContact(contact));
            btnSetPrimary.setOnClickListener(v -> listener.onSetPrimary(contact));
        }
    }
}