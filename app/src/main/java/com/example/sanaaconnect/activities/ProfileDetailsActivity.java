package com.example.sanaaconnect.activities;

import static com.example.sanaaconnect.constants.Constants.getUserFullName;
import static com.example.sanaaconnect.constants.Constants.getUserUid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sanaaconnect.Adapters.ReviewAdapter;
import com.example.sanaaconnect.R;
import com.example.sanaaconnect.databinding.ActivityProfileDetailsBinding;
import com.example.sanaaconnect.models.MessageModel;
import com.example.sanaaconnect.models.ReviewModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ProfileDetailsActivity extends AppCompatActivity {

    private ActivityProfileDetailsBinding binding;
    private RecyclerView recyclerViewReviews;
    private ReviewAdapter reviewAdapter;
    String fullNameUser;
    TextView ratingsTextView;
    ImageView imageViewUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        recyclerViewReviews = findViewById(R.id.reviewsRV);
        imageViewUrl = findViewById(R.id.imageViewProfile);
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
        String imageUrl = getIntent().getStringExtra("imageUrl");

        // Use the data as needed
        TextView nameTextView = findViewById(R.id.textViewFullName);
        TextView titleTextView = findViewById(R.id.textViewTitle);
        TextView chargesTextView = findViewById(R.id.textViewCharges);
        TextView emailTextView = findViewById(R.id.textViewEmail);
        TextView educationTextView = findViewById(R.id.education);
        TextView locationTextView = findViewById(R.id.textViewLocation);
        TextView experienceTextView = findViewById(R.id.textViewExp);
        ratingsTextView = findViewById(R.id.ratings);

        titleTextView.setText(title);
        nameTextView.setText(fullName);
        chargesTextView.setText(charges);
        emailTextView.setText(email);
        educationTextView.setText(education);
        locationTextView.setText(location);
        experienceTextView.setText(experience);
        Picasso.get().load(imageUrl).into(imageViewUrl);

        // Fetch and display reviews for the specific user
        fetchAndDisplayReviews(clientId);
        binding.textViewPortfolio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create an Intent to launch PortfolioActivity
                Intent intent = new Intent(ProfileDetailsActivity.this, PortfolioViewActivity.class);

                // Add the clientId as an extra to the intent
                intent.putExtra("jobClientId", clientId);

                // Start PortfolioActivity
                startActivity(intent);
            }
        });


        binding.chart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileDetailsActivity.this);
                View dialogView = getLayoutInflater().inflate(R.layout.add_message, null);

                EditText MessageEt = dialogView.findViewById(R.id.idEdMessage);

                builder.setView(dialogView);
                AlertDialog dialog = builder.create();
                dialogView.findViewById(R.id.add1).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String content = MessageEt.getText().toString();
                        String userName = getIntent().getStringExtra("fullName");
                        String receiverId = getIntent().getStringExtra("jobClientId");
                        String senderId = getUserUid();
                        String timeStamp = MessageModel.getCurrentTimeStamp();

                        // Send message
                        sendMessage(content, userName, receiverId, senderId, timeStamp);

                        // Clear the fields in your dialog and dismiss it
                        MessageEt.getText().clear();
                        dialog.dismiss();
                    }
                });

                dialogView.findViewById(R.id.cancelButton1).setOnClickListener(new View.OnClickListener() {
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

        binding.review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileDetailsActivity.this);
                View dialogView = getLayoutInflater().inflate(R.layout.add_review, null);

                EditText ContentEt = dialogView.findViewById(R.id.idEdTitle);
                TextView ratingValue = dialogView.findViewById(R.id.ratingText);
                AppCompatButton minus = dialogView.findViewById(R.id.btnMinus);
                AppCompatButton add = dialogView.findViewById(R.id.btnPlus);
                minus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        float currentRating = Float.parseFloat(ratingValue.getText().toString());
                        if (currentRating > 1.0) {
                            currentRating -= 0.5f;
                            ratingValue.setText(String.valueOf(currentRating));
                        } else {
                            Toast.makeText(getApplicationContext(), "Minimum rating reached", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        float currentRating = Float.parseFloat(ratingValue.getText().toString());
                        if (currentRating < 5.0) {
                            currentRating += 0.5f;
                            ratingValue.setText(String.valueOf(currentRating));
                        } else {
                            Toast.makeText(getApplicationContext(), "Maximum rating reached", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                builder.setView(dialogView);
                AlertDialog dialog = builder.create();
                dialogView.findViewById(R.id.add).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String content = ContentEt.getText().toString();
                        String ownerName = fullNameUser;
                        float rating = Float.parseFloat(ratingValue.getText().toString());
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
                        ReviewModel review = new ReviewModel(clientId, ownerId, ownerName, content, date, reactions, rating);
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

    private void sendMessage(String content, String userName, String receiverId, String senderId, String timeStamp) {
        DatabaseReference chatsRef = FirebaseDatabase.getInstance().getReference("Chats");

        // Generate the chatId using receiverId and senderId
        String chatId = receiverId + "_" + senderId;

        // Reference to the specific chat
        DatabaseReference chatRef = chatsRef.child(chatId).child("messages");

        // Generate unique messageId
        String messageId = chatRef.push().getKey();

        // Create the message object
        MessageModel message = new MessageModel(messageId, receiverId,chatId, senderId, content, timeStamp, userName,"false");

        // Save the message to the chat node
        chatRef.child(messageId).setValue(message)
                .addOnSuccessListener(aVoid -> Toast.makeText(ProfileDetailsActivity.this, "Message sent successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(ProfileDetailsActivity.this, "Failed to send message: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void fetchAndDisplayReviews(String clientId) {
        DatabaseReference reviewsRef = FirebaseDatabase.getInstance().getReference("reviews");
        Query query = reviewsRef.orderByChild("clientId").equalTo(clientId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<ReviewModel> reviews = new ArrayList<>();
                float totalRating = 0;
                int count = 0;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ReviewModel review = snapshot.getValue(ReviewModel.class);
                    if (review != null) {
                        reviews.add(review);
                        totalRating += review.getRating(); // Add up ratings
                        count++;
                    }
                }

                if (count > 0) {
                    float averageRating = totalRating / count; // Calculate average rating
                    // Display average rating
                    ratingsTextView.setText("Average Rating: " + averageRating);
                } else {
                    ratingsTextView.setText("No Reviews Yet");
                }

                // Update the RecyclerView with reviews
                reviewAdapter.setReviews(reviews);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle onCancelled event
            }
        });
    }
}
