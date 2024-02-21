package com.example.sanaaconnect.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sanaaconnect.R;
import com.example.sanaaconnect.models.JobModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Firebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class JobManagementAdapter extends RecyclerView.Adapter<JobManagementAdapter.ViewHolder> {
    List<JobModel>jobList;
    Context context;

    public JobManagementAdapter(List<JobModel> jobList, Context context) {
        this.jobList = jobList;
        this.context = context;
    }

    public void filterList(ArrayList<JobModel> filteredList) {
        jobList = filteredList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public JobManagementAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.job_item, parent, false));

    }
    @Override
    public void onBindViewHolder(@NonNull JobManagementAdapter.ViewHolder holder, int position) {
        JobModel jobModel = jobList.get(position);
        holder.titleTxt.setText(jobModel.getJobTitle());
        holder.amountTxt.setText("Ksh " + jobModel.getAmount());
        holder.contentTxt.setText(jobModel.getDescription());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(context, view);
                popupMenu.inflate(R.menu.popup_menu_management);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.menu_edit) {
                            // Implement edit action here
                            editJob(jobModel);
                            return true;
                        } else if (item.getItemId() == R.id.menu_delete) {
                            // Implement delete action here
                            deleteJob(jobModel);
                            return true;
                        } else {
                            return false;
                        }
                    }
                });
                popupMenu.show();
            }
        });


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
    }

    private void deleteJob(JobModel jobModel) {
        String clientId;
        // Get the currently logged-in user's ID
        SharedPreferences preferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        clientId = preferences.getString("clientId", "");
        String loggedInUserId = clientId;

        // Check if the job's clientId matches the logged-in user's ID
        if (jobModel.getClientId().equals(loggedInUserId)) {
            // If the IDs match, delete the job
             DatabaseReference jobRef = FirebaseDatabase.getInstance().getReference().child("Jobs").child(jobModel.getJobTitle());
            jobRef.removeValue()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Job deleted successfully
                            Toast.makeText(context, "Job deleted successfully", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Failed to delete the job
                            Toast.makeText(context, "Failed to delete job: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            // If the IDs do not match, show a message indicating that the user does not have permission to delete this job
            Toast.makeText(context, "You do not have permission to delete this job", Toast.LENGTH_SHORT).show();
        }
    }


    private void editJob(JobModel jobModel) {
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
