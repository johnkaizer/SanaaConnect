package com.example.sanaaconnect.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sanaaconnect.Adapters.TextAdapters;
import com.example.sanaaconnect.R;
import com.example.sanaaconnect.models.MessageModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ConversationActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TextAdapters textAdapters;
    private ArrayList<MessageModel> list;

    private String senderId, receiverId, chatId,fullName;
    private EditText editTextEt;
    private Button sendBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        recyclerView = findViewById(R.id.recyclerViewMessages);
        editTextEt = findViewById(R.id.editTextMessage);
        sendBtn = findViewById(R.id.buttonSend);
        list = new ArrayList<>();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        textAdapters = new TextAdapters(this, list);
        recyclerView.setAdapter(textAdapters);

        // Retrieve senderId, receiverId, and chatId from the intent
        senderId = getIntent().getStringExtra("senderId");
        receiverId = getIntent().getStringExtra("receiverId");
        chatId = getIntent().getStringExtra("chatId");
        fullName = getIntent().getStringExtra("fullName");

        // Get the ID of the current logged-in user
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String currentUserId = currentUser != null ? currentUser.getUid() : null;

        // Compare currentUserId with senderId and receiverId to determine new senderId and receiverId
        if (currentUserId != null) {
            if (currentUserId.equals(receiverId)) {
                // If current user is the receiver, set senderId to current user's ID and receiverId to the other user's ID
                senderId = currentUserId;
                receiverId = getIntent().getStringExtra("senderId");
            }
        }

        fetchMessages();

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String messageContent = editTextEt.getText().toString().trim();
                if (!TextUtils.isEmpty(messageContent)) {
                    sendMessage(messageContent);
                } else {
                    Toast.makeText(ConversationActivity.this, "Please enter a message", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void fetchMessages() {
        DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("Chats").child(chatId).child("messages");

        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                    MessageModel message = messageSnapshot.getValue(MessageModel.class);
                    if (message != null) {
                        list.add(message);
                    }
                }
                textAdapters.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ConversationActivity.this, "Failed to load messages: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendMessage(String content) {
        DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("Chats").child(chatId).child("messages");
        String messageId = chatRef.push().getKey();
        String timeStamp = String.valueOf(System.currentTimeMillis());
        MessageModel message = new MessageModel(messageId, receiverId, chatId, senderId, content, timeStamp, fullName);

        if (messageId != null) {
            chatRef.child(messageId).setValue(message).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(ConversationActivity.this, "Message sent successfully", Toast.LENGTH_SHORT).show();
                    editTextEt.getText().clear(); // Clear the message field
                } else {
                    Toast.makeText(ConversationActivity.this, "Failed to send message", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}



