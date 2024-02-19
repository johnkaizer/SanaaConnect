package com.example.sanaaconnect.constants;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Constants {
    // User database reference
    public static final DatabaseReference referenceUser = FirebaseDatabase.getInstance().getReference("Users");

    // Get signed-in user uid
    public static String getUserUid() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        return (currentUser != null) ? currentUser.getUid() : null;
    }
}

