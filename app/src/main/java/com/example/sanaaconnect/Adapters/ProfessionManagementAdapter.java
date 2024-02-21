package com.example.sanaaconnect.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sanaaconnect.R;
import com.example.sanaaconnect.models.ProfessionModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ProfessionManagementAdapter extends RecyclerView.Adapter<ProfessionManagementAdapter.ViewHolder> {
    List<ProfessionModel> professionList;
    Context context;

    public ProfessionManagementAdapter(List<ProfessionModel> professionList, Context context) {
        this.professionList = professionList;
        this.context = context;
    }

    public void filterList(List<ProfessionModel> filteredList) {
        professionList = filteredList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProfessionManagementAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.profession_item, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull ProfessionManagementAdapter.ViewHolder holder, int position) {
        ProfessionModel professionModel = professionList.get(position);
        // Set the values to TextViews
        holder.nameTxt.setText("Name:" + professionModel.getFullName());
        holder.proffesionTxt.setText("Description: " + professionModel.getTitle());
        holder.chargesTxt.setText("Ksh:" + professionModel.getCharges());
        holder.phoneTxt.setText("Phone: " + professionModel.getPhone());
        String imageUrl = professionModel.getImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Picasso.get()
                    .load(imageUrl)
                    .into(holder.imageView);
        } else {
            // If imageUrl is empty or null, set a default placeholder
            holder.imageView.setImageResource(R.drawable.ic_logo);
        }
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(context, view);
                popupMenu.inflate(R.menu.popup_menu_management);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.menu_edit) {
                            // Implement edit action here
                            editProff(professionModel);
                            return true;
                        } else if (item.getItemId() == R.id.menu_delete) {
                            // Implement delete action here
                            deleteProff(professionModel);
                            return true;
                        } else {
                            return false;
                        }
                    }
                });
                popupMenu.show();
            }
        });


    }

    private void deleteProff(ProfessionModel professionModel) {
        String clientId;
        // Get the currently logged-in user's ID
        SharedPreferences preferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        clientId = preferences.getString("clientId", "");
        String loggedInUserId = clientId;

        // Retrieve the title of the profession
        String title = professionModel.getTitle();

        // Construct the DatabaseReference to fetch the profile using the title
        DatabaseReference skillsProfileRef = FirebaseDatabase.getInstance().getReference().child("SkillsProfile");
        Query query = skillsProfileRef.orderByChild("title").equalTo(title);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    // Retrieve the profession model
                    ProfessionModel profModel = dataSnapshot.getValue(ProfessionModel.class);
                    // Check if the profile belongs to the logged-in user
                    if (profModel != null && profModel.getProffId().equals(loggedInUserId)) {
                        // Delete the profile
                        dataSnapshot.getRef().removeValue()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // Profile deleted successfully
                                        Toast.makeText(context, "Profile deleted successfully", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Failed to delete the profile
                                        Toast.makeText(context, "Failed to delete profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        // If the profile does not belong to the logged-in user, show a message
                        Toast.makeText(context, "You do not have permission to delete this profile", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "Failed to fetch profile: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void editProff(ProfessionModel professionModel) {
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
}
