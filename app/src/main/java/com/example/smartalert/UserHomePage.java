package com.example.smartalert;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class UserHomePage extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private static final int MENU_HOME = R.id.action_home;
    private static final int MENU_SEND_INCIDENT = R.id.action_new_incident;
    private static final int MENU_STATISTICS = R.id.action_statistics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home_page);

        mAuth = FirebaseAuth.getInstance();

        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                Fragment selectedFragment = null;

                int itemId = item.getItemId(); // Store the item ID in a variable

                // Use if-else statements instead of a switch for non-constant expressions
                if (itemId == MENU_HOME) {
                    return true;
                } else if (itemId == MENU_SEND_INCIDENT) {
                    return true;
                } else if (itemId == MENU_STATISTICS) {
                    return true;
                }
                return false;
            }
        });


    }




    public void logout(View view){
        mAuth.signOut();

        Intent intent = new Intent(this, StartUpActivity.class);
        startActivity(intent);
    }


}