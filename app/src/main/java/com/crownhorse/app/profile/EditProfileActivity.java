package com.crownhorse.app.profile;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.crownhorse.app.R;
import com.crownhorse.app.models.User;
import com.crownhorse.app.repository.UserRepository;
import com.crownhorse.app.utils.LocaleHelper;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {

    private TextInputEditText etName;
    private Spinner spCountry, spCity, spCurrency, spLanguage;
    private CircleImageView ivAvatar;
    private Button btnSave;
    private View progressBar;
    private Uri selectedImageUri;
    private User currentUser;
    private final UserRepository userRepository = new UserRepository();

    private final ActivityResultLauncher<String> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    Glide.with(this).load(uri).into(ivAvatar);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        etName = findViewById(R.id.etName);
        spCountry = findViewById(R.id.spCountry);
        spCity = findViewById(R.id.spCity);
        spCurrency = findViewById(R.id.spCurrency);
        spLanguage = findViewById(R.id.spLanguage);
        ivAvatar = findViewById(R.id.ivAvatar);
        btnSave = findViewById(R.id.btnSave);
        progressBar = findViewById(R.id.progressBar);

        setupSpinners();
        ivAvatar.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));
        btnSave.setOnClickListener(v -> saveProfile());

        loadProfile();
    }

    private void setupSpinners() {
        String[] countries = getResources().getStringArray(R.array.countries);
        String[] cities = getResources().getStringArray(R.array.cities);
        String[] currencies = getResources().getStringArray(R.array.currencies);
        String[] languages = getResources().getStringArray(R.array.languages);

        spCountry.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, countries));
        spCity.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, cities));
        spCurrency.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, currencies));
        spLanguage.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, languages));
    }

    private void loadProfile() {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) return;

        progressBar.setVisibility(View.VISIBLE);
        userRepository.getUser(uid, new UserRepository.Callback<>() {
            @Override
            public void onSuccess(User user) {
                progressBar.setVisibility(View.GONE);
                currentUser = user;
                if (user == null) return;

                etName.setText(user.getName() != null ? user.getName() : "");

                if (user.getPhotoUrl() != null && !user.getPhotoUrl().isEmpty()) {
                    Glide.with(EditProfileActivity.this)
                            .load(user.getPhotoUrl())
                            .placeholder(R.drawable.ic_profile_placeholder)
                            .into(ivAvatar);
                }

                selectSpinnerItem(spCountry, user.getCountry());
                selectSpinnerItem(spCity, user.getCity());
                selectSpinnerItem(spCurrency, user.getCurrency());
                selectLanguageSpinner(user.getLanguage());
            }

            @Override
            public void onFailure(Exception e) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(EditProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void selectSpinnerItem(Spinner spinner, String value) {
        if (value == null) return;
        for (int i = 0; i < spinner.getCount(); i++) {
            if (value.equals(spinner.getItemAtPosition(i))) {
                spinner.setSelection(i);
                break;
            }
        }
    }

    private void selectLanguageSpinner(String langCode) {
        if (langCode == null) return;
        String label;
        switch (langCode) {
            case "ar": label = "Arabic"; break;
            case "iw": label = "Hebrew"; break;
            default: label = "English"; break;
        }
        for (int i = 0; i < spLanguage.getCount(); i++) {
            if (label.equalsIgnoreCase(spLanguage.getItemAtPosition(i).toString())) {
                spLanguage.setSelection(i);
                break;
            }
        }
    }

    private void saveProfile() {
        String name = etName.getText() != null ? etName.getText().toString().trim() : "";
        String country = spCountry.getSelectedItem().toString();
        String city = spCity.getSelectedItem().toString();
        String currency = spCurrency.getSelectedItem().toString();
        String languageLabel = spLanguage.getSelectedItem().toString();

        String languageCode = "en";
        if (languageLabel.equalsIgnoreCase("Arabic")) languageCode = "ar";
        else if (languageLabel.equalsIgnoreCase("Hebrew")) languageCode = "iw";

        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) return;

        User user = currentUser != null ? currentUser : new User();
        user.setUid(uid);
        user.setName(name);
        user.setCountry(country);
        user.setCity(city);
        user.setCurrency(currency);
        user.setLanguage(languageCode);

        setLoading(true);
        final String finalLanguageCode = languageCode;

        if (selectedImageUri != null) {
            StorageReference ref = FirebaseStorage.getInstance().getReference()
                    .child("users/" + uid + ".jpg");
            ref.putFile(selectedImageUri)
                    .addOnSuccessListener(t -> ref.getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                user.setPhotoUrl(uri.toString());
                                persistUser(user, finalLanguageCode);
                            })
                            .addOnFailureListener(e -> {
                                setLoading(false);
                                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }))
                    .addOnFailureListener(e -> {
                        setLoading(false);
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            persistUser(user, finalLanguageCode);
        }
    }

    private void persistUser(User user, String languageCode) {
        userRepository.saveUser(user, new UserRepository.Callback<>() {
            @Override
            public void onSuccess(Void result) {
                SharedPreferences prefs = getSharedPreferences("crownhorse_prefs", MODE_PRIVATE);
                prefs.edit().putString("language", languageCode).apply();
                LocaleHelper.setLocale(EditProfileActivity.this, languageCode);
                setLoading(false);
                finish();
            }

            @Override
            public void onFailure(Exception e) {
                setLoading(false);
                Toast.makeText(EditProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setLoading(boolean loading) {
        btnSave.setEnabled(!loading);
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
    }

    @Override
    public boolean onSupportNavigateUp() { finish(); return true; }
}
