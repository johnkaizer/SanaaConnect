package com.example.sanaaconnect.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sanaaconnect.Adapters.CategoryAdapter;
import com.example.sanaaconnect.Adapters.JobAdapter;
import com.example.sanaaconnect.Adapters.ProfessionAdapter;
import com.example.sanaaconnect.R;
import com.example.sanaaconnect.databinding.FragmentHomeBinding;
import com.example.sanaaconnect.models.CategoryModel;
import com.example.sanaaconnect.models.JobModel;
import com.example.sanaaconnect.models.ProfessionModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomeFragment extends Fragment implements CategoryAdapter.OnItemClickListener {

    private FragmentHomeBinding binding;
    //Professionals
    ArrayList<ProfessionModel> professionList;
    ProfessionAdapter professionAdapter;

    //Jobs
    ArrayList<JobModel> jobList;
    JobAdapter jobAdapter;

    //Categories
    ArrayList<CategoryModel> categoryModels;
    CategoryAdapter categoryAdapter;
    EditText searchEd;
    private ProgressBar loadingProgressBar;
    RecyclerView homeRec;
    Query databaseReference;

    RecyclerView categoryRV;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        homeRec = root.findViewById(R.id.notRec);
        searchEd = root.findViewById(R.id.search_Txt);
        //Items category headers
        categoryRV = root.findViewById(R.id.catRv);
        categoryModels = new ArrayList<>();
        categoryModels.add(new CategoryModel("Job Listings"));
        categoryModels.add(new CategoryModel("Professionals"));

        categoryAdapter = new CategoryAdapter(getActivity(), categoryModels, this);
        categoryRV.setAdapter(categoryAdapter);
        categoryRV.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));
        categoryRV.setNestedScrollingEnabled(false);
        // Find the index of the "Donations" category
        int donationsIndex = -1;
        for (int i = 0; i < categoryModels.size(); i++) {
            if (categoryModels.get(i).getHeader().equals("Job Listings")) {
                donationsIndex = i;
                break;
            }
        }

        // Set the "Jobs" category as selected by default
        categoryAdapter.setSelectedItem(donationsIndex);

        // Trigger the initial retrieval of donations
        if (isInternetAvailable()) {
            // Internet is available, proceed with database calls
            getJobs();
        } else {

            Toast.makeText(getContext(), "No internet connection", Toast.LENGTH_SHORT).show();
        }
        searchEd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed in this case
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();
                if (isInternetAvailable()) {
                    performSearch(query);
                } else {
                    Toast.makeText(getContext(), "No internet connection", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Not needed in this case
            }
        });
        // Initialize loadingProgressBar
        loadingProgressBar = root.findViewById(R.id.loadingProgressBar);


        return root;
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

    private void filterJobs(String query) {
        ArrayList<JobModel> filteredList = new ArrayList<>();
        for (JobModel jobModel : jobList) {
            if (jobModel.getJobTitle().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(jobModel);
            }
        }
        jobAdapter.filterList(filteredList);
    }

    private void filterProfesionals(String query) {
        ArrayList<ProfessionModel> filteredList = new ArrayList<>();
        for (ProfessionModel profession : professionList) {
            if (profession.getTitle().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(profession);
            }
        }
        professionAdapter.filterList(filteredList);
    }

    private void performSearch(String query) {
        int selectedItem = categoryAdapter.getSelectedItem();
        if (selectedItem == 0) {
            filterJobs(query);
        } else if (selectedItem == 1) {
            filterProfesionals(query);
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private boolean isInternetAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    @Override
    public void onItemClick(CategoryModel category) {
        String categoryName = category.getHeader();
        if (isInternetAvailable()) {
            switch (categoryName) {
                case "Job Listings":
                    getJobs();
                    break;
                case "Professionals":
                    getProfessionals();
                    break;
                default:
                    // Handle unsupported category
                    break;
            }
        } else {
            Toast.makeText(getContext(), "No internet connection", Toast.LENGTH_SHORT).show();
        }
    }

    private void getJobs() {
        showLoadingIndicator();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Jobs");
        homeRec.setHasFixedSize(true);
        jobList = new ArrayList<>();
        jobAdapter = new JobAdapter(jobList,getContext());
        homeRec.setAdapter(jobAdapter);
        homeRec.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        homeRec.setNestedScrollingEnabled(false);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Clear the existing list before adding new items
                jobList.clear();
                // Process the retrieved data and add it to the list
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    JobModel jobModel = dataSnapshot.getValue(JobModel.class);
                    jobList.add(jobModel);
                }

                // Notify the adapter about the data change
                jobAdapter.notifyDataSetChanged();

                // Check if the list is empty
                if (jobList.isEmpty()) {
                    // Show the animation and hide the RecyclerView
                    homeRec.setVisibility(View.GONE);
                } else {
                    //  show the RecyclerView
                    homeRec.setVisibility(View.VISIBLE);
                }
                hideLoadingIndicator();

            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Sorry, could not fetch anything", Toast.LENGTH_SHORT).show();

                homeRec.setVisibility(View.GONE);
                hideLoadingIndicator();

            }

        });
    }

    private void getProfessionals() {
        showLoadingIndicator();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("SkillsProfile");
        homeRec.setHasFixedSize(true);
        professionList = new ArrayList<>();
        professionAdapter = new ProfessionAdapter(professionList,getContext());
        homeRec.setAdapter(professionAdapter);
        homeRec.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        homeRec.setNestedScrollingEnabled(false);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Clear the existing list before adding new items
                professionList.clear();
                // Iterate through each userUid node
                for (DataSnapshot userUidSnapshot : snapshot.getChildren()) {
                    // Iterate through each professionDetails node under the userUid node
                    for (DataSnapshot professionDetailsSnapshot : userUidSnapshot.getChildren()) {
                        // Retrieve the profession details
                        ProfessionModel professionModel = professionDetailsSnapshot.getValue(ProfessionModel.class);
                        // Add the profession details to the list
                        professionList.add(professionModel);
                    }
                }

                // Notify the adapter about the data change
                professionAdapter.notifyDataSetChanged();

                // Check if the list is empty
                if (professionList.isEmpty()) {
                    // Show the animation and hide the RecyclerView
                    homeRec.setVisibility(View.GONE);
                } else {
                    //  show the RecyclerView
                    homeRec.setVisibility(View.VISIBLE);
                }
                hideLoadingIndicator();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Sorry, could not fetch anything", Toast.LENGTH_SHORT).show();
                homeRec.setVisibility(View.GONE);
                hideLoadingIndicator();
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        // Check if internet is available
        if (isInternetAvailable()) {
            int selectedItem = categoryAdapter.getSelectedItem();
            switch (selectedItem) {
                case 0: // Jobs
                    getJobs();
                    break;
                case 1: // Professionals
                    getProfessionals();
                    break;
            }
        } else {
            // Internet is not available, show the error animation and hide the RecyclerView
            homeRec.setVisibility(View.GONE);
            Toast.makeText(getContext(), "No internet connection", Toast.LENGTH_SHORT).show();
        }
    }


}