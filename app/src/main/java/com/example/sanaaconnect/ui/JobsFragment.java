package com.example.sanaaconnect.ui;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sanaaconnect.Adapters.JobAdapter;
import com.example.sanaaconnect.Adapters.JobManagementAdapter;
import com.example.sanaaconnect.R;
import com.example.sanaaconnect.constants.Constants;
import com.example.sanaaconnect.databinding.FragmentJobsBinding;
import com.example.sanaaconnect.models.JobModel;
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
import java.util.Locale;

public class JobsFragment extends Fragment {

    private FragmentJobsBinding binding;
    private ProgressDialog progressDialog;
    private ArrayList<JobModel> jobList;
    private DatabaseReference databaseReference;
    private RecyclerView jobRecyclerView;
    private String clientId;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentJobsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Uploading data...");
        progressDialog.setCancelable(false);
        jobRecyclerView = root.findViewById(R.id.jobsRv);
        jobRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        jobList = new ArrayList<>();

        // Initialize Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Jobs"); // Replace with your actual Firebase node

        fetchJobs();
        binding.addJobs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                View dialogView = getLayoutInflater().inflate(R.layout.add_job, null);

                EditText title = dialogView.findViewById(R.id.idEdTitle);
                EditText description = dialogView.findViewById(R.id.idEdDesc);
                EditText salary = dialogView.findViewById(R.id.idEdAmount);
                EditText deadline = dialogView.findViewById(R.id.idDeadline);

                builder.setView(dialogView);
                AlertDialog dialog = builder.create();

                // Set OnClickListener for pickDateButton to show date picker
                deadline.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Initialize Calendar instance
                        final Calendar calendar = Calendar.getInstance();

                        // Create DatePickerDialog with the current date
                        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                                new DatePickerDialog.OnDateSetListener() {
                                    @Override
                                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                        // Set the chosen date on EditText
                                        String selectedDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                                        deadline.setText(selectedDate);
                                    }
                                },
                                calendar.get(Calendar.YEAR),
                                calendar.get(Calendar.MONTH),
                                calendar.get(Calendar.DAY_OF_MONTH));

                        // Show the DatePickerDialog
                        datePickerDialog.show();
                    }
                });

                dialogView.findViewById(R.id.add).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String Title = title.getText().toString();
                        String Desc = description.getText().toString();
                        String amount = salary.getText().toString();
                        String Deadline = deadline.getText().toString();
                        String email = Constants.getUserEmail();

                        // Check if any of the fields are empty
                        if (TextUtils.isEmpty(Title) || TextUtils.isEmpty(Desc) || TextUtils.isEmpty(amount) || TextUtils.isEmpty(Deadline)) {
                            Toast.makeText(getContext(), "Please fill all the fields", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Retrieve clientId from SharedPreferences
                        SharedPreferences preferences = requireActivity().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
                        String clientId = preferences.getString("clientId", "");

                        // Generate postDate in the required format
                        SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd h:mma", Locale.getDefault());
                        String postDate = sdf.format(new Date());

                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Jobs");
                        JobModel jobModel = new JobModel(clientId, Title, Desc, amount, postDate, Deadline,email);
                        databaseReference.child(Title).setValue(jobModel);

                        Toast.makeText(getContext(), "Data saved ", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();

                        // Clear the fields in your dialog
                        title.getText().clear();
                        description.getText().clear();
                        salary.getText().clear();
                        deadline.getText().clear();

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

        return root;
    }

    private void fetchJobs() {
        // Retrieve staff id from SharedPreferences
        SharedPreferences preferences = requireActivity().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        clientId = preferences.getString("clientId", "");

        // Query to filter zones based on staff ID
        Query query = databaseReference.orderByChild("clientId").equalTo(clientId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                jobList.clear(); // Clear existing data

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    // Parse job data from Firebase
                    JobModel jobs = dataSnapshot.getValue(JobModel.class);

                    if (jobs != null) {
                        jobList.add(jobs); // Add jobs to the list
                    }
                }

                // Create and set the adapter for the RecyclerView
                JobManagementAdapter jobAdapter = new JobManagementAdapter(jobList,getContext());
                jobRecyclerView.setAdapter(jobAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle the error
                Toast.makeText(getContext(), "Failed to fetch jobs from Firebase", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
