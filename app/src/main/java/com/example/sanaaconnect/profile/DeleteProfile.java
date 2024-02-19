package com.example.sanaaconnect.profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sanaaconnect.auth.ChangePassword;
import com.example.sanaaconnect.activities.MainActivity;
import com.example.sanaaconnect.R;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class DeleteProfile extends AppCompatActivity {

    private FirebaseAuth authProfile;
    private FirebaseUser firebaseUser;
    private EditText editTextUserPwd;
    private TextView textViewAuthenticated;
    private ProgressBar progressBar;
    private String userPwd;
    private Button buttonReAuthenticate, buttonDeleteUser;
    private static final String TAG = "DeleteProfile";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_profile);

        getSupportActionBar().setTitle("Delete Your Profile");

        progressBar = findViewById(R.id.progressBar);
        editTextUserPwd = findViewById(R.id.editText_delete_user_pwd);
        textViewAuthenticated = findViewById(R.id.textView_delete_user_authenticated);
        buttonDeleteUser = findViewById(R.id.button_delete_user);
        buttonReAuthenticate = findViewById(R.id.button_delete_user_authenticate);

        //disable delete user button until user is authenticated
        buttonDeleteUser.setEnabled(false);

        authProfile = FirebaseAuth.getInstance();
        firebaseUser = authProfile.getCurrentUser();
        
        if (firebaseUser.equals("")){
            Toast.makeText(this, "Something went wrong!" + "User details are no available at the moment", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(DeleteProfile.this, CreatorProfile.class);
            startActivity(intent);
            finish();
        } else {
            reAuthenticateUser(firebaseUser);
        }
    }

    //reAuthenticate user before changing password
    private void reAuthenticateUser(FirebaseUser firebaseUser) {
        buttonReAuthenticate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userPwd = editTextUserPwd.getText().toString();

                if (TextUtils.isEmpty(userPwd)){
                    Toast.makeText(DeleteProfile.this, "Password is needed", Toast.LENGTH_SHORT).show();
                    editTextUserPwd.setError("Please enter your current password to authenticate");
                    editTextUserPwd.requestFocus();
                } else {
                    progressBar.setVisibility(View.VISIBLE);

                    //ReAuthenticate user now
                    AuthCredential credential = EmailAuthProvider.getCredential(firebaseUser.getEmail(), userPwd);

                    firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                progressBar.setVisibility(View.GONE);

                                //Disable editText for  password.
                                editTextUserPwd.setEnabled(false);

                                //Enable delete profile button
                                buttonReAuthenticate.setEnabled(false);
                                buttonDeleteUser.setEnabled(true);

                                //set textview to show user is authenticated
                                textViewAuthenticated.setText("You are authenticated / verified." + "You can change password now!");
                                Toast.makeText(DeleteProfile.this, "Password has been verified. You can delete your profile and related data now!", Toast.LENGTH_SHORT).show();

                                //Update color of change password button
                                buttonDeleteUser.setBackgroundTintList(ContextCompat.getColorStateList(DeleteProfile.this, R.color.Red));

                                buttonDeleteUser.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        showAlertDialog();
                                    }
                                });
                            } else {
                                try {
                                    throw task.getException();
                                } catch(Exception e){
                                    Toast.makeText(DeleteProfile.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }
            }
        });

    }

    private void showAlertDialog() {
        //setup alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(DeleteProfile.this);
        builder.setTitle("Delete Profile and Related Data?");
        builder.setMessage("Do you really want to delete your profile and related data? This action is irreversible!");

        //open email app if user clicks/taps "continue"
        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteUser(firebaseUser);
            }
        });

        //Return to user profile Activity if user presses cancel button
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(DeleteProfile.this, CreatorProfile.class);
                startActivity(intent);
                finish();
            }
        });


        //create alert box
        AlertDialog alertDialog = builder.create();

        //Change color of the continue button
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.Red));
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorAccent2));
            }
        });

        //show alert dialog
        alertDialog.show();
    }

    private void deleteUser(FirebaseUser firebaseUser) {
        firebaseUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    deleteUserData();
                    authProfile.signOut();
                    Toast.makeText(DeleteProfile.this, "User has been deleted", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(DeleteProfile.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    try {
                        throw task.getException();
                    } catch(Exception e){
                        Toast.makeText(DeleteProfile.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    //delete all the data of the user
    private void deleteUserData() {
        //delete dislpay pic
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageReference = firebaseStorage.getReferenceFromUrl(firebaseUser.getPhotoUrl().toString());
        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d(TAG, "OnSuccess: Photo Deleted");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, e.getMessage());
                Toast.makeText(DeleteProfile.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        //Delete Dta from Realtime database
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Registered Users");
        databaseReference.child(firebaseUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d(TAG, "OnSuccess: User Data Deleted");

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, e.getMessage());
                Toast.makeText(DeleteProfile.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Creating Actionbar menu

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate menu items
        getMenuInflater().inflate(R.menu.common_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //when any menu item is selected
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home){
            NavUtils.navigateUpFromSameTask(DeleteProfile.this);

        } else if (id == R.id.menu_refresh){
            //refresh activity
            startActivity(getIntent());
            finish();
            overridePendingTransition(0,0);

        } else if (id == R.id.menu_update_profile) {
            Intent intent = new Intent(DeleteProfile.this, UpdateProfile.class);
            startActivity(intent);
            finish();

        }else if (id == R.id.menu_update_email) {
            Intent intent = new Intent(DeleteProfile.this, UpdateEmail.class);
            startActivity(intent);
        } else if (id == R.id.menu_change_password) {
            Intent intent = new Intent(DeleteProfile.this, ChangePassword.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.menu_delete_profile) {
            Intent intent = new Intent(DeleteProfile.this, DeleteProfile.class);
            startActivity(intent);
            finish();
        }else {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();

        }

        return super.onOptionsItemSelected(item);
    }
}