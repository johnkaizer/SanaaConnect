package com.example.sanaaconnect.activities;

import static com.example.sanaaconnect.constants.Constants.getUserUid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sanaaconnect.R;
import com.example.sanaaconnect.databinding.ActivityJobDetailsBinding;
import com.example.sanaaconnect.databinding.ActivityProfileDetailsBinding;
import com.example.sanaaconnect.models.MessageModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class JobDetailsActivity extends AppCompatActivity {
    private ActivityJobDetailsBinding binding;
    TextView sendEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityJobDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Get data from intent extras
        String jobTitle = getIntent().getStringExtra("jobTitle");
        String jobAmount = getIntent().getStringExtra("jobAmount");
        String jobDescription = getIntent().getStringExtra("jobDescription");
        String jobPostDate = getIntent().getStringExtra("jobPostDate");
        String jobDeadline = getIntent().getStringExtra("jobDeadline");
        String userEmail = getIntent().getStringExtra("userEmail");
        String recieverId = getIntent().getStringExtra("jobClientId");

        // Use the data as needed
        TextView titleTextView = findViewById(R.id.jobTitleTextView);
        TextView amountTextView = findViewById(R.id.amountTextView);
        TextView descriptionTextView = findViewById(R.id.descriptionTextView);
        TextView postDateTextView = findViewById(R.id.postedTextView);
        TextView jobDeadlineTextView = findViewById(R.id.deadline);
        TextView sendEmailTextView = findViewById(R.id.sendMail); // TextView for sending email

        titleTextView.setText(jobTitle);
        jobDeadlineTextView.setText(jobDeadline);
        amountTextView.setText("Ksh " + jobAmount);
        descriptionTextView.setText(jobDescription);
        binding.chart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(JobDetailsActivity.this);
                View dialogView = getLayoutInflater().inflate(R.layout.add_message, null);

                EditText MessageEt = dialogView.findViewById(R.id.idEdMessage);

                builder.setView(dialogView);
                AlertDialog dialog = builder.create();
                dialogView.findViewById(R.id.add1).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String content = MessageEt.getText().toString();
                        String userName = generateUniqueUsername();
                        String receiverId = getIntent().getStringExtra("jobClientId");
                        String senderId = getUserUid();
                        String timeStamp = MessageModel.getCurrentTimeStamp();

                        // Send message
                        sendMessage(content, userName, receiverId, senderId, timeStamp);

                        // Clear the fields in your dialog and dismiss it
                        MessageEt.getText().clear();
                        dialog.dismiss();
                    }
                });

                dialogView.findViewById(R.id.cancelButton1).setOnClickListener(new View.OnClickListener() {
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

        // Set the email address to the TextView
        sendEmailTextView.setText(userEmail);
        sendEmailTextView.setText("Click the email (" + userEmail + ") to send proposal");

        sendEmailTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to launch Gmail with the specified email address
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:" + userEmail));

                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    // Handle if no email app is available
                    Toast.makeText(getApplicationContext(), "No email app found", Toast.LENGTH_SHORT).show();
                }
            }
        });

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

    private void sendMessage(String content, String userName, String receiverId, String senderId, String timeStamp) {
        // Reference to your chats
        DatabaseReference chatsRef = FirebaseDatabase.getInstance().getReference("Chats");

        // Query for existing conversation between senderId and receiverId
        chatsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String chatId = null;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot messageSnapshot : snapshot.child("messages").getChildren()) {
                        MessageModel message = messageSnapshot.getValue(MessageModel.class);
                        if (message != null && ((message.getSenderId().equals(senderId) && message.getRecieverId().equals(receiverId)) ||
                                (message.getRecieverId().equals(senderId) && message.getSenderId().equals(receiverId)))) {
                            chatId = snapshot.getKey();
                            break;
                        }
                    }

                    if (chatId != null) break;
                }

                if (chatId == null) {
                    // No existing conversation found, create a new one
                    chatId = chatsRef.push().getKey();
                }

                // Now we have chatId, proceed to send message
                if (chatId != null) {
                    DatabaseReference chatRef = chatsRef.child(chatId).child("messages");
                    String messageId = chatRef.push().getKey();

                    MessageModel message = new MessageModel(messageId, chatId, receiverId, senderId, content, timeStamp, userName);
                    chatRef.child(messageId).setValue(message)
                            .addOnSuccessListener(aVoid -> {
                                // Handle success
                                Toast.makeText(JobDetailsActivity.this, "Message sent successfully", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                // Handle failure
                                Toast.makeText(JobDetailsActivity.this, "Failed to send message: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                } else {
                    // Handle error, failed to create chatId
                    Toast.makeText(JobDetailsActivity.this, "Error creating chat session", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors
                Toast.makeText(JobDetailsActivity.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String generateUniqueUsername() {
        String userName = "Unknown";
        Random random = new Random();
        int randomNumber = random.nextInt(1000);
        return userName + randomNumber;
    }
}