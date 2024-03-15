package com.example.sanaaconnect.profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sanaaconnect.activities.PortfolioActivity;
import com.example.sanaaconnect.auth.ChangePassword;
import com.example.sanaaconnect.R;
import com.example.sanaaconnect.models.Users;
import com.example.sanaaconnect.utils.ReadWriteUserDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UpdateProfile extends AppCompatActivity {

    private EditText editTextUpdateName, editTextUpdateDoB, editTextUpdateMobile;
    private String textFullName, textMobile;
    private FirebaseAuth authProfile;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        getSupportActionBar().setTitle("Update Profile Details");

        progressBar = findViewById(R.id.progressBar);
        editTextUpdateName = findViewById(R.id.editText_update_profile_name);
        editTextUpdateDoB = findViewById(R.id.editText_update_profile_dob);
        editTextUpdateMobile = findViewById(R.id.editText_update_profile_mobile);


        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        //show profile data
        showProfile(firebaseUser);

        //Upload profile pic
        TextView textViewUploadProfilePic = findViewById(R.id.textView_profile_upload_pic);
        textViewUploadProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UpdateProfile.this, UploadProfilePic.class);
                startActivity(intent);
                finish();
            }
        });

      //Update Email
        TextView textViewUploadEmail = findViewById(R.id.textView_profile_update_email);
        textViewUploadEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UpdateProfile.this, UpdateEmail.class);
                startActivity(intent);
                finish();
            }
        });


        //Update Profile
        Button buttonUpdateProfile = findViewById(R.id.button_update_profile);
        buttonUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile(firebaseUser);
            }
        });





    }
    //update profile
    private void updateProfile(FirebaseUser firebaseUser) {
            textFullName = editTextUpdateName.getText().toString();
            textMobile = editTextUpdateMobile.getText().toString();

            // Extract user reference
            DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Users");

            String userID = firebaseUser.getUid();

            progressBar.setVisibility(View.VISIBLE);

            // Update only fullName and phoneNumber fields
            Map<String, Object> updateValues = new HashMap<>();
            updateValues.put("fullName", textFullName);
            updateValues.put("phoneNumber", textMobile);

            referenceProfile.child(userID).updateChildren(updateValues).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        // Update display name
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(textFullName).build();
                        firebaseUser.updateProfile(profileUpdates);

                        Toast.makeText(UpdateProfile.this, "Update Successful", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(UpdateProfile.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
                    }

                    progressBar.setVisibility(View.GONE);
                }
            });

    }
    //fetch data from database and display
    private void showProfile(FirebaseUser firebaseUser) {
        String userIDofRegistered = firebaseUser.getUid();

        // Extracting user reference from Database for "Registered users"
        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Users");

        progressBar.setVisibility(View.VISIBLE);

        referenceProfile.child(userIDofRegistered).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users readUserDetails = snapshot.getValue(Users.class);

                // Populate EditText fields with initial user profile data
                if (readUserDetails != null) {
                    editTextUpdateName.setText(readUserDetails.fullName);
                    editTextUpdateDoB.setText(readUserDetails.role); // Assuming email is the role
                    editTextUpdateMobile.setText(readUserDetails.phoneNumber);
                } else {
                    Toast.makeText(UpdateProfile.this, "User details not found", Toast.LENGTH_SHORT).show();
                }

                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UpdateProfile.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
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
            NavUtils.navigateUpFromSameTask(UpdateProfile.this);

        } else if (id == R.id.menu_refresh){
            //refresh activity
            startActivity(getIntent());
            finish();
            overridePendingTransition(0,0);

        } else if (id == R.id.menu_update_profile) {
            Intent intent = new Intent(UpdateProfile.this, UpdateProfile.class);
            startActivity(intent);

        } else if (id == R.id.menu_update_email) {
            Intent intent = new Intent(UpdateProfile.this, UpdateEmail.class);
            startActivity(intent);

        }else if (id == R.id.menu_portfolio) {
            Intent intent = new Intent(UpdateProfile.this, PortfolioActivity.class);
            startActivity(intent);

        } else if (id == R.id.menu_change_password) {
            Intent intent = new Intent(UpdateProfile.this, ChangePassword.class);
            startActivity(intent);

        }  else if (id == R.id.menu_delete_profile) {
            Intent intent = new Intent(UpdateProfile.this, DeleteProfile.class);
            startActivity(intent);

        } else {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();

        }

        return super.onOptionsItemSelected(item);
    }
}