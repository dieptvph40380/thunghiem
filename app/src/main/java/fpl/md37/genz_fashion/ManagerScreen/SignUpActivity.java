package fpl.md37.genz_fashion.ManagerScreen;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.genz_fashion.databinding.ActivitySignUpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import fpl.md37.genz_fashion.UserScreen.MainActivity;

public class SignUpActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    ActivitySignUpBinding binding;
    FirebaseFirestore fstore;
    String userID;

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        mAuth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        setContentView(binding.getRoot());

        binding.tvDangNhap.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
            startActivity(intent);
            finish();
        });

        binding.buttonDangKy.setOnClickListener(view -> {
            String email = binding.edtEmail.getText().toString().trim();
            String password = binding.edtPassword.getText().toString().trim();
            String repass = binding.edtNhaplaiPassword.getText().toString().trim();
            String name = binding.edtUsername.getText().toString().trim();
            String phone = binding.edtPhone.getText().toString().trim();
            String address = binding.edtAddress.getText().toString().trim();

            // Validate fields
            if (!validateFields(name, email, phone, address, password, repass)) {
                return; // Stop if validation fails
            }

            // Create user in Firebase Auth
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(SignUpActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();

                                userID = mAuth.getCurrentUser().getUid();
                                DocumentReference documentReference = fstore.collection("Client").document(userID);
                                Map<String, Object> user = new HashMap<>();
                                user.put("userId", userID);
                                user.put("name", name);
                                user.put("email", email);
                                user.put("phone", phone);
                                user.put("address", address);

                                documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(SignUpActivity.this, "User profile created.", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                // Redirect to MainActivity
                                startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                                finish();

                            } else {
                                Toast.makeText(SignUpActivity.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        });
    }

    private boolean validateFields(String name, String email, String phone, String address, String password, String repass) {
        // Validate name
        if (TextUtils.isEmpty(name)) {
            binding.edtUsername.setError("Username is required.");
            binding.edtUsername.requestFocus();
            return false;
        }

        // Validate email
        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.edtEmail.setError("Enter a valid email address.");
            binding.edtEmail.requestFocus();
            return false;
        }

        // Validate phone
        if (TextUtils.isEmpty(phone) || phone.length() < 10) {
            binding.edtPhone.setError("Enter a valid phone number (at least 10 digits).");
            binding.edtPhone.requestFocus();
            return false;
        }

        // Validate address
        if (TextUtils.isEmpty(address)) {
            binding.edtAddress.setError("Address is required.");
            binding.edtAddress.requestFocus();
            return false;
        }

        // Validate password
        if (!isValidPassword(password)) {
            binding.passwordTil.setError("Password must be at least 8 characters, include a number, a letter");
            binding.edtPassword.requestFocus();
            return false;
        } else {
            binding.passwordTil.setError(null);
        }

        // Validate re-entered password
        if (!password.equals(repass)) {
            binding.passwordTilConfirm.setError("Passwords do not match.");
            binding.edtNhaplaiPassword.requestFocus();
            return false;
        } else {
            binding.passwordTilConfirm.setError(null);
        }

        return true;
    }

    private boolean isValidPassword(String password) {
        String passwordPattern = "^(?=.*[a-zA-Z])(?=.*\\d).{8,}$";
        return password != null && password.matches(passwordPattern);
    }

}
