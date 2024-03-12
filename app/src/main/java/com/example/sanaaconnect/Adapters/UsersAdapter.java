package com.example.sanaaconnect.Adapters;

import android.annotation.SuppressLint;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

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
    public void onBindViewHolder(@NonNull UsersAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Users users = usersList.get(position);
        holder.userName.setText(users.getFullName());
        holder.pass.setText(users.getPassword());
        holder.phone.setText(users.getPhoneNumber());
        holder.email.setText(users.getEmail());
        holder.role.setText(users.getRole());
        holder.buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the position of the item clicked
                int position = holder.getAdapterPosition();

                if (position != RecyclerView.NO_POSITION) {
                    // Get the user to be deleted
                    Users userToDelete = usersList.get(position);

                    // Call the method to delete the user
                    deleteUser(userToDelete.getUserId(), position);
                }
            }
        });

    }

    private void deleteUser(String userId, int position) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Users");

        // Query to find the user with the matching userId
        Query query = usersRef.orderByChild("userId").equalTo(userId);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Delete the user from Firebase
                    snapshot.getRef().removeValue()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // User deletion successful
                                    // Remove the item from the list and notify adapter
                                    usersList.remove(position);
                                    notifyItemRemoved(position);
                                    notifyItemRangeChanged(position, usersList.size());

                                    Toast.makeText(context, "User deleted successfully", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // User deletion failed
                                    Toast.makeText(context, "Failed to delete user: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
                Toast.makeText(context, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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
