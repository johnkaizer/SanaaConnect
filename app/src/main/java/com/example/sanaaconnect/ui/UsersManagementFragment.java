package com.example.sanaaconnect.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.sanaaconnect.Adapters.JobAdapter;
import com.example.sanaaconnect.Adapters.ProfessionAdapter;
import com.example.sanaaconnect.Adapters.UsersAdapter;
import com.example.sanaaconnect.R;
import com.example.sanaaconnect.databinding.FragmentMessagesBinding;
import com.example.sanaaconnect.databinding.FragmentUsersManagementBinding;
import com.example.sanaaconnect.models.JobModel;
import com.example.sanaaconnect.models.ProfessionModel;
import com.example.sanaaconnect.models.Users;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UsersManagementFragment extends Fragment {
    private FragmentUsersManagementBinding binding;
    ArrayList<Users> usersList;
    UsersAdapter usersAdapter;
    RecyclerView usersRec;
    private ProgressBar loadingProgressBar;
    Query databaseReference;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentUsersManagementBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        usersRec = root.findViewById(R.id.usersRv);
        getUsers();
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

    private void getUsers() {
        showLoadingIndicator();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        usersRec.setHasFixedSize(true);
        usersList = new ArrayList<>();
        usersAdapter = new UsersAdapter(usersList,getContext());
        usersRec.setAdapter(usersAdapter);
        usersRec.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        usersRec.setNestedScrollingEnabled(false);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Clear the existing list before adding new items
                usersList.clear();
                // Process the retrieved data and add it to the list
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Users users = dataSnapshot.getValue(Users.class);
                    usersList.add(users);
                }

                // Notify the adapter about the data change
                usersAdapter.notifyDataSetChanged();

                // Check if the list is empty
                if (usersList.isEmpty()) {
                    // Show the animation and hide the RecyclerView
                    usersRec.setVisibility(View.GONE);
                } else {
                    //  show the RecyclerView
                    usersRec.setVisibility(View.VISIBLE);
                }
                hideLoadingIndicator();

            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Sorry, could not fetch anything", Toast.LENGTH_SHORT).show();

                usersRec.setVisibility(View.GONE);
                hideLoadingIndicator();

            }

        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}