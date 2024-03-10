package com.example.sanaaconnect.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.sanaaconnect.Adapters.ProfessionManagementAdapter;
import com.example.sanaaconnect.activities.AddSkillProfileActivity;
import com.example.sanaaconnect.R;
import com.example.sanaaconnect.databinding.FragmentProfessionalRegBinding;
import com.example.sanaaconnect.models.ProfessionModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ProfessionalRegFragment extends Fragment {
    private FragmentProfessionalRegBinding binding;
    private ProgressBar loadingProgressBar;
    private ArrayList<ProfessionModel> professionList;
    ProfessionManagementAdapter professionManagementAdapter;
    private DatabaseReference databaseReference;
    private RecyclerView profRecyclerView;
    private String clientId;
    private EditText searchET;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProfessionalRegBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        profRecyclerView = root.findViewById(R.id.skillsRv);
        searchET = root.findViewById(R.id.editText);

        fetchProfessional();
        setupSearch();
        binding.addSkills.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create an Intent to open the activity for adding new skills
                Intent intent = new Intent(requireContext(), AddSkillProfileActivity.class);
                // Start the activity
                startActivity(intent);
            }
        });
        // Initialize loadingProgressBar
        loadingProgressBar = root.findViewById(R.id.loadingProgressBar);
        return root;
    }

    private void setupSearch() {
        searchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Nothing to do here
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Filter list as the user types
                filterSkills(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Nothing to do here
            }
        });
    }

    private void filterSkills(String query) {
        // New list to hold the filtered skills profile
        ArrayList<ProfessionModel> filteredList = new ArrayList<>();

        // Loop through the original list to find matches
        for (ProfessionModel professionModel : professionList) {
            if (professionModel.getTitle().toLowerCase().contains(query.toLowerCase()) || professionModel.getFullName().toLowerCase().contains(query.toLowerCase())) {
                // If the skills profile title or fullname contains the search query, add it to the filtered list
                filteredList.add(professionModel);
            }
        }

        // Update the RecyclerView with the filtered list
        updateRecyclerView(filteredList);
    }

    private void updateRecyclerView(ArrayList<ProfessionModel> list) {
        ProfessionManagementAdapter professionManagementAdapter1 = new ProfessionManagementAdapter(list, getContext());
        profRecyclerView.setAdapter(professionManagementAdapter1);
    }

    private void showLoadingIndicator() {
        if (loadingProgressBar != null) {
            loadingProgressBar.setVisibility(View.VISIBLE);
        }
    }

    private void hideLoadingIndicator() {
        if (loadingProgressBar != null) {
            loadingProgressBar.setVisibility(View.GONE);
        }
    }

    private void fetchProfessional() {
        showLoadingIndicator();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("SkillsProfile");
        profRecyclerView.setHasFixedSize(true);
        professionList = new ArrayList<>();
        professionManagementAdapter = new ProfessionManagementAdapter(professionList,getContext());
        profRecyclerView.setAdapter(professionManagementAdapter);
        profRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        profRecyclerView.setNestedScrollingEnabled(false);
        // Retrieve staff id from SharedPreferences
        SharedPreferences preferences = requireActivity().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        clientId = preferences.getString("clientId", "");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Clear the existing list before adding new items
                professionList.clear();
                // Iterate through each userUid node
                for (DataSnapshot userUidSnapshot : snapshot.getChildren()) {
                    // Check if the userUid matches the clientId
                    if (userUidSnapshot.getKey().equals(clientId)) {
                        // Iterate through each professionDetails node under the userUid node
                        for (DataSnapshot professionDetailsSnapshot : userUidSnapshot.getChildren()) {
                            // Retrieve the profession details
                            ProfessionModel professionModel = professionDetailsSnapshot.getValue(ProfessionModel.class);
                            // Add the profession details to the list
                            professionList.add(professionModel);
                        }
                    }
                }

                // Notify the adapter about the data change
                professionManagementAdapter.notifyDataSetChanged();

                // Check if the list is empty
                if (professionList.isEmpty()) {
                    // Show the animation and hide the RecyclerView
                    profRecyclerView.setVisibility(View.GONE);
                } else {
                    //  show the RecyclerView
                    profRecyclerView.setVisibility(View.VISIBLE);
                }
                hideLoadingIndicator();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Sorry, could not fetch anything", Toast.LENGTH_SHORT).show();
                profRecyclerView.setVisibility(View.GONE);
                hideLoadingIndicator();
            }
        });
    }

}