package com.crownhorse.app.services;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.crownhorse.app.R;
import com.crownhorse.app.models.Service;
import com.crownhorse.app.repository.ServiceRepository;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class AddEditServiceActivity extends AppCompatActivity {

    private TextInputEditText etName, etDescription, etPrice, etCategory, etLocation;
    private Button btnSave;
    private View progressBar;
    private Service existingService;
    private final ServiceRepository repository = new ServiceRepository();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_service);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        etName = findViewById(R.id.etName);
        etDescription = findViewById(R.id.etDescription);
        etPrice = findViewById(R.id.etPrice);
        etCategory = findViewById(R.id.etCategory);
        etLocation = findViewById(R.id.etLocation);
        btnSave = findViewById(R.id.btnSave);
        progressBar = findViewById(R.id.progressBar);

        String serviceId = getIntent().getStringExtra("serviceId");
        if (serviceId != null) loadService(serviceId);

        btnSave.setOnClickListener(v -> saveService());
    }

    private void loadService(String serviceId) {
        progressBar.setVisibility(View.VISIBLE);
        com.google.firebase.firestore.FirebaseFirestore.getInstance()
                .collection("services").document(serviceId).get()
                .addOnSuccessListener(doc -> {
                    progressBar.setVisibility(View.GONE);
                    existingService = doc.toObject(Service.class);
                    if (existingService != null) {
                        etName.setText(existingService.getName());
                        etDescription.setText(existingService.getDescription());
                        etPrice.setText(String.valueOf(existingService.getPrice()));
                        etCategory.setText(existingService.getCategory());
                        etLocation.setText(existingService.getLocation());
                    }
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void saveService() {
        String name = etName.getText() != null ? etName.getText().toString().trim() : "";
        String desc = etDescription.getText() != null ? etDescription.getText().toString().trim() : "";
        String priceStr = etPrice.getText() != null ? etPrice.getText().toString().trim() : "";
        String category = etCategory.getText() != null ? etCategory.getText().toString().trim() : "";
        String location = etLocation.getText() != null ? etLocation.getText().toString().trim() : "";

        if (name.isEmpty() || priceStr.isEmpty()) {
            Toast.makeText(this, R.string.error_fill_all_fields, Toast.LENGTH_SHORT).show();
            return;
        }

        double price;
        try { price = Double.parseDouble(priceStr); } catch (NumberFormatException e) { price = 0; }

        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) return;

        Service service = existingService != null ? existingService : new Service();
        service.setName(name);
        service.setDescription(desc);
        service.setPrice(price);
        service.setCategory(category);
        service.setLocation(location);
        service.setProviderId(uid);
        if (service.getCreatedAt() == 0) service.setCreatedAt(System.currentTimeMillis());

        setLoading(true);
        if (existingService != null) {
            repository.updateService(service, new ServiceRepository.Callback<>() {
                @Override public void onSuccess(Void r) { setLoading(false); finish(); }
                @Override public void onFailure(Exception e) { setLoading(false); Toast.makeText(AddEditServiceActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show(); }
            });
        } else {
            repository.addService(service, new ServiceRepository.Callback<>() {
                @Override public void onSuccess(String id) { setLoading(false); finish(); }
                @Override public void onFailure(Exception e) { setLoading(false); Toast.makeText(AddEditServiceActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show(); }
            });
        }
    }

    private void setLoading(boolean loading) {
        btnSave.setEnabled(!loading);
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
    }

    @Override
    public boolean onSupportNavigateUp() { finish(); return true; }
}
