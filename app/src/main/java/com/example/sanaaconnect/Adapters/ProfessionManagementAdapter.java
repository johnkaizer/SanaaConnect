package com.example.sanaaconnect.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Spinner;
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
        String loggedInUserId;
        // Get the currently logged-in user's ID
        SharedPreferences preferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        loggedInUserId = preferences.getString("clientId", "");

        String proffId = professionModel.getProffId();
        String title = professionModel.getTitle();

        // Check if the profile belongs to the logged-in user
        if (proffId.equals(loggedInUserId)) {
            DatabaseReference skillsProfileRef = FirebaseDatabase.getInstance().getReference().child("SkillsProfile").child(proffId).child(title);
            skillsProfileRef.removeValue()
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



    private void editProff(ProfessionModel professionModel) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.edit_proffesion, null);

        EditText fullName = dialogView.findViewById(R.id.fullName);
        EditText titleSpinner = dialogView.findViewById(R.id.profession1);
        EditText emailEd = dialogView.findViewById(R.id.email1);
        EditText charges = dialogView.findViewById(R.id.charges);
        Spinner experienceSpinner = dialogView.findViewById(R.id.experience_spinner);
        Spinner educationSpinner = dialogView.findViewById(R.id.education_spinner);
        EditText location = dialogView.findViewById(R.id.location);

        // Populate fields with existing profession data
        fullName.setText(professionModel.getFullName());
        charges.setText(professionModel.getCharges());
        location.setText(professionModel.getLocation());
        titleSpinner.setText(professionModel.getPhone());
        emailEd.setText(professionModel.getEmail());


        ArrayAdapter<CharSequence> experienceAdapter = ArrayAdapter.createFromResource(context,
                R.array.experience_levels, android.R.layout.simple_spinner_item);
        experienceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        experienceSpinner.setAdapter(experienceAdapter);

        ArrayAdapter<CharSequence> educationAdapter = ArrayAdapter.createFromResource(context,
                R.array.education_levels, android.R.layout.simple_spinner_item);
        educationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        educationSpinner.setAdapter(educationAdapter);

        String selectedExperience = professionModel.getExperience();
        if (selectedExperience != null) {
            int experiencePosition = experienceAdapter.getPosition(selectedExperience);
            if (experiencePosition != -1) {
                experienceSpinner.setSelection(experiencePosition);
            }
        }

        String selectedEducation = professionModel.getEducation();
        if (selectedEducation != null) {
            int educationPosition = educationAdapter.getPosition(selectedEducation);
            if (educationPosition != -1) {
                educationSpinner.setSelection(educationPosition);
            }
        }

        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        dialogView.findViewById(R.id.add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String FullName = fullName.getText().toString();
                String Title = professionModel.getTitle();
                String Charges = charges.getText().toString();
                String email = emailEd.getText().toString();
                String phone = titleSpinner.getText().toString();
                String Experience = experienceSpinner.getSelectedItem().toString();
                String Education = educationSpinner.getSelectedItem().toString();
                String Location = location.getText().toString();
                // Retrieve ImageUrl from the existing professionModel

                // Check if any of the fields are empty
                if (TextUtils.isEmpty(FullName) || TextUtils.isEmpty(Charges) || TextUtils.isEmpty(Location) ) {
                    Toast.makeText(context, "Please fill all the fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Retrieve clientId from SharedPreferences
                SharedPreferences preferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
                String clientId = preferences.getString("clientId", "");

                // Update the existing profession profile entry
                DatabaseReference professionProfileRef = FirebaseDatabase.getInstance().getReference()
                        .child("SkillsProfile")
                        .child(professionModel.getProffId())
                        .child(professionModel.getTitle()); // Add this line to specify the title node

                ProfessionModel updatedProfessionModel = new ProfessionModel(clientId, FullName, Title, Education,Charges, "", Experience, Location,email,phone);
                professionProfileRef.setValue(updatedProfessionModel)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Profession profile updated successfully
                                Toast.makeText(context, "Profession profile updated successfully", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Failed to update profession profile
                                Toast.makeText(context, "Failed to update profession profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

            }
        });

        dialogView.findViewById(R.id.cancelButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Dismiss the dialog
                dialog.dismiss();
            }
        });

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        dialog.show();
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
