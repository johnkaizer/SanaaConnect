package com.example.sanaaconnect.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.example.sanaaconnect.Adapters.PortfolioAdapter;
import com.example.sanaaconnect.R;
import com.example.sanaaconnect.models.Portfolio;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PortfolioViewActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    PortfolioAdapter portfolioAdapter;
    ArrayList<Portfolio> list;
    String clientId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portfolio_view);

        recyclerView = findViewById(R.id.portfolioRV);
        list = new ArrayList<>();
        portfolioAdapter = new PortfolioAdapter(this, list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(portfolioAdapter);

        // Retrieve the clientId from the intent
        clientId = getIntent().getStringExtra("jobClientId");

        // Query Firebase for portfolios where userId matches clientId
        DatabaseReference portfoliosRef = FirebaseDatabase.getInstance().getReference("Portfolios");
        portfoliosRef.orderByChild("userUid").equalTo(clientId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Portfolio portfolio = snapshot.getValue(Portfolio.class);
                    list.add(portfolio);
                }
                portfolioAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(PortfolioViewActivity.this, "Failed to fetch portfolios", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
