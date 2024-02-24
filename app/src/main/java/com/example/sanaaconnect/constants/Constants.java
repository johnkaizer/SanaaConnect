package com.example.sanaaconnect.constants;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Constants {
    // User database reference
    public static final DatabaseReference referenceUser = FirebaseDatabase.getInstance().getReference("Users");

    // Get signed-in user uid
    public static String getUserUid() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        return (currentUser != null) ? currentUser.getUid() : null;
    }

    // Get signed-in user email
    public static String getUserEmail() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        return (currentUser != null) ? currentUser.getEmail() : null;
    }

    // Get signed-in user phone number
    public static String getUserPhone() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        return (currentUser != null) ? currentUser.getPhoneNumber() : null;
    }

    // Get signed-in user full name
    public static void getUserFullName(final String userId, final ValueEventListener listener) {
        if (userId != null) {
            Query query = referenceUser.child(userId).child("fullName");
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (listener != null) {
                        listener.onDataChange(dataSnapshot);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    if (listener != null) {
                        listener.onCancelled(databaseError);
                    }
                }
            });
        }
    }

}



