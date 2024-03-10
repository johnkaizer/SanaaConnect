package com.example.sanaaconnect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sanaaconnect.Adapters.TextAdapters;
import com.example.sanaaconnect.models.MessageModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

    private String senderId, receiverId, chatId;
    private EditText editTextEt;
    private Button sendBtn;
    private DatabaseReference chatsRef = FirebaseDatabase.getInstance().getReference("Chats");

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
        DatabaseReference messagesRef = FirebaseDatabase.getInstance().getReference("Chats");

        messagesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                for (DataSnapshot chatSnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot messageSnapshot : chatSnapshot.child("messages").getChildren()) {
                        MessageModel message = messageSnapshot.getValue(MessageModel.class);
                        if (message != null &&
                                ((message.getSenderId().equals(senderId) && message.getRecieverId().equals(receiverId)) ||
                                        (message.getSenderId().equals(receiverId) && message.getRecieverId().equals(senderId)))) {
                            list.add(message);
                        }
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
        DatabaseReference messagesRef = FirebaseDatabase.getInstance().getReference("Chats");

        // Check if there is an existing chat for the sender and receiver
        String membersKey = senderId.compareTo(receiverId) > 0 ? senderId + "_" + receiverId : receiverId + "_" + senderId;
        Query query = messagesRef.orderByChild("members").equalTo(membersKey);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot chatSnapshot : dataSnapshot.getChildren()) {
                    // Iterate over each chat and check if it matches the membersKey
                    String chatId = chatSnapshot.getKey();
                    if (chatSnapshot.child("messages").child(chatId).child("chatId").getValue(String.class).equals(chatId)) {
                        // Existing chat found, use this chatId to send the message
                        sendMessageToExistingChat(chatId, content);
                        return;
                    }
                }

                // If no existing chat found, create a new chat and send the message
                String chatId = messagesRef.push().getKey();
                if (chatId != null) {
                    // Set up initial chat details
                    messagesRef.child(chatId).child("members").setValue(membersKey);
                    sendMessageToExistingChat(chatId, content);
                } else {
                    // Handle error creating new chat
                    Toast.makeText(ConversationActivity.this, "Failed to create new chat", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle onCancelled event
                Toast.makeText(ConversationActivity.this, "Failed to check for existing chat: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendMessageToExistingChat(String chatId, String content) {
        DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("Chats").child(chatId).child("messages");
        String messageId = chatRef.push().getKey();
        String timeStamp = String.valueOf(System.currentTimeMillis());
        MessageModel message = new MessageModel(messageId, chatId, receiverId, senderId, content, timeStamp, "Unknown");

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


