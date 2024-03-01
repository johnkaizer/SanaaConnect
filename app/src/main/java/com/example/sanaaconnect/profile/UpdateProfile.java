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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UpdateProfile extends AppCompatActivity {

    private EditText editTextUpdateName, editTextUpdateDoB, editTextUpdateMobile;
    private RadioGroup radioGroupUpdateGender;
    private RadioButton radioButtonUpdateGenderSelected;
    private String textFullName, textDoB, textGender, textMobile;
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

        radioGroupUpdateGender = findViewById(R.id.radio_group_update_profile_gender);

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



        //Setting up DatePicker on editText
        editTextUpdateDoB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //extracting saved dd, m, yyyy into different variables
                String textSADoB [] = textDoB.split("/");

                int day = Integer.parseInt(textSADoB[0]);
                int month = Integer.parseInt(textSADoB[1]) - 1;
                int year = Integer.parseInt(textSADoB[2]);

                DatePickerDialog picker;

                //Date Picker Dialog
                picker = new DatePickerDialog(UpdateProfile.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        editTextUpdateDoB.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                    }
                }, year, month, day);
                picker.show();

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
        int selectedGenderID = radioGroupUpdateGender.getCheckedRadioButtonId();
        radioButtonUpdateGenderSelected = findViewById(selectedGenderID);

        //Validate Mobile Number using matcher and pattern (Regular Expression)
        String mobileRegex = "[0][0-9]{9}"; //First no. should be 0 and rest 9 can be any no.
        Matcher mobileMatcher;
        Pattern mobilePattern = Pattern.compile(mobileRegex);
        mobileMatcher = mobilePattern.matcher(textMobile);


        if(TextUtils.isEmpty(textFullName)){
            Toast.makeText(UpdateProfile.this, "Please enter your full name", Toast.LENGTH_LONG).show();
            editTextUpdateName.setError("Full Name is required!");
            editTextUpdateName.requestFocus();

        }  else if (TextUtils.isEmpty(textDoB)) {
            Toast.makeText(UpdateProfile.this, "Please enter your date of birth", Toast.LENGTH_LONG).show();
            editTextUpdateDoB.setError("Date of birth is required!");
            editTextUpdateDoB.requestFocus();

        } else if (TextUtils.isEmpty(radioButtonUpdateGenderSelected.getText())) {
            Toast.makeText(UpdateProfile.this, "Please enter your gender", Toast.LENGTH_LONG).show();
            radioButtonUpdateGenderSelected.setError("Gender is required!");
            radioButtonUpdateGenderSelected.requestFocus();

        } else if (TextUtils.isEmpty(textMobile)) {
            Toast.makeText(UpdateProfile.this, "Please enter your mobile number", Toast.LENGTH_LONG).show();
            editTextUpdateMobile.setError("Mobile number is required!");
            editTextUpdateMobile.requestFocus();

        } else if (textMobile.length() != 10) {
            Toast.makeText(UpdateProfile.this, "Please re-enter your mobile number", Toast.LENGTH_LONG).show();
            editTextUpdateMobile.setError("Mobile No. should be 10 digits!");
            editTextUpdateMobile.requestFocus();

        } else if (!mobileMatcher.find()) {
            Toast.makeText(UpdateProfile.this, "Please re-enter your mobile number", Toast.LENGTH_LONG).show();
            editTextUpdateMobile.setError("Mobile No. is not valid");
            editTextUpdateMobile.requestFocus();


        } else {
            textGender = radioButtonUpdateGenderSelected.getText().toString();
            textFullName = editTextUpdateName.getText().toString();
            textDoB = editTextUpdateDoB.getText().toString();
            textMobile = editTextUpdateMobile.getText().toString();

            //enter user data into Firebase Realtime Database. Set up dependencies
            ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails(textDoB, textGender, textMobile);

            //Extract user reference
            DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");

            String userID = firebaseUser.getUid();

            progressBar.setVisibility(View.VISIBLE);

            referenceProfile.child(userID).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        //setting a display name
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(textFullName).build();
                        firebaseUser.updateProfile(profileUpdates);

                        Toast.makeText(UpdateProfile.this, "Update Successful", Toast.LENGTH_SHORT).show();

                        // stop user from returning to UpdateProfile activity on pressing back button and close activity
                        Intent intent = new Intent(UpdateProfile.this, CreatorProfile.class );
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

                        startActivity(intent);
                        finish();
                    } else {
                        try{
                            throw task.getException();
                        } catch (Exception e){
                            Toast.makeText(UpdateProfile.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }

                    progressBar.setVisibility(View.GONE);
                }
            });
        }

    }

    //fetch data from database and display
    private void showProfile(FirebaseUser firebaseUser) {
        String userIDofRegistered = firebaseUser.getUid();

        //Extracting user reference from Database for "Registered users"
        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Users");

        progressBar.setVisibility(View.VISIBLE);

        referenceProfile.child(userIDofRegistered).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users readUserDetails = snapshot.getValue(Users.class);
                if (readUserDetails != null) {
                    textFullName = firebaseUser.getDisplayName();
                    textDoB = readUserDetails.getRole();
                    textGender = readUserDetails.getRole();
                    textMobile = readUserDetails.getEmail();

                    editTextUpdateName.setText(textFullName);
                    editTextUpdateDoB.setText(textDoB);
                    editTextUpdateMobile.setText(textMobile);

                    //Show Gender through Radio Button
                    if (textGender.equals("Male")){
                        radioButtonUpdateGenderSelected = findViewById(R.id.radio_male);
                    } else {
                        radioButtonUpdateGenderSelected = findViewById(R.id.radio_female);

                    }
                    radioButtonUpdateGenderSelected.setChecked(true);

                } else {
                    Toast.makeText(UpdateProfile.this, "Something went wrong", Toast.LENGTH_SHORT).show();
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