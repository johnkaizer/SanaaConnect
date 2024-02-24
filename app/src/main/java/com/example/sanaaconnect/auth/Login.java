package com.example.sanaaconnect.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sanaaconnect.R;
import com.example.sanaaconnect.activities.HomeDashActivity;
import com.example.sanaaconnect.profile.CreatorProfile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {

    private EditText editTextLoginEmail, editTextLoginPwd;
    private ProgressBar progressBar;
    private FirebaseAuth authProfile;
    private static final String TAG = "Login";
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextLoginEmail = findViewById(R.id.editText_login_email);
        editTextLoginPwd = findViewById(R.id.editText_login_pwd);
        progressBar = findViewById(R.id.progressBar);

        authProfile = FirebaseAuth.getInstance();
        preferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        editor = preferences.edit();

        //reset password
        TextView textViewLinkResetPwd = findViewById(R.id.textView_forgot_password);
        textViewLinkResetPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Login.this, "You can reset your password now!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Login.this, ForgotPassword.class));
            }
        });

        //Register
        TextView textViewLinkRegister = findViewById(R.id.textView_register_link);
        textViewLinkRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Login.this, "You can register now!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Login.this, Register.class));
            }
        });

        //show hide password
        ImageView imageViewShowHidePwd = findViewById(R.id.imageView_show_hide_pwd);
        imageViewShowHidePwd.setImageResource(R.drawable.ic_show_pwd);
        imageViewShowHidePwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editTextLoginPwd.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                    //If password is visible then hide it
                    editTextLoginPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    //change icon
                    imageViewShowHidePwd.setImageResource(R.drawable.ic_show_pwd);
                } else {
                    editTextLoginPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    imageViewShowHidePwd.setImageResource(R.drawable.ic_hide_pwd);
                }
            }
        });

        //Login User
        Button buttonLogin = findViewById(R.id.button_login);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextLoginEmail.getText().toString();
                String password = editTextLoginPwd.getText().toString();

                if(TextUtils.isEmpty(email)){
                    editTextLoginEmail.setError("Email is required!");
                    editTextLoginEmail.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    editTextLoginEmail.setError("Valid email is required!");
                    editTextLoginEmail.requestFocus();
                } else if (TextUtils.isEmpty(password)){
                    editTextLoginPwd.setError("Password is required!");
                    editTextLoginPwd.requestFocus();
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    loginUser(email, password);
                }
            }
        });
    }

    private void loginUser(String email, String password) {
        authProfile.signInWithEmailAndPassword(email, password).addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()){
                    FirebaseUser firebaseUser = authProfile.getCurrentUser();
                    if (firebaseUser != null && firebaseUser.isEmailVerified()) {
                        String userId = firebaseUser.getUid();
                        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);
                        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    String role = snapshot.child("role").getValue(String.class);
                                    editor.putString("role", role);
                                    editor.putString("clientId", userId);
                                    editor.apply();
                                    startActivity(new Intent(Login.this, HomeDashActivity.class));
                                    finish();
                                } else {
                                    // Handle the case where user data does not exist
                                    Toast.makeText(Login.this, "User data not found", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                // Handle errors
                                Log.e(TAG, "Database error: " + error.getMessage());
                                Toast.makeText(Login.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        // Email is not verified, prompt the user to verify the email
                        Toast.makeText(Login.this, "Please verify your email address before logging in", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Handle login failure
                    Toast.makeText(Login.this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = authProfile.getCurrentUser();
        if (currentUser != null){
            Toast.makeText(this, "Already Logged In!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Login.this, HomeDashActivity.class));
            finish();
        } else {
            Toast.makeText(this, "You can log in now!", Toast.LENGTH_SHORT).show();
        }
    }
}
