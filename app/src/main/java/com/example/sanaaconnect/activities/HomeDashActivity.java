package com.example.sanaaconnect.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Menu;
import android.widget.Toast;

import com.example.sanaaconnect.R;
import com.example.sanaaconnect.constants.Constants;
import com.example.sanaaconnect.models.Users;
import com.example.sanaaconnect.profile.CreatorProfile;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sanaaconnect.databinding.ActivityHomeDashBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeDashActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityHomeDashBinding binding;
    private FirebaseAuth authProfile;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    String userIdentity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityHomeDashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        editor = preferences.edit();
        userIdentity = Constants.getUserUid();

        setSupportActionBar(binding.appBarHomeDash.toolbar);
        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser= authProfile.getCurrentUser();
        if (firebaseUser == null){
            Toast.makeText(this, "Something Went Wrong! User's details not available at the moment", Toast.LENGTH_LONG).show();

        }

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_messages)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home_dash);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }
    private void getUserRole() {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(userIdentity);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Users user = snapshot.getValue(Users.class);
                    String userRole = user.getRole();

                    // Now you have the user's role
                    // You can dynamically modify the menu based on this role
                    modifyMenuBasedOnRole(userRole);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }

    private void modifyMenuBasedOnRole(String userRole) {
        NavigationView navigationView = binding.navView;
        Menu navMenu = navigationView.getMenu();

        // Hide all menu items
        for (int i = 0; i < navMenu.size(); i++) {
            navMenu.getItem(i).setVisible(false);
        }

        // Show common menu items for all users
        navMenu.findItem(R.id.nav_home).setVisible(true);
        navMenu.findItem(R.id.nav_messages).setVisible(true);

        // Show additional menu items based on the user's role
        if ("Admin".equals(userRole)) {
            navMenu.findItem(R.id.nav_users_management).setVisible(true);
        }else if("Client".equals(userRole)){
            navMenu.findItem(R.id.nav_jobs).setVisible(true);
        }else if("Professional".equals(userRole)){
            navMenu.findItem(R.id.nav_skill_profile).setVisible(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_dash, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.account){
            Intent intent = new Intent(HomeDashActivity.this, CreatorProfile.class);
            startActivity(intent);

        } else if (id == R.id.action_logout){
            editor.clear();
            editor.commit();
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(this, "Logged Out", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(HomeDashActivity.this, MainActivity.class);

            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home_dash);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
    @Override
    protected void onResume() {
        super.onResume();
        getUserRole();
    }
}