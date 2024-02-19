package com.example.sanaaconnect.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.sanaaconnect.R;
import com.example.sanaaconnect.activities.HomeDashActivity;
import com.example.sanaaconnect.databinding.ActivityRegisterBinding;
import com.example.sanaaconnect.models.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private Spinner spinner;
    private FirebaseAuth mAuth;
    private EditText editTextName, editTextEmail, editTextPhone, editTextPassword;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance();
        editTextName = findViewById(R.id.editText_register_full_name);
        editTextEmail = findViewById(R.id.editText_register_email);
        editTextPhone = findViewById(R.id.editText_register_mobile);
        editTextPassword = findViewById(R.id.editText_register_password);
        progressBar = findViewById(R.id.progressBar);

        //Spinners
        spinner = findViewById(R.id.role_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.roles, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        binding.buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = editTextEmail.getText().toString().trim();
                String fullName = editTextName.getText().toString().trim();
                String phone = editTextPhone.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();
                String role = spinner.getSelectedItem().toString().trim();

                if (fullName.isEmpty()) {
                    editTextName.setError("Full name is required!!");
                    editTextName.requestFocus();
                    return;
                }
                if (email.isEmpty()) {
                    editTextEmail.setError("Email is required!!");
                    editTextEmail.requestFocus();
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    editTextEmail.setError("Please provide a valid email address!");
                    editTextEmail.requestFocus();
                    return;
                }
                if (phone.isEmpty()) {
                    editTextPhone.setError("Phone number is required!");
                    editTextPhone.requestFocus();
                    return;
                }
                if (phone.length() != 10) {
                    editTextPhone.setError("Phone number must be 10 digits!");
                    editTextPhone.requestFocus();
                    return;
                }
                if (password.isEmpty()) {
                    editTextPassword.setError("Password is required!");
                    editTextPassword.requestFocus();
                    return;
                }
                if (role.isEmpty()) {
                    editTextPassword.setError("Role is required!");
                    editTextPassword.requestFocus();
                    return;
                }
                if (password.length() < 6) {
                    editTextPassword.setError("Min password length is 6 characters");
                    editTextPassword.requestFocus();
                    return;
                }
                if (!isInternetConnected()) {
                    Toast.makeText(Register.this, "No internet connection available", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);

                                if (task.isSuccessful()) {
                                    // Sign in success
                                    Toast.makeText(Register.this, "User registration successful", Toast.LENGTH_SHORT).show();

                                    // Save additional user details to Firebase Realtime Database
                                    FirebaseUser currentUser = mAuth.getCurrentUser();
                                    if (currentUser != null) {
                                        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Users");

                                        // Generate a unique user ID
                                        String userId = usersRef.push().getKey();

                                        // Create a User object with the provided details
                                        Users newUser = new Users(userId,fullName, email, phone, password, role);

                                        // Save user details to the database
                                        usersRef.child(currentUser.getUid()).setValue(newUser);

                                        // Navigate to the sign-in activity
                                        Intent intent = new Intent(Register.this, Login.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(Register.this, "User registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
        
    }

    public boolean isInternetConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}