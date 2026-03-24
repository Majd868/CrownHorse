package com.crownhorse.app.services;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.crownhorse.app.R;
import com.crownhorse.app.bookings.CreateBookingActivity;
import com.crownhorse.app.models.Service;
import com.google.android.material.snackbar.Snackbar;

public class ServiceDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String serviceId = getIntent().getStringExtra("serviceId");
        if (serviceId == null) { finish(); return; }

        loadService(serviceId);
    }

    private void loadService(String serviceId) {
        com.google.firebase.firestore.FirebaseFirestore.getInstance()
                .collection("services").document(serviceId).get()
                .addOnSuccessListener(doc -> {
                    Service service = doc.toObject(Service.class);
                    if (service == null) { finish(); return; }

                    ((TextView) findViewById(R.id.tvName)).setText(service.getName());
                    ((TextView) findViewById(R.id.tvDescription)).setText(service.getDescription());
                    ((TextView) findViewById(R.id.tvPrice)).setText(
                            getString(R.string.price_format, service.getPrice()));
                    ((TextView) findViewById(R.id.tvCategory)).setText(service.getCategory());
                    ((TextView) findViewById(R.id.tvLocation)).setText(
                            service.getLocation() != null ? service.getLocation() : "");

                    Button btnBook = findViewById(R.id.btnBook);
                    SharedPreferences prefs = getSharedPreferences("crownhorse_prefs", MODE_PRIVATE);
                    String role = prefs.getString("userRole", "owner");
                    btnBook.setVisibility("owner".equals(role) ? View.VISIBLE : View.GONE);
                    btnBook.setOnClickListener(v -> {
                        Intent intent = new Intent(this, CreateBookingActivity.class);
                        intent.putExtra("serviceId", service.getServiceId());
                        intent.putExtra("providerId", service.getProviderId());
                        startActivity(intent);
                    });
                })
                .addOnFailureListener(e ->
                        Snackbar.make(findViewById(android.R.id.content),
                                e.getMessage() != null ? e.getMessage() : "Error",
                                Snackbar.LENGTH_SHORT).show());
    }

    @Override
    public boolean onSupportNavigateUp() { finish(); return true; }
}
