package com.example.sanaaconnect.activities;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.sanaaconnect.Adapters.JobManagementAdapter;
import com.example.sanaaconnect.Adapters.PortfolioAdapter;
import com.example.sanaaconnect.R;
import com.example.sanaaconnect.constants.Constants;
import com.example.sanaaconnect.models.JobModel;
import com.example.sanaaconnect.models.Portfolio;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PortfolioActivity extends AppCompatActivity {
    ImageView imageView;
    AppCompatButton pickImage;
    AppCompatButton submitImage;
    RecyclerView recyclerViewImage;
     ArrayList<Portfolio> portfolios;
    String userIdentity;
    Uri imageUri;

    FirebaseStorage storage = FirebaseStorage.getInstance();
    DatabaseReference databaseReference;
    ProgressBar progressBar;

    private final ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
            uri -> {
                // Handle the returned Uri, for example, set it to an ImageView
                imageView.setImageURI(uri);
                imageUri = uri; // Keep a reference to the Uri of the picked image
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portfolio);

        imageView = findViewById(R.id.product_image);
        pickImage = findViewById(R.id.pick_btn);
        submitImage = findViewById(R.id.submit_btn);
        recyclerViewImage = findViewById(R.id.recyclerViewPortfolio);
        userIdentity = Constants.getUserUid(); // Ensure you have this method in your Constants class
        recyclerViewImage.setLayoutManager(new LinearLayoutManager(PortfolioActivity.this));
         portfolios = new ArrayList<>();
        // Initialize the ProgressBar
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        // Initialize your database reference
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Portfolios");

        pickImage.setOnClickListener(v -> mGetContent.launch("image/*"));

        submitImage.setOnClickListener(v -> {
            if (imageUri != null) {
                uploadImageToFirebase(imageUri);
            }
        });
        // Fetch portfolios
        fetchPortfolios();
    }

    private void fetchPortfolios() {
        databaseReference.orderByChild("userUid").equalTo(userIdentity).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Portfolio> portfolios = new ArrayList<>(); // Initialize a new list for portfolios

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Portfolio portfolio = snapshot.getValue(Portfolio.class);
                    if (portfolio != null) {
                        portfolios.add(portfolio); // Add the portfolio to your list
                    }
                }

                // Correct instantiation of PortfolioAdapter with the context and the portfolio list
                PortfolioAdapter portfolioAdapter = new PortfolioAdapter(PortfolioActivity.this, portfolios);
                recyclerViewImage.setAdapter(portfolioAdapter);
                portfolioAdapter.notifyDataSetChanged(); // Notify the adapter that the data set has changed
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(PortfolioActivity.this, "Error loading portfolios: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadImageToFirebase(Uri uri) {
        Toast loadingToast = Toast.makeText(PortfolioActivity.this, "Uploading...", Toast.LENGTH_SHORT);
        loadingToast.show();
        progressBar.setVisibility(View.VISIBLE);

        StorageReference fileRef = storage.getReference().child("images/" + userIdentity + "/" + System.currentTimeMillis() + ".jpg");
        fileRef.putFile(uri)
                .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri1 -> {
                    String imageUrl = uri1.toString();
                    savePortfolioDetails(imageUrl);
                    progressBar.setVisibility(View.GONE);
                    loadingToast.cancel();
                    Toast.makeText(PortfolioActivity.this, "Upload successful", Toast.LENGTH_SHORT).show();
                }))
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    loadingToast.cancel();
                    Toast.makeText(PortfolioActivity.this, "Upload failed: " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void savePortfolioDetails(String imageUrl) {
        String key = databaseReference.push().getKey();
        Map<String, Object> portfolio = new HashMap<>();
        portfolio.put("imageUrl", imageUrl);
        portfolio.put("timeStamp", ServerValue.TIMESTAMP); // Use Firebase server timestamp
        portfolio.put("userUid", userIdentity);

        if (key != null) {
            databaseReference.child(key).setValue(portfolio)
                    .addOnSuccessListener(aVoid -> Toast.makeText(PortfolioActivity.this, "Portfolio added", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(PortfolioActivity.this, "Error adding portfolio", Toast.LENGTH_SHORT).show());
        }
    }
}
