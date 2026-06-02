package com.crownhorse.app.horses;

import android.Manifest;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.content.pm.PackageManager;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.crownhorse.app.BuildConfig;
import com.crownhorse.app.R;
import com.crownhorse.app.models.Horse;
import com.crownhorse.app.repository.HorseRepository;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.io.IOException;

public class AddEditHorseActivity extends AppCompatActivity {

    private TextInputEditText etName, etAge, etType, etDescription;
    private ImageView ivPhoto;
    private Button btnSave;
    private View progressBar;
    private Uri selectedImageUri;
    private Uri pendingCameraImageUri;
    private Horse existingHorse;
    private final HorseRepository repository = new HorseRepository();

    private final ActivityResultLauncher<String> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    Glide.with(this).load(uri).into(ivPhoto);
                }
            });
    private final ActivityResultLauncher<Uri> cameraLauncher =
            registerForActivityResult(new ActivityResultContracts.TakePicture(), success -> {
                if (success && pendingCameraImageUri != null) {
                    selectedImageUri = pendingCameraImageUri;
                    Glide.with(this).load(pendingCameraImageUri).into(ivPhoto);
                }
                pendingCameraImageUri = null;
            });

    private final ActivityResultLauncher<String> cameraPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
                if (granted) {
                    openCamera();
                } else {
                    Toast.makeText(this, R.string.camera_permission_denied, Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_horse);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        etName = findViewById(R.id.etName);
        etAge = findViewById(R.id.etAge);
        etType = findViewById(R.id.etType);
        etDescription = findViewById(R.id.etDescription);
        ivPhoto = findViewById(R.id.ivPhoto);
        btnSave = findViewById(R.id.btnSave);
        progressBar = findViewById(R.id.progressBar);

        ivPhoto.setOnClickListener(v -> showImageSourceDialog());
        btnSave.setOnClickListener(v -> saveHorse());

        String horseId = getIntent().getStringExtra("horseId");
        if (horseId != null) {
            loadHorse(horseId);
        }
    }

    private void loadHorse(String horseId) {
        progressBar.setVisibility(View.VISIBLE);
        // Load from repository - we'll use getHorsesByOwner workaround: direct Firestore call
        com.google.firebase.firestore.FirebaseFirestore.getInstance()
                .collection("horses").document(horseId).get()
                .addOnSuccessListener(doc -> {
                    progressBar.setVisibility(View.GONE);
                    existingHorse = doc.toObject(Horse.class);
                    if (existingHorse != null) populateFields(existingHorse);
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void populateFields(Horse horse) {
        etName.setText(horse.getName());
        etAge.setText(String.valueOf(horse.getAge()));
        etType.setText(horse.getType());
        etDescription.setText(horse.getDescription());
        if (horse.getPhotoUrl() != null && !horse.getPhotoUrl().isEmpty()) {
            Glide.with(this).load(horse.getPhotoUrl())
                    .placeholder(R.drawable.ic_horse_placeholder).into(ivPhoto);
        }
    }

    private void saveHorse() {
        String name = etName.getText() != null ? etName.getText().toString().trim() : "";
        String ageStr = etAge.getText() != null ? etAge.getText().toString().trim() : "";
        String type = etType.getText() != null ? etType.getText().toString().trim() : "";
        String desc = etDescription.getText() != null ? etDescription.getText().toString().trim() : "";

        if (name.isEmpty() || ageStr.isEmpty()) {
            Toast.makeText(this, R.string.error_fill_all_fields, Toast.LENGTH_SHORT).show();
            return;
        }

        int age;
        try { age = Integer.parseInt(ageStr); } catch (NumberFormatException e) { age = 0; }

        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) return;

        Horse horse = existingHorse != null ? existingHorse : new Horse();
        horse.setName(name);
        horse.setAge(age);
        horse.setType(type);
        horse.setDescription(desc);
        horse.setOwnerId(uid);
        if (horse.getCreatedAt() == 0) horse.setCreatedAt(System.currentTimeMillis());

        setLoading(true);

        if (selectedImageUri != null) {
            String horseId = existingHorse != null ? existingHorse.getHorseId()
                    : "temp_" + System.currentTimeMillis();
            repository.uploadHorsePhoto(selectedImageUri, horseId, new HorseRepository.Callback<>() {
                @Override
                public void onSuccess(String url) {
                    horse.setPhotoUrl(url);
                    persistHorse(horse);
                }

                @Override
                public void onFailure(Exception e) {
                    setLoading(false);
                    Toast.makeText(AddEditHorseActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            persistHorse(horse);
        }
    }

    private void showImageSourceDialog() {
        CharSequence[] items = {
                getString(R.string.photo_source_camera),
                getString(R.string.photo_source_gallery)
        };

        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.choose_image_source)
                .setItems(items, (dialog, which) -> {
                    if (which == 0) {
                        checkCameraPermissionAndOpen();
                    } else {
                        imagePickerLauncher.launch("image/*");
                    }
                })
                .show();
    }

    private void checkCameraPermissionAndOpen() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void openCamera() {
        try {
            File imageFile = createTempImageFile();
            pendingCameraImageUri = FileProvider.getUriForFile(
                    this,
                    BuildConfig.APPLICATION_ID + ".fileprovider",
                    imageFile
            );
            cameraLauncher.launch(pendingCameraImageUri);
        } catch (IOException | IllegalArgumentException e) {
            pendingCameraImageUri = null;
            Toast.makeText(this, R.string.error_open_camera, Toast.LENGTH_SHORT).show();
        }
    }

    private File createTempImageFile() throws IOException {
        File dir = new File(getCacheDir(), "images");
        if (!dir.exists() && !dir.mkdirs()) {
            throw new IOException("Failed to create image directory");
        }
        return File.createTempFile("horse_", ".jpg", dir);
    }

    private void persistHorse(Horse horse) {
        if (existingHorse != null) {
            repository.updateHorse(horse, new HorseRepository.Callback<>() {
                @Override public void onSuccess(Void r) { setLoading(false); finish(); }
                @Override public void onFailure(Exception e) { setLoading(false); Toast.makeText(AddEditHorseActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show(); }
            });
        } else {
            repository.addHorse(horse, new HorseRepository.Callback<>() {
                @Override public void onSuccess(String id) { setLoading(false); finish(); }
                @Override public void onFailure(Exception e) { setLoading(false); Toast.makeText(AddEditHorseActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show(); }
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
