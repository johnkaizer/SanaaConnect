package com.example.sanaaconnect.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sanaaconnect.R;
import com.example.sanaaconnect.models.MessageModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class TextAdapters extends RecyclerView.Adapter<TextAdapters.ViewHolder> {
    Context context;
    List<MessageModel> list;
    private String currentUserUid; // This will hold the current user's UID as a string

    public TextAdapters(Context context, List<MessageModel> list) {
        this.context = context;
        this.list = list;
        // Initialize currentUserUid here by fetching the current FirebaseUser and getting its UID
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) { // Check if user is not null to avoid NullPointerException
            this.currentUserUid = currentUser.getUid(); // Get the current user's UID
        }
    }

    @NonNull
    @Override
    public TextAdapters.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.text_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TextAdapters.ViewHolder holder, int position) {
        MessageModel messageModel = list.get(position);

        holder.timeTxt.setText(messageModel.getTimeStamp());
        holder.contentTxt.setText(messageModel.getContent());

        // Use currentUserUid field for comparison
        if (messageModel.getSenderId().equals(currentUserUid)) {
            // Current user is the sender, set background color to green or any color for sent messages
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent2));
        } else {
            // Current user is not the sender, set background color to grey or any color for received messages
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.dark_gray));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView timeTxt;
        TextView contentTxt;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            timeTxt = itemView.findViewById(R.id.review_time);
            contentTxt = itemView.findViewById(R.id.content_txt);

        }
    }
}
