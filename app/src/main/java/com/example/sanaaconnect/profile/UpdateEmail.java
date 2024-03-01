package com.example.sanaaconnect.profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sanaaconnect.activities.PortfolioActivity;
import com.example.sanaaconnect.auth.ChangePassword;
import com.example.sanaaconnect.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UpdateEmail extends AppCompatActivity {

    private FirebaseAuth authProfile;
    private FirebaseUser firebaseUser;
    private ProgressBar progressBar;
    private TextView textViewAuthenticated;
    private String userOldEmail, userNewEmail, userPwd;
    private Button buttonUpdateEmail;
    private EditText editTextNewEmail, editTextPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_email);

        getSupportActionBar().setTitle("Update Email");

        progressBar = findViewById(R.id.progressBar);
        editTextPwd = findViewById(R.id.editText_update_email_verify_password);
        editTextNewEmail = findViewById(R.id.editText_update_email_new);
        textViewAuthenticated = findViewById(R.id.textView_update_email_authenticated);
        buttonUpdateEmail = findViewById(R.id.button_update_email);

        buttonUpdateEmail.setEnabled(false); //Make button disabled in the beginning until the user is authenticated
        editTextNewEmail.setEnabled(false);

        authProfile = FirebaseAuth.getInstance();
        firebaseUser = authProfile.getCurrentUser();

        //set old email id on textView
        if (firebaseUser == null) {
            Toast.makeText(this, "Something went wrong! User details not available", Toast.LENGTH_SHORT).show();
        } else {
            userOldEmail = firebaseUser.getEmail();
            TextView textViewOldEmail = findViewById(R.id.textView_update_email_old);
            textViewOldEmail.setText(userOldEmail);
            textViewAuthenticate(firebaseUser);
        }

        //show hide password
        ImageView imageViewShowHidePwd = findViewById(R.id.imageView_show_hide_pwd);
        imageViewShowHidePwd.setImageResource(R.drawable.ic_show_pwd);
        imageViewShowHidePwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editTextPwd.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())) {
                    //If password is visible then hide it
                    editTextPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    //change icon
                    imageViewShowHidePwd.setImageResource(R.drawable.ic_show_pwd);
                } else {
                    editTextPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    imageViewShowHidePwd.setImageResource(R.drawable.ic_hide_pwd);
                }
            }
        });
    }

    //ReAuthenticate user before updating email
    private void textViewAuthenticate(FirebaseUser firebaseUser) {
        Button buttonVerifyUser = findViewById(R.id.button_authenticate_user);
        buttonVerifyUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userPwd = editTextPwd.getText().toString();

                if (TextUtils.isEmpty(userPwd)) {
                    Toast.makeText(UpdateEmail.this, "Password is needed to continue", Toast.LENGTH_SHORT).show();
                    editTextPwd.setError("Please enter your password for authentication");
                    editTextPwd.requestFocus();
                } else {
                    progressBar.setVisibility(View.VISIBLE);

                    AuthCredential credential = EmailAuthProvider.getCredential(userOldEmail, userPwd);

                    firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                progressBar.setVisibility(View.GONE);

                                Toast.makeText(UpdateEmail.this, "Password Verified" + "You can update email now", Toast.LENGTH_LONG).show();

                                //Set TextView to show user is authenticated
                                textViewAuthenticated.setText("You are authenticated. You can update your email now!");

                                //Disable edittext for password and enable edittext for new email and update button
                                editTextNewEmail.setEnabled(true);
                                editTextPwd.setEnabled(false);
                                buttonVerifyUser.setEnabled(false);
                                buttonUpdateEmail.setEnabled(true);

                                //change color of update email button
                                buttonUpdateEmail.setBackgroundTintList(ContextCompat.getColorStateList(UpdateEmail.this, R.color.colorAccent2));

                                buttonUpdateEmail.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        userNewEmail = editTextNewEmail.getText().toString();
                                        if (TextUtils.isEmpty(userNewEmail)) {
                                            Toast.makeText(UpdateEmail.this, "New email is required", Toast.LENGTH_SHORT).show();
                                            editTextNewEmail.setError("Please enter new email");
                                            editTextNewEmail.requestFocus();

                                        } else if (!Patterns.EMAIL_ADDRESS.matcher(userNewEmail).matches()) {
                                            Toast.makeText(UpdateEmail.this, "Please enter a valid email", Toast.LENGTH_SHORT).show();
                                            editTextNewEmail.setError("Please enter valid email");
                                            editTextNewEmail.requestFocus();

                                        } else if (userOldEmail.equals(userNewEmail)) {
                                            Toast.makeText(UpdateEmail.this, "New email cannot be the same as the old email", Toast.LENGTH_SHORT).show();
                                            editTextNewEmail.setError("Cannot use the same email. Please enter new email!");
                                            editTextNewEmail.requestFocus();

                                        } else {
                                            progressBar.setVisibility(View.VISIBLE);
                                            verifyBeforeUpdateEmail(firebaseUser);
                                        }
                                    }
                                });

                            } else {
                                try {
                                    throw task.getException();
                                } catch (Exception e) {
                                    Toast.makeText(UpdateEmail.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }

                            }
                        }
                    });
                }
            }
        });
    }

    private void verifyBeforeUpdateEmail(FirebaseUser firebaseUser) {
        String newEmail = editTextNewEmail.getText().toString();
        String password = editTextPwd.getText().toString();

        if (TextUtils.isEmpty(newEmail) || TextUtils.isEmpty(password)) {
            Toast.makeText(UpdateEmail.this, "New email and password are required", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        AuthCredential credential = EmailAuthProvider.getCredential(firebaseUser.getEmail(), password);

        firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> reauthTask) {
                if (reauthTask.isSuccessful()) {
                    firebaseUser.verifyBeforeUpdateEmail(newEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> updateEmailTask) {
                            progressBar.setVisibility(View.GONE);

                            if (updateEmailTask.isSuccessful()) {
                                // Email update verification sent
                                Toast.makeText(UpdateEmail.this, "Verification email sent. Please check your email", Toast.LENGTH_SHORT).show();
                                /*Intent intent = new Intent(UpdateEmail.this, Login.class);
                                startActivity(intent);
                                finish(); */

                            } else {
                                // Failed to send email update verification
                                Toast.makeText(UpdateEmail.this, "Failed to send verification email: " + updateEmailTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    // ReAuthentication failed
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(UpdateEmail.this, "Reauthentication failed: " + reauthTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //Creating ActionBar menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate menu items
        getMenuInflater().inflate(R.menu.common_menu, menu);
        return true;
    }

    //when any menu item is selected
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(UpdateEmail.this);

        } else if (id == R.id.menu_refresh) {
            //refresh activity
            startActivity(getIntent());
            overridePendingTransition(0, 0);

        }else if (id == R.id.menu_portfolio) {
            Intent intent = new Intent(UpdateEmail.this, PortfolioActivity.class);
            startActivity(intent);

        } else if (id == R.id.menu_update_profile) {
            Intent intent = new Intent(UpdateEmail.this, UpdateProfile.class);
            startActivity(intent);

        } else if (id == R.id.menu_update_email) {
            // Already in the update email activity, no need to start it again
            // Add any specific behavior here if needed
            Toast.makeText(this, "You are already in the Update Email screen", Toast.LENGTH_SHORT).show();

        } else if (id == R.id.menu_change_password) {
            Intent intent = new Intent(UpdateEmail.this, ChangePassword.class);
            startActivity(intent);
        } else if (id == R.id.menu_delete_profile) {
            Intent intent = new Intent(UpdateEmail.this, DeleteProfile.class);
            startActivity(intent);

        } else {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
        }

        return super.onOptionsItemSelected(item);
    }
}

