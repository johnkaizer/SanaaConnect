package com.example.sanaaconnect.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sanaaconnect.activities.JobDetailsActivity;
import com.example.sanaaconnect.R;
import com.example.sanaaconnect.models.JobModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class JobAdapter extends RecyclerView.Adapter<JobAdapter.ViewHolder> {
    List<JobModel>jobList;
    Context context;

    public JobAdapter(List<JobModel> jobList, Context context) {
        this.jobList = jobList;
        this.context = context;
    }

    public void filterList(ArrayList<JobModel> filteredList) {
        jobList = filteredList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public JobAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.job_item, parent, false));

    }
    @Override
    public void onBindViewHolder(@NonNull JobAdapter.ViewHolder holder, int position) {
        JobModel jobModel = jobList.get(position);
        holder.titleTxt.setText(jobModel.getJobTitle());
        holder.amountTxt.setText("Ksh " + jobModel.getAmount());
        holder.contentTxt.setText(jobModel.getDescription());

        // Parse the saved post date
        SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd h:mma", Locale.getDefault());
        Date postDate = null;
        try {
            postDate = sdf.parse(jobModel.getPostDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Ensure both postDate and currentDate are not null before proceeding
        if (postDate != null) {
            // Get the current date and time
            Date currentDate = new Date();

            // Calculate the time difference in milliseconds if currentDate is not null
            if (currentDate != null) {
                long timeDifference = currentDate.getTime() - postDate.getTime();

                // Convert milliseconds to minutes, hours, or days
                long minutesDifference = TimeUnit.MILLISECONDS.toMinutes(timeDifference);
                long hoursDifference = TimeUnit.MILLISECONDS.toHours(timeDifference);
                long daysDifference = TimeUnit.MILLISECONDS.toDays(timeDifference);

                // Determine the appropriate format based on the time difference
                String timeAgo;
                if (minutesDifference < 60) {
                    timeAgo = minutesDifference + " minutes ago";
                } else if (hoursDifference < 24) {
                    timeAgo = hoursDifference + " hours ago";
                } else {
                    timeAgo = daysDifference + " days ago";
                }

                // Set the calculated time difference to the date text view
                holder.dateTxt.setText(timeAgo);
            } else {
                // Handle the case where currentDate is null (unlikely to happen)
                holder.dateTxt.setText("Unknown date");
            }
        } else {
            // Handle the case where postDate is null (e.g., invalid date format)
            holder.dateTxt.setText("Invalid date");
        }
        // Set onClickListener for the card view
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Open DetailsActivity and send the jobModel data
                Intent intent = new Intent(context, JobDetailsActivity.class);
                intent.putExtra("jobTitle", jobModel.getJobTitle());
                intent.putExtra("jobAmount", jobModel.getAmount());
                intent.putExtra("jobDescription", jobModel.getDescription());
                intent.putExtra("jobPostDate", jobModel.getPostDate());
                intent.putExtra("jobDeadline", jobModel.getDeadlineDate());
                intent.putExtra("userEmail", jobModel.getEmail());
                intent.putExtra("jobClientId", jobModel.getClientId());
                context.startActivity(intent);
            }
        });
        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                // Check if the current user is an admin (You need to implement this logic)
                // Call isAdmin() method and handle the result
                isAdmin(new AdminCheckListener() {
                    @Override
                    public void onAdminChecked(boolean isAdmin) {
                        if (isAdmin) {
                            // User is an admin, perform admin-specific actions
                            showDeleteConfirmationDialog(jobModel);
                        } else {
                            // User is not an admin
                            Toast.makeText(context, "Only admins can delete jobs", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                return false; // Not consumed, proceed with regular click handling
            }
        });

    }

    private void isAdmin(AdminCheckListener listener) {
        // Get the current user's UID from Firebase Authentication
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();

            // Reference to the "Users" table in the Firebase Realtime Database
            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Users");

            // Query to get the role of the current user using their UID
            usersRef.child(uid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    boolean isAdmin = false;
                    // Check if the user exists in the database
                    if (dataSnapshot.exists()) {
                        // Get the role of the user
                        String role = dataSnapshot.child("role").getValue(String.class);

                        // Check if the role is admin
                        if (role != null && role.equals("Admin")) {
                            // User is an admin
                            isAdmin = true;
                        }
                    }
                    // Notify the listener with the result
                    listener.onAdminChecked(isAdmin);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle database error
                }
            });
        }
    }

    // Define an interface for the listener
    interface AdminCheckListener {
        void onAdminChecked(boolean isAdmin);
    }

    private void showDeleteConfirmationDialog(JobModel jobModel) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Confirm Deletion");
        builder.setMessage("Are you sure you want to delete this job?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteJob(jobModel.getJobTitle());
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void deleteJob(String jobTitle) {
        DatabaseReference jobRef = FirebaseDatabase.getInstance().getReference("Jobs");
        jobRef.orderByChild("jobTitle").equalTo(jobTitle).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    snapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }

    @Override
    public int getItemCount() {
        return jobList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTxt;
        TextView amountTxt;
        TextView dateTxt;
        TextView contentTxt;
        CardView cardView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTxt = itemView.findViewById(R.id.name_txt);
            amountTxt = itemView.findViewById(R.id.amount);
            dateTxt = itemView.findViewById(R.id.review_time);
            contentTxt = itemView.findViewById(R.id.content);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }
}
