package com.example.sanaaconnect.Adapters;

import static androidx.fragment.app.FragmentManager.TAG;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sanaaconnect.activities.ConversationActivity;
import com.example.sanaaconnect.R;
import com.example.sanaaconnect.models.MessageModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class MessageAdapters extends RecyclerView.Adapter<MessageAdapters.ViewHolder> {
    Context context;
    List<MessageModel>list;

    public MessageAdapters(Context context, List<MessageModel> list) {
        this.context = context;
        this.list = list;
    }


    @NonNull
    @Override
    public MessageAdapters.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.sms_item, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapters.ViewHolder holder, int position) {
        MessageModel messageModel = list.get(position);
        holder.nameTxt.setText(messageModel.getUserName());
        holder.timeTxt.setText(messageModel.getTimeStamp());

        // Call getUnreadMessageCount to update the counterTextView
        getUnreadMessageCount(messageModel.getChatId(), holder.counterTextView);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create an Intent to launch the ProfileDetails activity
                Intent intent = new Intent(context, ConversationActivity.class);

                // Pass the data to the intent as extras
                intent.putExtra("senderId", messageModel.getSenderId());
                intent.putExtra("receiverId", messageModel.getRecieverId());
                intent.putExtra("chatId", messageModel.getChatId());
                intent.putExtra("fullName", messageModel.getUserName());

                // Start the ProfileDetails activity
                context.startActivity(intent);
            }
        });
    }

    private void getUnreadMessageCount(String chatId, TextView counterTextView) {
        DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference().child("Chats").child(chatId).child("messages");

        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int unreadCount = 0;
                for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                    // Check if the message is unread
                    String isReadValue = messageSnapshot.child("isRead").getValue(String.class);
                    // Assuming "false" indicates unread
                    if (isReadValue != null && isReadValue.equals("false")) {
                        unreadCount++;
                    }
                }
                // Update the counterTextView with the unread message count
                counterTextView.setText(String.valueOf(unreadCount));

                // Set visibility of counterTextView based on unread count
                if (unreadCount > 0) {
                    counterTextView.setVisibility(View.VISIBLE);
                } else {
                    counterTextView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTxt;
        TextView timeTxt;
        CardView cardView;
        TextView counterTextView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTxt = itemView.findViewById(R.id.name_txt);
            timeTxt = itemView.findViewById(R.id.sms_time);
            cardView = itemView.findViewById(R.id.cardView);
            counterTextView = itemView.findViewById(R.id.counter);

        }
    }
}
