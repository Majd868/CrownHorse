package com.crownhorse.app.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.crownhorse.app.R;
import com.crownhorse.app.models.User;
import com.crownhorse.app.repository.UserRepository;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class SignInActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword;
    private Button btnSignIn;
    private View progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnSignIn = findViewById(R.id.btnSignIn);
        progressBar = findViewById(R.id.progressBar);

        btnSignIn.setOnClickListener(v -> attemptSignIn());

        TextView tvSignUp = findViewById(R.id.tvSignUp);
        tvSignUp.setOnClickListener(v ->
                startActivity(new Intent(this, SignUpActivity.class)));
    }

    private void attemptSignIn() {
        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString() : "";

        if (email.isEmpty() || password.isEmpty()) {
            showError(getString(R.string.error_fill_all_fields));
            return;
        }

        setLoading(true);
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    setLoading(false);
                    // Check if profile is complete
                    String uid = authResult.getUser().getUid();
                    new UserRepository().getUser(uid, new UserRepository.Callback<>() {
                        @Override
                        public void onSuccess(User user) {
                            if (user != null && user.getRole() != null && user.getCountry() != null) {
                                startActivity(new Intent(SignInActivity.this,
                                        com.crownhorse.app.home.HomeActivity.class));
                            } else {
                                startActivity(new Intent(SignInActivity.this, RoleSelectActivity.class));
                            }
                            finish();
                        }

                        @Override
                        public void onFailure(Exception e) {
                            startActivity(new Intent(SignInActivity.this,
                                    com.crownhorse.app.home.HomeActivity.class));
                            finish();
                        }
                    });
                })
                .addOnFailureListener(e -> {
                    setLoading(false);
                    showError(e.getMessage());
                });
    }

    private void showError(String msg) {
        Snackbar.make(btnSignIn, msg != null ? msg : getString(R.string.error_unknown),
                Snackbar.LENGTH_LONG).show();
    }

    private void setLoading(boolean loading) {
        btnSignIn.setEnabled(!loading);
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
    }
}
