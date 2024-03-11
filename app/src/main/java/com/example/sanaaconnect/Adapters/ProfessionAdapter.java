package com.example.sanaaconnect.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sanaaconnect.R;
import com.example.sanaaconnect.activities.ProfileDetailsActivity;
import com.example.sanaaconnect.models.JobModel;
import com.example.sanaaconnect.models.ProfessionModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ProfessionAdapter extends RecyclerView.Adapter<ProfessionAdapter.ViewHolder> {
    List<ProfessionModel> professionList;
    Context context;

    public ProfessionAdapter(List<ProfessionModel> professionList, Context context) {
        this.professionList = professionList;
        this.context = context;
    }

    public void filterList(List<ProfessionModel> filteredList) {
        professionList = filteredList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProfessionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.profession_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ProfessionAdapter.ViewHolder holder, int position) {
        ProfessionModel professionModel = professionList.get(position);
        holder.nameTxt.setText("Name:" + professionModel.getFullName());
        holder.proffesionTxt.setText("Description: " + professionModel.getTitle());
        holder.chargesTxt.setText("Ksh:" + professionModel.getCharges());
        holder.phoneTxt.setText("Phone: " + professionModel.getPhone());
        String imageUrl = professionModel.getImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Picasso.get().load(imageUrl).into(holder.imageView);
        } else {
            holder.imageView.setImageResource(R.drawable.ic_logo);
        }

        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                // Check if the current user is an admin
                isAdmin(new AdminCheckListener() {
                    @Override
                    public void onAdminChecked(boolean isAdmin) {
                        if (isAdmin) {
                            showDeleteConfirmationDialog(professionModel);
                        } else {
                            // User is not an admin, handle accordingly (optional)
                            Toast.makeText(context, "Only admins can delete professionals", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                return true; // Consume the long click event
            }
        });

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Launch ProfileDetails activity
                Intent intent = new Intent(context, ProfileDetailsActivity.class);
                intent.putExtra("fullName", professionModel.getFullName());
                intent.putExtra("title", professionModel.getTitle());
                intent.putExtra("charges", professionModel.getCharges());
                intent.putExtra("imageUrl", professionModel.getImageUrl());
                intent.putExtra("email", professionModel.getEmail());
                intent.putExtra("education", professionModel.getEducation());
                intent.putExtra("location", professionModel.getLocation());
                intent.putExtra("experience", professionModel.getExperience());
                intent.putExtra("jobClientId", professionModel.getProffId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return professionList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTxt;
        TextView proffesionTxt;
        TextView chargesTxt;
        TextView phoneTxt;
        ImageView imageView;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTxt = itemView.findViewById(R.id.name_txt);
            proffesionTxt = itemView.findViewById(R.id.title_txt);
            chargesTxt = itemView.findViewById(R.id.charges_txt);
            phoneTxt = itemView.findViewById(R.id.phone_txt);
            imageView = itemView.findViewById(R.id.roundedImage);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }

    private void isAdmin(AdminCheckListener listener) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Users");
            usersRef.child(uid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    boolean isAdmin = false;
                    if (dataSnapshot.exists()) {
                        String role = dataSnapshot.child("role").getValue(String.class);
                        if (role != null && role.equals("Admin")) {
                            isAdmin = true;
                        }
                    }
                    listener.onAdminChecked(isAdmin);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle database error
                }
            });
        }
    }

    private void showDeleteConfirmationDialog(ProfessionModel professionModel) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Confirm Deletion");
        builder.setMessage("Are you sure you want to delete this professional?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteProfessional(professionModel.getProffId());
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void deleteProfessional(String proffId) {
        DatabaseReference professionalRef = FirebaseDatabase.getInstance().getReference("SkillsProfile").child(proffId);
        professionalRef.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(context, "Professional skill profile deleted successfully", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Failed to delete professional: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    interface AdminCheckListener {
        void onAdminChecked(boolean isAdmin);
    }
}

