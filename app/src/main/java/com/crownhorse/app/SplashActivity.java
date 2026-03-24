package com.crownhorse.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.crownhorse.app.auth.SignInActivity;
import com.crownhorse.app.home.HomeActivity;
import com.crownhorse.app.repository.UserRepository;
import com.crownhorse.app.utils.LocaleHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_DELAY = 1500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences prefs = getSharedPreferences("crownhorse_prefs", MODE_PRIVATE);
        String lang = prefs.getString("language", "en");
        LocaleHelper.setLocale(this, lang);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(this::checkAuthState, SPLASH_DELAY);
    }

    private void checkAuthState() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            goTo(SignInActivity.class);
            return;
        }
        // Check if user profile exists in Firestore
        new UserRepository().getUser(currentUser.getUid(), new UserRepository.Callback<>() {
            @Override
            public void onSuccess(com.crownhorse.app.models.User user) {
                if (user != null && user.getRole() != null && user.getCountry() != null) {
                    goTo(HomeActivity.class);
                } else {
                    goTo(com.crownhorse.app.auth.RoleSelectActivity.class);
                }
            }

            @Override
            public void onFailure(Exception e) {
                goTo(SignInActivity.class);
            }
        });
    }

    private void goTo(Class<?> cls) {
        startActivity(new Intent(this, cls));
        finish();
    }
}
