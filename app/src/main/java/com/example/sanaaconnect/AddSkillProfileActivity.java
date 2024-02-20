package com.example.sanaaconnect;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.sanaaconnect.constants.Constants;
import com.example.sanaaconnect.models.ProfessionModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class AddSkillProfileActivity extends AppCompatActivity {
    EditText fullNameEt;
    EditText chargesEt;
    EditText locationEt;
    Spinner proffSpinner;
    Spinner eduSpinner;
    Spinner experiemceSpinner;
    ImageView imageV;
    Button submitBtn;
    Button uploadBtn;
    public static final int REQUEST_CODE_IMAGE = 101;
    private ProgressDialog progressDialog;
    Uri imageUri;
    boolean isImageAdded = false;
    private String clientId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_skill_profile);

        fullNameEt = findViewById(R.id.fullName);
        chargesEt = findViewById(R.id.charges);
        locationEt = findViewById(R.id.location);
        eduSpinner = findViewById(R.id.education_spinner);
        experiemceSpinner = findViewById(R.id.experience_spinner);
        proffSpinner = findViewById(R.id.profession_spinner);
        imageV = findViewById(R.id.product_image);
        submitBtn = findViewById(R.id.submit_btn);
        uploadBtn = findViewById(R.id.car_btn);

        SharedPreferences preferences = this.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        clientId = preferences.getString("clientId", "");

        //Spinners
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.education_levels, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        eduSpinner.setAdapter(adapter);

        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this, R.array.experience_levels, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        experiemceSpinner.setAdapter(adapter1);

        ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(this, R.array.professional_titles, android.R.layout.simple_spinner_item);
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        proffSpinner.setAdapter(adapter3);

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, REQUEST_CODE_IMAGE);
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertData();
            }
        });
    }

    private void insertData() {
        // Check for internet connection
        if (!isInternetConnected()) {
            Toast.makeText(this, "No internet connection available. Please turn on data and try again.", Toast.LENGTH_SHORT).show();
            return;
        }

        String fullName = fullNameEt.getText().toString();
        String title = proffSpinner.getSelectedItem().toString().toLowerCase();
        String education = eduSpinner.getSelectedItem().toString();
        String charges = chargesEt.getText().toString();
        String experience = experiemceSpinner.getSelectedItem().toString();
        String location = locationEt.getText().toString();
        // Get email and phone of the logged-in user
        String email = Constants.getUserEmail();
        String phone = Constants.getUserPhone();

        // Check if any of the fields are empty
        if (TextUtils.isEmpty(fullName) || TextUtils.isEmpty(title) || TextUtils.isEmpty(education) || TextUtils.isEmpty(charges) ) {
            Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show a progress dialog while uploading data
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Saving skill profile...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        DatabaseReference skillsProfileRef = FirebaseDatabase.getInstance().getReference("SkillsProfile").child(clientId);

        // Create a ProfessionModel object
        ProfessionModel professionModel = new ProfessionModel();
        professionModel.setProffId(clientId);
        professionModel.setFullName(fullName);
        professionModel.setTitle(title);
        professionModel.setEducation(education);
        professionModel.setCharges(charges);
        professionModel.setExperience(experience);
        professionModel.setLocation(location);
        professionModel.setEmail(email);
        professionModel.setPhone(phone);

        // Save the ProfessionModel object to Firebase
        skillsProfileRef.child(title).setValue(professionModel)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            Toast.makeText(AddSkillProfileActivity.this, "Skill profile added successfully", Toast.LENGTH_SHORT).show();
                            // Clear fields or handle UI as needed
                            fullNameEt.setText("");
                            chargesEt.setText("");
                            locationEt.setText("");
                        } else {
                            Toast.makeText(AddSkillProfileActivity.this, "Failed to add skill profile", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private boolean isInternetConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    private void uploadImageToStorage(String productId, ImageUploadCallback callback) {
        if (isImageAdded) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                // You can adjust the compression quality based on your requirements
                bitmap.compress(Bitmap.CompressFormat.JPEG, 10, baos);
                byte[] data = baos.toByteArray();

                StorageReference storageRef = FirebaseStorage.getInstance().getReference()
                        .child("product_images")
                        .child(clientId)
                        .child(productId);

                UploadTask uploadTask = storageRef.putBytes(data);
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String imageUrl = uri.toString();
                                callback.onImageUploadSuccess(imageUrl);
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callback.onImageUploadFailure();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
                callback.onImageUploadFailure();
            }
        } else {
            // No image added
            callback.onImageUploadSuccess(null);
        }
    }

    interface ImageUploadCallback {
        void onImageUploadSuccess(String imageUrl);
        void onImageUploadFailure();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish(); // Finish the activity
    }
}

