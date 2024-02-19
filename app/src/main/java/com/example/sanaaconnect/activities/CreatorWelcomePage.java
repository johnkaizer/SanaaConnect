package com.example.sanaaconnect.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.sanaaconnect.R;
import com.example.sanaaconnect.auth.Login;
import com.example.sanaaconnect.auth.Register;

public class CreatorWelcomePage extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creator_welcome_page);

        //Open Login Activity
        Button buttonLogin = findViewById(R.id.button_login);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreatorWelcomePage.this, Login.class);
                startActivity(intent);
            }
        });

        //Open Register Activity

        TextView textViewRegister = findViewById(R.id.textView_register_link);
        textViewRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreatorWelcomePage.this, Register.class);
                startActivity(intent);

            }
        });


    }
}