package com.example.sanaaconnect.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sanaaconnect.R;
import com.example.sanaaconnect.models.JobModel;
import com.example.sanaaconnect.models.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {
    List<Users> usersList;
    Context context;

    public UsersAdapter(List<Users> usersList, Context context) {
        this.usersList = usersList;
        this.context = context;
    }

    @NonNull
    @Override
    public UsersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.users_item, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull UsersAdapter.ViewHolder holder, int position) {
        Users users = usersList.get(position);
        holder.userName.setText(users.getFullName());
        holder.pass.setText(users.getPassword());
        holder.phone.setText(users.getPhoneNumber());
        holder.email.setText(users.getEmail());
        holder.role.setText(users.getRole());

        // Get the user ID for the current user
        String userId = users.getUserId();

        holder.buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Call the method to delete the user's data
                deleteUser(userId);
            }
        });
    }

    // Method to delete a user's data from Firebase
    private void deleteUser(String userId) {
        // Reference to the Firebase Realtime Database
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();

        // Delete user data from Realtime Database
        databaseRef.child("Users").child(userId).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // User data deletion successful
                        // Now delete the user from Firebase Authentication
                        deleteFromAuthentication(userId);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // User data deletion failed
                        Toast.makeText(context, "Failed to delete user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Method to delete user from Firebase Authentication
    private void deleteFromAuthentication(String userId) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Check if the user ID matches the current user's ID before deleting
            if (user.getUid().equals(userId)) {
                // If it's the current user, show a message that they cannot delete themselves
                Toast.makeText(context, "You cannot delete yourself", Toast.LENGTH_SHORT).show();
                return;
            }

            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), user.getUid());
            user.reauthenticate(credential)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                user.delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                // User deletion from Firebase Authentication successful
                                                Toast.makeText(context, "User deleted successfully", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                // User deletion from Firebase Authentication failed
                                                Toast.makeText(context, "Failed to delete user from Authentication: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            } else {
                                // Re-authentication failed
                                Toast.makeText(context, "Re-authentication failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }



    @Override
    public int getItemCount() {
        return usersList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView userName;
        TextView pass;
        TextView phone;
        TextView role;
        TextView email;
        Button buttonDelete;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.username);
            pass = itemView.findViewById(R.id.password);
            phone = itemView.findViewById(R.id.phone);
            role = itemView.findViewById(R.id.business);
            email = itemView.findViewById(R.id.email);
            buttonDelete = itemView.findViewById(R.id.deleteBtn);
        }
    }
}
