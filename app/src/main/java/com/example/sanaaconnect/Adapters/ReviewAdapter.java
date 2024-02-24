package com.example.sanaaconnect.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sanaaconnect.R;
import com.example.sanaaconnect.models.ReviewModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {

    private List<ReviewModel> reviews;
    private Context context;

    public ReviewAdapter(Context context, List<ReviewModel> reviews) {
        this.context = context;
        this.reviews = reviews;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_review, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ReviewModel review = reviews.get(position);

        holder.textViewName.setText(review.getOwnerName());
        holder.textViewContent.setText(review.getContent());
        holder.textViewReactions.setText(String.valueOf(review.getReactions()));
        // Parse the saved post date
        SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd h:mma", Locale.getDefault());
        Date postDate = null;
        try {
            postDate = sdf.parse(review.getDate());
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
                holder.textViewTime.setText(timeAgo);
            } else {
                // Handle the case where currentDate is null (unlikely to happen)
                holder.textViewTime.setText("Unknown date");
            }
        } else {
            // Handle the case where postDate is null (e.g., invalid date format)
            holder.textViewTime.setText("Invalid date");
        }

        holder.imageButtonLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference reviewsRef = FirebaseDatabase.getInstance().getReference("reviews");
                Query query = reviewsRef.orderByChild("clientId").equalTo(review.getClientId());

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            ReviewModel reviewModel = snapshot.getValue(ReviewModel.class);
                            if (reviewModel != null) {
                                // Update the reactions count for this review
                                int currentReactions = reviewModel.getReactions();
                                snapshot.getRef().child("reactions").setValue(currentReactions + 1);
                                // Update the local review object as well
                                review.setReactions(currentReactions + 1);
                                // Update UI
                                notifyItemChanged(holder.getAdapterPosition());
                                break; // Stop the loop after updating the first matching review
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle onCancelled event
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }
    public void setReviews(List<ReviewModel> reviews) {
        this.reviews = reviews;
        notifyDataSetChanged(); // Notify RecyclerView that dataset has changed
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName, textViewContent, textViewTime, textViewReactions;
        ImageButton imageButtonLike;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewContent = itemView.findViewById(R.id.textViewContent);
            textViewTime = itemView.findViewById(R.id.textViewTime);
            textViewReactions = itemView.findViewById(R.id.textViewReactions);
            imageButtonLike = itemView.findViewById(R.id.imageButtonLike);
        }
    }
}

