package com.example.sanaaconnect.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.example.sanaaconnect.R;

public class ProfileDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_details);
        // Get data from intent extras
        String fullName = getIntent().getStringExtra("fullName");
        String title = getIntent().getStringExtra("title");
        String charges = getIntent().getStringExtra("charges");
        String email = getIntent().getStringExtra("email");
        String education = getIntent().getStringExtra("education");
        String location = getIntent().getStringExtra("location");
        String experience = getIntent().getStringExtra("experience");
        String ClientId = getIntent().getStringExtra("jobClientId");

        // Use the data as needed
        TextView nameTextView = findViewById(R.id.textViewFullName);
        TextView titleTextView = findViewById(R.id.textViewTitle);
        TextView chargesTextView = findViewById(R.id.textViewCharges);
        TextView emailTextView = findViewById(R.id.textViewEmail);
        TextView educationTextView = findViewById(R.id.education);
        TextView locationTextView = findViewById(R.id.textViewLocation);
        TextView experienceTextView = findViewById(R.id.textViewExp);

        titleTextView.setText(title);
        nameTextView.setText(fullName);
        chargesTextView.setText(charges);
        emailTextView.setText(email);
        educationTextView.setText(education);
        locationTextView.setText(location);
        experienceTextView.setText(experience);

    }
}