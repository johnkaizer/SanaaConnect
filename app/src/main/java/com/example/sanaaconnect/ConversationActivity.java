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
        DatabaseReference chatsRef = FirebaseDatabase.getInstance().getReference("Chats");

        if (chatId != null) {
            // If chatId is already available, send the message to the existing chat
            DatabaseReference chatRef = chatsRef.child(chatId).child("messages");
            String messageId = chatRef.push().getKey();

            String timeStamp = MessageModel.getCurrentTimeStamp();
            String userName = "Unknown"; // You can update this with the actual username

            MessageModel message = new MessageModel(messageId, chatId, receiverId, senderId, content, timeStamp, userName);
            if (messageId != null) {
                chatRef.child(messageId).setValue(message)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(ConversationActivity.this, "Message sent successfully", Toast.LENGTH_SHORT).show();
                                editTextEt.getText().clear();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ConversationActivity.this, "Failed to send message: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        } else {
            // Query for existing conversation between senderId and receiverId
            Query query = chatsRef.orderByChild("senderId_receiverId").equalTo(senderId + "_" + receiverId);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Retrieve the chatId and send the message to the existing chat
                        for (DataSnapshot chatSnapshot : dataSnapshot.getChildren()) {
                            chatId = chatSnapshot.getKey();
                            sendMessage(content);
                            return;
                        }
                    } else {
                        // Create a new chat and send the message
                        String newChatId = chatsRef.push().getKey();
                        if (newChatId != null) {
                            chatId = newChatId;
                            sendMessage(content);
                        } else {
                            Toast.makeText(ConversationActivity.this, "Error creating new chat", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(ConversationActivity.this, "Failed to send message: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }



}


