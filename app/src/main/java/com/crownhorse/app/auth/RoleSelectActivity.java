package com.crownhorse.app.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.crownhorse.app.R;
import com.crownhorse.app.repository.UserRepository;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

public class RoleSelectActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_select);

        findViewById(R.id.cardOwner).setOnClickListener(v -> selectRole("owner"));
        findViewById(R.id.cardProvider).setOnClickListener(v -> selectRole("provider"));
    }

    private void selectRole(String role) {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) return;

        new UserRepository().getUser(uid, new UserRepository.Callback<>() {
            @Override
            public void onSuccess(com.crownhorse.app.models.User user) {
                if (user == null) user = new com.crownhorse.app.models.User();
                user.setUid(uid);
                user.setRole(role);
                new UserRepository().saveUser(user, new UserRepository.Callback<>() {
                    @Override
                    public void onSuccess(Void result) {
                        startActivity(new Intent(RoleSelectActivity.this, OnboardingActivity.class));
                        finish();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Snackbar.make(findViewById(android.R.id.content),
                                e.getMessage() != null ? e.getMessage() : getString(R.string.error_unknown),
                                Snackbar.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onFailure(Exception e) {
                // Still create with role
                com.crownhorse.app.models.User user = new com.crownhorse.app.models.User();
                user.setUid(uid);
                user.setRole(role);
                new UserRepository().saveUser(user, new UserRepository.Callback<>() {
                    @Override
                    public void onSuccess(Void r) {
                        startActivity(new Intent(RoleSelectActivity.this, OnboardingActivity.class));
                        finish();
                    }

                    @Override
                    public void onFailure(Exception ex) {
                        Snackbar.make(findViewById(android.R.id.content),
                                ex.getMessage() != null ? ex.getMessage() : getString(R.string.error_unknown),
                                Snackbar.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
}
