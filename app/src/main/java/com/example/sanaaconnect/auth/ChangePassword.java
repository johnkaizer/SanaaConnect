package com.example.sanaaconnect.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sanaaconnect.R;
import com.example.sanaaconnect.profile.CreatorProfile;
import com.example.sanaaconnect.profile.DeleteProfile;
import com.example.sanaaconnect.profile.UpdateEmail;
import com.example.sanaaconnect.profile.UpdateProfile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePassword extends AppCompatActivity {

    private FirebaseAuth authProfile;
    private EditText editTextPwdCurr, editTextPwdNew, editTextPwdConfirmNew;
    private TextView textViewAuthenticated;
    private Button buttonChangePwd, buttonReAuthenticate;
    private ProgressBar progressBar;
    private String userPwdCurr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        getSupportActionBar().setTitle("Change Password");

        editTextPwdNew = findViewById(R.id.editText_change_pwd_new);
        editTextPwdCurr = findViewById(R.id.editText_change_pwd_current);
        editTextPwdConfirmNew = findViewById(R.id.editText_change_pwd_new_confirm);
        textViewAuthenticated = findViewById(R.id.textView_change_pwd_authenticated);
        progressBar = findViewById(R.id.progressBar);
        buttonReAuthenticate = findViewById(R.id.button_change_pwd_authenticate);
        buttonChangePwd = findViewById(R.id.button_change_pwd);


        //Disable editText for new password till authenticated
        editTextPwdNew.setEnabled(false);
        editTextPwdConfirmNew.setEnabled(false);
        buttonChangePwd.setEnabled(false);

        authProfile =  FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        if (firebaseUser.equals("")) {
            Toast.makeText(this, "Something went wrong! User's details not available", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ChangePassword.this, CreatorProfile.class);
            startActivity(intent);
            finish();
        } else{
            reAuthenticateUser(firebaseUser);
        }

    }

    //reAuthenticate user before changing password
    private void reAuthenticateUser(FirebaseUser firebaseUser) {
        buttonReAuthenticate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userPwdCurr = editTextPwdCurr.getText().toString();

                if (TextUtils.isEmpty(userPwdCurr)){
                    Toast.makeText(ChangePassword.this, "Password is needed", Toast.LENGTH_SHORT).show();
                    editTextPwdCurr.setError("Please enter your current password to authenticate");
                    editTextPwdCurr.requestFocus();
                } else {
                    progressBar.setVisibility(View.VISIBLE);

                    //ReAuthenticate user now
                    AuthCredential credential = EmailAuthProvider.getCredential(firebaseUser.getEmail(), userPwdCurr);

                    firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                progressBar.setVisibility(View.GONE);

                                //Disable editText for current password. Enable edittext for new password and confirm new password
                                editTextPwdCurr.setEnabled(false);
                                editTextPwdNew.setEnabled(true);
                                editTextPwdConfirmNew.setEnabled(true);

                                //enable change pwd button. dissable authenticate button
                                buttonReAuthenticate.setEnabled(false);
                                buttonChangePwd.setEnabled(true);

                                //set textview to show user is authenticated
                                textViewAuthenticated.setText("You are authenticated / verified." + "You can change password now!");
                                Toast.makeText(ChangePassword.this, "Password has been verified. Change password now", Toast.LENGTH_SHORT).show();

                                //Update color of change password button
                               buttonChangePwd.setBackgroundTintList(ContextCompat.getColorStateList(ChangePassword.this, R.color.colorAccent2));

                                buttonChangePwd.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        changePwd(firebaseUser);
                                    }
                                });
                            } else {
                                try {
                                    throw task.getException();
                                } catch(Exception e){
                                    Toast.makeText(ChangePassword.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }
            }
        });

    }

    private void changePwd(FirebaseUser firebaseUser) {
        String userPwdNew = editTextPwdNew.getText().toString();
        String userPwdConfirmNew = editTextPwdConfirmNew.getText().toString();

        if (TextUtils.isEmpty(userPwdNew)){
            Toast.makeText(this, "Please enter a new password", Toast.LENGTH_SHORT).show();
            editTextPwdNew.setError("Please enter your new password");
            editTextPwdNew.requestFocus();
        } else if (TextUtils.isEmpty(userPwdConfirmNew)){
            Toast.makeText(this, "Please confirm the new password", Toast.LENGTH_SHORT).show();
            editTextPwdConfirmNew.setError("Please re-enter new password");
            editTextPwdConfirmNew.requestFocus();

        } else if (!userPwdNew.matches(userPwdConfirmNew)) {
            Toast.makeText(this, "Password did not match the new password", Toast.LENGTH_SHORT).show();
            editTextPwdConfirmNew.setError("Please re-enter same password");
            editTextPwdConfirmNew.requestFocus();

        } else if (userPwdCurr.matches(userPwdNew)) {
            Toast.makeText(this, "New password cannot be same as the old password", Toast.LENGTH_SHORT).show();
            editTextPwdNew.setError("Please enter a new password");
            editTextPwdNew.requestFocus();
        } else {
            progressBar.setVisibility(View.VISIBLE);

            firebaseUser.updatePassword(userPwdNew).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(ChangePassword.this, "Password has been changed", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ChangePassword.this, CreatorProfile.class);
                        startActivity(intent);
                        finish();
                    } else {
                        try {
                            throw task.getException();
                        } catch (Exception e){
                            Toast.makeText(ChangePassword.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    progressBar.setVisibility(View.GONE);
                }
            });
        }


    }

    //Creating Actionbar menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate menu items
        getMenuInflater().inflate(R.menu.common_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //when any menu item is selected
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home){
            NavUtils.navigateUpFromSameTask(ChangePassword.this);

        } else if (id == R.id.menu_refresh){
            //refresh activity
            startActivity(getIntent());
            finish();
            overridePendingTransition(0,0);

        } else if (id == R.id.menu_update_profile) {
            Intent intent = new Intent(ChangePassword.this, UpdateProfile.class);
            startActivity(intent);
            finish();

        } else if (id == R.id.menu_update_email) {
            Intent intent = new Intent(ChangePassword.this, UpdateEmail.class);
            startActivity(intent);

        } else if (id == R.id.menu_change_password) {
            Intent intent = new Intent(ChangePassword.this, ChangePassword.class);
            startActivity(intent);
            finish();

        } else if (id == R.id.menu_delete_profile) {
            Intent intent = new Intent(ChangePassword.this, DeleteProfile.class);
            startActivity(intent);
            finish();

        } else {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();


        }

        return super.onOptionsItemSelected(item);
    }
}