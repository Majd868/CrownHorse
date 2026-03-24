package com.crownhorse.app.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.crownhorse.app.R;
import com.crownhorse.app.models.User;
import com.crownhorse.app.repository.UserRepository;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity {

    private TextInputEditText etName, etEmail, etPassword, etConfirmPassword;
    private Button btnSignUp;
    private View progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnSignUp = findViewById(R.id.btnSignUp);
        progressBar = findViewById(R.id.progressBar);

        btnSignUp.setOnClickListener(v -> attemptSignUp());
        findViewById(R.id.tvSignIn).setOnClickListener(v -> finish());
    }

    private void attemptSignUp() {
        String name = etName.getText() != null ? etName.getText().toString().trim() : "";
        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString() : "";
        String confirm = etConfirmPassword.getText() != null ? etConfirmPassword.getText().toString() : "";

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showError(getString(R.string.error_fill_all_fields));
            return;
        }
        if (!password.equals(confirm)) {
            showError(getString(R.string.error_passwords_mismatch));
            return;
        }
        if (password.length() < 6) {
            showError(getString(R.string.error_password_too_short));
            return;
        }

        setLoading(true);
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    String uid = authResult.getUser().getUid();
                    User user = new User();
                    user.setUid(uid);
                    user.setName(name);
                    user.setEmail(email);
                    user.setCreatedAt(System.currentTimeMillis());
                    new UserRepository().saveUser(user, new UserRepository.Callback<>() {
                        @Override
                        public void onSuccess(Void result) {
                            setLoading(false);
                            startActivity(new Intent(SignUpActivity.this, RoleSelectActivity.class));
                            finish();
                        }

                        @Override
                        public void onFailure(Exception e) {
                            setLoading(false);
                            showError(e.getMessage());
                        }
                    });
                })
                .addOnFailureListener(e -> {
                    setLoading(false);
                    showError(e.getMessage());
                });
    }

    private void showError(String msg) {
        Snackbar.make(btnSignUp, msg != null ? msg : getString(R.string.error_unknown),
                Snackbar.LENGTH_LONG).show();
    }

    private void setLoading(boolean loading) {
        btnSignUp.setEnabled(!loading);
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
    }
}
