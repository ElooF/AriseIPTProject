package com.example.arise.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.arise.data.remote.FirebaseManager;
import com.example.arise.databinding.ActivityAuthBinding;

public class AuthActivity extends AppCompatActivity {

    private ActivityAuthBinding binding;
    private boolean isRegisterMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAuthBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toggleAuthMode.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == binding.btnModeRegister.getId()) {
                    isRegisterMode = true;
                    binding.tilName.setVisibility(View.VISIBLE);
                    binding.btnSubmitAuth.setText("Register");
                } else {
                    isRegisterMode = false;
                    binding.tilName.setVisibility(View.GONE);
                    binding.btnSubmitAuth.setText("Log In");
                }
            }
        });

        binding.btnSubmitAuth.setOnClickListener(v -> performAuth());

        binding.btnGuestMode.setOnClickListener(v -> navigateToMain());
    }

    private void performAuth() {
        String email = binding.etEmail.getText() != null ? binding.etEmail.getText().toString().trim() : "";
        String password = binding.etPassword.getText() != null ? binding.etPassword.getText().toString().trim() : "";
        String name = binding.etName.getText() != null ? binding.etName.getText().toString().trim() : "Marianne";

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseManager fm = FirebaseManager.getInstance(this);
        FirebaseManager.AuthCallback callback = new FirebaseManager.AuthCallback() {
            @Override
            public void onSuccess(String uid, String userEmail, String displayName) {
                Toast.makeText(AuthActivity.this, "Welcome, " + displayName + "!", Toast.LENGTH_SHORT).show();
                navigateToMain();
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(AuthActivity.this, "Error: " + errorMessage, Toast.LENGTH_LONG).show();
                // Guest fallback if offline/no firebase
                navigateToMain();
            }
        };

        if (isRegisterMode) {
            fm.register(email, password, name, callback);
        } else {
            fm.login(email, password, callback);
        }
    }

    private void navigateToMain() {
        startActivity(new Intent(AuthActivity.this, MainActivity.class));
        finish();
    }
}
