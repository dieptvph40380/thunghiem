package fpl.md37.genz_fashion.ManagerScreen;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.genz_fashion.R;
import com.example.genz_fashion.databinding.ActivitySigninBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import fpl.md37.genz_fashion.UserScreen.ForgotRole;
import fpl.md37.genz_fashion.UserScreen.MainActivity;

public class SignInActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private ActivitySigninBinding binding;

    private static final String ADMIN_EMAIL = "admin1@gmail.com";

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            navigateToMainActivity();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySigninBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance();

        binding.toRegister.setOnClickListener(view -> navigateToSignUp());

        binding.forgotpass.setOnClickListener(view -> navigateToForgotPassword());

        binding.btnsign.setOnClickListener(view -> handleSignIn());
    }

    private void navigateToSignUp() {
        Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
        startActivity(intent);
        finish();
    }

    private void navigateToForgotPassword() {
        Intent intent = new Intent(getApplicationContext(), ForgotRole.class);
        startActivity(intent);
        finish();
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void handleSignIn() {
        String email = String.valueOf(binding.edtemail.getText()).trim();
        String password = String.valueOf(binding.edtpassword.getText()).trim();

        if (TextUtils.isEmpty(email)) {
            binding.edtemail.setError("Email is required");
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.edtemail.setError("Invalid email address");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            binding.edtpassword.setError("Password is required");
            return;
        }
        if (password.length() < 6) {
            binding.edtpassword.setError("Password must be at least 6 characters");
            return;
        }

        binding.progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    binding.progressBar.setVisibility(View.GONE);

                    if (task.isSuccessful()) {
                        handleSuccessfulSignIn(email);
                    } else {
                        handleSignInFailure(task.getException());
                    }
                });
    }

    private void handleSuccessfulSignIn(String email) {
        if (email.equals(ADMIN_EMAIL)) {
            Toast.makeText(getApplicationContext(), "Admin Login Successful", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), MainActivityManager.class);
            startActivity(intent);
        } else {
            Toast.makeText(getApplicationContext(), "User Login Successful", Toast.LENGTH_SHORT).show();
            navigateToMainActivity();
        }
        finish();
    }

    private void handleSignInFailure(Exception exception) {
        if (exception != null) {
            String errorMessage = exception.getMessage();
            if (errorMessage != null && errorMessage.contains("network error")) {
                Toast.makeText(SignInActivity.this, "Network error. Please check your connection.", Toast.LENGTH_SHORT).show();
            } else if (errorMessage != null && errorMessage.contains("password is invalid")) {
                Toast.makeText(SignInActivity.this, "Invalid password. Please try again.", Toast.LENGTH_SHORT).show();
            } else if (errorMessage != null && errorMessage.contains("no user record")) {
                Toast.makeText(SignInActivity.this, "No account found with this email.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(SignInActivity.this, "Login failed: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(SignInActivity.this, "Login failed. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }
}
