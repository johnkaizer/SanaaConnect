package com.example.sanaaconnect.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.example.sanaaconnect.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class JobDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_details);
        // Get data from intent extras
        String jobTitle = getIntent().getStringExtra("jobTitle");
        String jobAmount = getIntent().getStringExtra("jobAmount");
        String jobDescription = getIntent().getStringExtra("jobDescription");
        String jobPostDate = getIntent().getStringExtra("jobPostDate");
        String jobDeadline = getIntent().getStringExtra("jobDeadline");
        String ClientId = getIntent().getStringExtra("jobClientId");

        // Use the data as needed
        TextView titleTextView = findViewById(R.id.jobTitleTextView);
        TextView amountTextView = findViewById(R.id.amountTextView);
        TextView descriptionTextView = findViewById(R.id.descriptionTextView);
        TextView postDateTextView = findViewById(R.id.postedTextView);
        TextView jobDeadlineTextView = findViewById(R.id.deadline);

        titleTextView.setText(jobTitle);
        jobDeadlineTextView.setText(jobDeadline);
        amountTextView.setText("Ksh " + jobAmount);
        descriptionTextView.setText(jobDescription);
        // Parse the saved post date
        SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd h:mma", Locale.getDefault());
        Date postDate = null;
        try {
            assert jobPostDate != null;
            postDate = sdf.parse(jobPostDate);
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
                postDateTextView.setText(timeAgo);
            } else {
                // Handle the case where currentDate is null (unlikely to happen)
                postDateTextView.setText("Unknown date");
            }
        } else {
            // Handle the case where postDate is null (e.g., invalid date format)
            postDateTextView.setText("Invalid date");
        }
    }

}