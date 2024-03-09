package com.example.sanaaconnect.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sanaaconnect.Adapters.MessageAdapters;
import com.example.sanaaconnect.R;
import com.example.sanaaconnect.databinding.FragmentMessagesBinding;
import com.example.sanaaconnect.models.MessageModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MessagesFragment extends Fragment {

    private FragmentMessagesBinding binding;
    private RecyclerView recyclerViewM;
    private MessageAdapters messageAdapters;
    private List<MessageModel> list = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentMessagesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recyclerViewM = root.findViewById(R.id.messageRV);
        recyclerViewM.setLayoutManager(new LinearLayoutManager(getContext()));

        fetchChats();

        return root;
    }

    private void fetchChats() {
        String userUid = getUserUid(); // Retrieve the logged-in user's UID
        DatabaseReference chatsRef = FirebaseDatabase.getInstance().getReference("Chats");

        chatsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Assume each child here is a chat node
                    // Now, check if the current user is a participant in this chat
                    boolean isUserAParticipant = false;
                    for (DataSnapshot messageSnapshot : snapshot.child("messages").getChildren()) {
                        MessageModel message = messageSnapshot.getValue(MessageModel.class);
                        if (message != null && (message.getSenderId().equals(userUid) || message.getRecieverId().equals(userUid))) {
                            isUserAParticipant = true;
                            break; // Found that the user is a participant, no need to check more messages
                        }
                    }

                    // If the user is a participant, add the first message of this chat to the list
                    if (isUserAParticipant) {
                        DataSnapshot firstMessageSnapshot = snapshot.child("messages").getChildren().iterator().next();
                        MessageModel firstMessage = firstMessageSnapshot.getValue(MessageModel.class);
                        if (firstMessage != null) {
                            list.add(firstMessage);
                        }
                    }
                }
                updateUI();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to load chats: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void updateUI() {
        if (messageAdapters == null) {
            messageAdapters = new MessageAdapters(getContext(), list);
            recyclerViewM.setAdapter(messageAdapters);
        } else {
            messageAdapters.notifyDataSetChanged();
        }
    }

    private String getUserUid() {
        // Implement this to retrieve the current user's UID
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
}
