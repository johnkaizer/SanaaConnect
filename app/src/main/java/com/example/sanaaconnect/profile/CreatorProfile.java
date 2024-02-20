package com.example.sanaaconnect.profile;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.sanaaconnect.auth.ChangePassword;
import com.example.sanaaconnect.R;
import com.example.sanaaconnect.utils.ReadWriteUserDetails;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class CreatorProfile extends AppCompatActivity {

    private TextView textViewWelcome, textViewFullName, textViewEmail, textViewDoB, textViewGender, textViewMobile;
    private ProgressBar progressBar;
    private String fullName, email, doB, gender, mobile;
    private ImageView imageview;
    private FirebaseAuth authProfile;
    private SwipeRefreshLayout swipeContainer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creator_profile);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Your Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        swipeToRefresh();

        textViewWelcome = findViewById(R.id.textView_show_welcome);
        textViewFullName = findViewById(R.id.textView_show_full_name);
        textViewEmail = findViewById(R.id.textView_show_email);
        textViewDoB = findViewById(R.id.textView_show_dob);
        textViewGender = findViewById(R.id.textView_show_gender);
        textViewMobile = findViewById(R.id.textView_show_mobile);
        progressBar = findViewById(R.id.progress_bar);

        //Set onClickListener on ImageView to open Upload Profile Pic Activity
        imageview = findViewById(R.id.imageView_profile_dp);
        imageview.setOnClickListener(v -> {
            Intent intent = new Intent(CreatorProfile.this, UploadProfilePic.class);
            startActivity(intent);
        });

        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser= authProfile.getCurrentUser();

        if (firebaseUser == null){
            Toast.makeText(this, "Something Went Wrong! User's details not available at the moment", Toast.LENGTH_LONG).show();

        } else {
            checkifEmailVerified(firebaseUser);
            progressBar.setVisibility(View.VISIBLE);
            showUserProfile(firebaseUser);

        }
    }

    private void swipeToRefresh() {
        //Look for swipe container
        swipeContainer = findViewById(R.id.swipeContainer);

        //set up refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(() -> {
            //code to refresh goes here.
            startActivity(getIntent());
            finish();
            overridePendingTransition(0,0);
            swipeContainer.setRefreshing(false);
        });

        //Configure refresh colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_green_light, android.R.color.holo_orange_light, android.R.color.holo_red_light);
    }


    //Users coming to creator profile after successful registration
    private void checkifEmailVerified(FirebaseUser firebaseUser) {
        if (!firebaseUser.isEmailVerified()){
            showAlertDialog();
        }
    }

    private void showAlertDialog() {
        //setup alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(CreatorProfile.this);
        builder.setTitle("Email Not Verified");
        builder.setMessage("Please verify your email now. You can not login without email verification next time.");

        //open email app if user clicks/taps "continue"
        builder.setPositiveButton("Continue", (dialog, which) -> {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_APP_EMAIL);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //TO EMAIL APP ON A NEW WINDOW
            startActivity(intent);
        });

        //create alert box
        AlertDialog alertDialog = builder.create();

        //show alert dialog
        alertDialog.show();
    }

    private void showUserProfile(FirebaseUser firebaseUser) {
        String userID = firebaseUser.getUid();

        //Extracting User Reference from Database for "Registered Users"
        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");
        referenceProfile.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWriteUserDetails readUserDetails = snapshot.getValue(ReadWriteUserDetails.class);
                if (readUserDetails != null){
                    fullName = firebaseUser.getDisplayName();
                    email = firebaseUser.getEmail();
                    doB = readUserDetails.doB;
                    gender = readUserDetails.gender;
                    mobile = readUserDetails.mobile;

                    textViewWelcome.setText(getString(R.string.welcome_head_profile, fullName));
                    textViewFullName.setText(fullName);
                    textViewEmail.setText(email);
                    textViewDoB.setText(doB);
                    textViewGender.setText(gender);
                    textViewMobile.setText(mobile);

                    //Set user dp (after upload)
                    Uri uri = firebaseUser.getPhotoUrl();

                    // Use Picasso.get() instead of Picasso.with()
                    Picasso.get().load(uri).into(imageview);

                } else {
                    Toast.makeText(CreatorProfile.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CreatorProfile.this, "Something Went Wrong! ", Toast.LENGTH_LONG).show();
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
            NavUtils.navigateUpFromSameTask(CreatorProfile.this);

        } else if (id == R.id.menu_refresh){
            //refresh activity
            startActivity(getIntent());
            finish();
            overridePendingTransition(0,0);
            
        } else if (id == R.id.menu_update_profile) {
            Intent intent = new Intent(CreatorProfile.this, UpdateProfile.class);
            startActivity(intent);
            finish();

        } else if (id == R.id.menu_update_email) {
            Intent intent = new Intent(CreatorProfile.this, UpdateEmail.class);
            startActivity(intent);
            finish();

        } else if (id == R.id.menu_change_password) {
            Intent intent = new Intent(CreatorProfile.this, ChangePassword.class);
            startActivity(intent);
            finish();

        } else if (id == R.id.menu_delete_profile) {
            Intent intent = new Intent(CreatorProfile.this, DeleteProfile.class);
            startActivity(intent);

        } else {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();

        }

        return super.onOptionsItemSelected(item);
    }
}