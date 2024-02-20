package com.example.sanaaconnect.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sanaaconnect.R;
import com.example.sanaaconnect.models.JobModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class JobAdapter extends RecyclerView.Adapter<JobAdapter.ViewHolder> {
    List<JobModel>jobList;

    public JobAdapter(List<JobModel> jobList) {
        this.jobList = jobList;
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
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTxt = itemView.findViewById(R.id.name_txt);
            amountTxt = itemView.findViewById(R.id.amount);
            dateTxt = itemView.findViewById(R.id.review_time);
            contentTxt = itemView.findViewById(R.id.content);
        }
    }
}
