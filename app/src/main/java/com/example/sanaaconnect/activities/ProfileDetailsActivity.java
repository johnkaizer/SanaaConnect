package com.example.sanaaconnect.activities;

import static com.example.sanaaconnect.constants.Constants.getUserFullName;
import static com.example.sanaaconnect.constants.Constants.getUserUid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sanaaconnect.Adapters.ReviewAdapter;
import com.example.sanaaconnect.R;
import com.example.sanaaconnect.constants.Constants;
import com.example.sanaaconnect.databinding.ActivityHomeDashBinding;
import com.example.sanaaconnect.databinding.ActivityProfileDetailsBinding;
import com.example.sanaaconnect.models.JobModel;
import com.example.sanaaconnect.models.ReviewModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ProfileDetailsActivity extends AppCompatActivity {

    private ActivityProfileDetailsBinding binding;
    private RecyclerView recyclerViewReviews;
    private ReviewAdapter reviewAdapter;
    String fullNameUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        recyclerViewReviews = findViewById(R.id.reviewsRV);
        recyclerViewReviews.setLayoutManager(new LinearLayoutManager(this));
        reviewAdapter = new ReviewAdapter(this, new ArrayList<>());
        recyclerViewReviews.setAdapter(reviewAdapter);
        String userId = getUserUid();
        getUserFullName(userId, new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                fullNameUser = dataSnapshot.getValue(String.class);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle onCancelled event
            }
        });


        // Get data from intent extras
        String fullName = getIntent().getStringExtra("fullName");
        String title = getIntent().getStringExtra("title");
        String charges = getIntent().getStringExtra("charges");
        String email = getIntent().getStringExtra("email");
        String education = getIntent().getStringExtra("education");
        String location = getIntent().getStringExtra("location");
        String experience = getIntent().getStringExtra("experience");
        String clientId = getIntent().getStringExtra("jobClientId");

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

        // Fetch and display reviews for the specific user
        fetchAndDisplayReviews(clientId);

        binding.review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileDetailsActivity.this);
                View dialogView = getLayoutInflater().inflate(R.layout.add_review, null);

                EditText ContentEt = dialogView.findViewById(R.id.idEdTitle);

                builder.setView(dialogView);
                AlertDialog dialog = builder.create();
                dialogView.findViewById(R.id.add).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String content = ContentEt.getText().toString();
                        String ownerName = fullNameUser;
                        String clientId = getIntent().getStringExtra("jobClientId");
                        String ownerId = getUserUid();
                        String date = ReviewModel.getCurrentDate(); // Get current date

                        int reactions = 0;

                        // Check if any of the fields are empty
                        if (TextUtils.isEmpty(content)) {
                            Toast.makeText(getApplicationContext(), "Please fill all the fields", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Save the review to Firebase
                        DatabaseReference reviewsRef = FirebaseDatabase.getInstance().getReference("reviews");
                        String reviewId = reviewsRef.push().getKey(); // Generate unique key for the review
                        ReviewModel review = new ReviewModel(clientId, ownerId, ownerName, content, date, reactions);
                        if (reviewId != null) {
                            reviewsRef.child(reviewId).setValue(review)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(ProfileDetailsActivity.this, "Review added successfully", Toast.LENGTH_SHORT).show();
                                            dialog.dismiss();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(ProfileDetailsActivity.this, "Failed to add review: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }

                        // Clear the fields in your dialog
                        ContentEt.getText().clear();

                        // Dismiss the dialog
                        dialog.dismiss();
                    }
                });

                dialogView.findViewById(R.id.cancelButton).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Dismiss the dialog
                        dialog.dismiss();
                    }
                });

                if (dialog.getWindow() != null) {
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                }
                dialog.show();
            }
        });


    }

    private void fetchAndDisplayReviews(String clientId) {
        DatabaseReference reviewsRef = FirebaseDatabase.getInstance().getReference("reviews");
        Query query = reviewsRef.orderByChild("clientId").equalTo(clientId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<ReviewModel> reviews = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ReviewModel review = snapshot.getValue(ReviewModel.class);
                    if (review != null) {
                        reviews.add(review);
                    }
                }
                reviewAdapter.setReviews(reviews);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle onCancelled event
            }
        });
    }
}