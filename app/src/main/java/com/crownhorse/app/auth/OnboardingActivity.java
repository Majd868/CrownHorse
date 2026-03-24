package com.crownhorse.app.auth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.crownhorse.app.R;
import com.crownhorse.app.home.HomeActivity;
import com.crownhorse.app.repository.UserRepository;
import com.crownhorse.app.utils.LocaleHelper;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

public class OnboardingActivity extends AppCompatActivity {

    private Spinner spCountry, spCity, spCurrency, spLanguage;
    private Button btnSave;
    private View progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        spCountry = findViewById(R.id.spCountry);
        spCity = findViewById(R.id.spCity);
        spCurrency = findViewById(R.id.spCurrency);
        spLanguage = findViewById(R.id.spLanguage);
        btnSave = findViewById(R.id.btnSave);
        progressBar = findViewById(R.id.progressBar);

        setupSpinners();
        btnSave.setOnClickListener(v -> saveOnboarding());
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

    private void saveOnboarding() {
        String country = spCountry.getSelectedItem().toString();
        String city = spCity.getSelectedItem().toString();
        String currency = spCurrency.getSelectedItem().toString();
        String languageLabel = spLanguage.getSelectedItem().toString();

        String languageCode = "en";
        if (languageLabel.equalsIgnoreCase("Arabic") || languageLabel.equalsIgnoreCase("عربي")) {
            languageCode = "ar";
        } else if (languageLabel.equalsIgnoreCase("Hebrew") || languageLabel.equalsIgnoreCase("עברית")) {
            languageCode = "iw";
        }

        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) return;

        setLoading(true);
        final String finalLanguageCode = languageCode;
        new UserRepository().getUser(uid, new UserRepository.Callback<>() {
            @Override
            public void onSuccess(com.crownhorse.app.models.User user) {
                if (user == null) user = new com.crownhorse.app.models.User();
                user.setUid(uid);
                user.setCountry(country);
                user.setCity(city);
                user.setCurrency(currency);
                user.setLanguage(finalLanguageCode);

                new UserRepository().saveUser(user, new UserRepository.Callback<>() {
                    @Override
                    public void onSuccess(Void r) {
                        SharedPreferences prefs = getSharedPreferences("crownhorse_prefs", MODE_PRIVATE);
                        prefs.edit().putString("language", finalLanguageCode).apply();
                        LocaleHelper.setLocale(OnboardingActivity.this, finalLanguageCode);
                        setLoading(false);
                        startActivity(new Intent(OnboardingActivity.this, HomeActivity.class));
                        finish();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        setLoading(false);
                        showError(e.getMessage());
                    }
                });
            }

            @Override
            public void onFailure(Exception e) {
                setLoading(false);
                showError(e.getMessage());
            }
        });
    }

    private void showError(String msg) {
        Snackbar.make(btnSave, msg != null ? msg : getString(R.string.error_unknown),
                Snackbar.LENGTH_LONG).show();
    }

    private void setLoading(boolean loading) {
        btnSave.setEnabled(!loading);
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
    }
}
