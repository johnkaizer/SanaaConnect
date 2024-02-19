package com.example.sanaaconnect.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sanaaconnect.R;
import com.example.sanaaconnect.profile.CreatorProfile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class CreatorLogin extends AppCompatActivity {

    private EditText editTextLoginEmail, editTextLoginPwd;
    private ProgressBar progressBar;
    private FirebaseAuth authProfile;
    private static final String TAG = "CreatorLogin";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creator_login);

        editTextLoginEmail = findViewById(R.id.editText_login_email);
        editTextLoginPwd = findViewById(R.id.editText_login_pwd);
        progressBar = findViewById(R.id.progressBar);

        authProfile = FirebaseAuth.getInstance();

        //reset password
        TextView textViewLinkResetPwd = findViewById(R.id.textView_forgot_password);
        textViewLinkResetPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(CreatorLogin.this, "You can reset your password now!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(CreatorLogin.this, ForgotPassword.class));
            }
        });

        //Register
        TextView textViewLinkRegister = findViewById(R.id.textView_register_link);
        textViewLinkRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(CreatorLogin.this, "You can reset your password now!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(CreatorLogin.this, RegisterCreator.class));
            }
        });




        //show hide password
        ImageView imageViewShowHidePwd = findViewById(R.id.imageView_show_hide_pwd);
        imageViewShowHidePwd.setImageResource(R.drawable.ic_show_pwd);
        imageViewShowHidePwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editTextLoginPwd.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                    //If password is visible then hide it
                    editTextLoginPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    //change icon
                    imageViewShowHidePwd.setImageResource(R.drawable.ic_show_pwd);
                } else {
                    editTextLoginPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    imageViewShowHidePwd.setImageResource(R.drawable.ic_hide_pwd);

                }
            }
        });

        //Login User
        Button buttonLogin = findViewById(R.id.button_login);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textEmail = editTextLoginEmail.getText().toString();
                String textPwd = editTextLoginPwd.getText().toString();

                if(TextUtils.isEmpty(textEmail)){
                    Toast.makeText(CreatorLogin.this, "Please enter your email", Toast.LENGTH_LONG).show();
                    editTextLoginEmail.setError("Email is required!");
                    editTextLoginEmail.requestFocus();

                } else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()) {
                    Toast.makeText(CreatorLogin.this, "Please re-enter your email", Toast.LENGTH_LONG).show();
                    editTextLoginEmail.setError("Valid email is required!");
                    editTextLoginEmail.requestFocus();

                } else if (TextUtils.isEmpty(textPwd)){
                    Toast.makeText(CreatorLogin.this, "Please enter your password", Toast.LENGTH_LONG).show();
                    editTextLoginPwd.setError("Password is required!");
                    editTextLoginPwd.requestFocus();
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    loginUser(textEmail, textPwd);
                }
            }
        });
    }

    private void loginUser(String email, String pwd) {
        authProfile.signInWithEmailAndPassword(email, pwd).addOnCompleteListener(CreatorLogin.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){

                    //get instance of current user
                    FirebaseUser firebaseUser = authProfile.getCurrentUser();

                    //check if email is verified before user can access their profile
                    if (firebaseUser.isEmailVerified()){
                        Toast.makeText(CreatorLogin.this, "You are logged in", Toast.LENGTH_SHORT).show();

                        //open user profile
                        //Start the Creator Profile Activity
                        startActivity(new Intent( CreatorLogin.this, CreatorProfile.class));
                        finish();

                    } else {
                        firebaseUser.sendEmailVerification();
                        authProfile.signOut(); //sign out user
                        showAlertDialog();
                    }

                } else {
                    try{
                        throw task.getException();
                    } catch(FirebaseAuthInvalidUserException e) {
                        editTextLoginEmail.setError("User does not exist or is no longer valid. Please register again");
                        editTextLoginEmail.requestFocus();
                    } catch (FirebaseAuthInvalidCredentialsException e){
                        editTextLoginEmail.setError("Invalid credentials. Kindly, check and re-enter.");
                        editTextLoginEmail.requestFocus();
                    } catch (Exception e){
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(CreatorLogin.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void showAlertDialog() {
        //setup alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(CreatorLogin.this);
        builder.setTitle("Email Not Verified");
        builder.setMessage("Please verify your email now. You can not login without email verification.");

        //open email app if user clicks/taps "continue"
        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //TO EMAIL APP ON A NEW WINDOW
                startActivity(intent);
            }
        });

        //create alert box
        AlertDialog alertDialog = builder.create();

        //show alert dialog
        alertDialog.show();
    }
// check if user is already logged in. It takes them directly to the creator profile in such cases
    @Override
    protected void onStart() {
        super.onStart();
        if (authProfile.getCurrentUser() != null){
            Toast.makeText(this, "Already Looged In!", Toast.LENGTH_SHORT).show();

            //Start the Creator Profile Activity
            startActivity(new Intent( CreatorLogin.this, CreatorProfile.class));
            finish();
        }
        else{
            Toast.makeText(this, "You can login now!", Toast.LENGTH_SHORT).show();

        }
    }
}