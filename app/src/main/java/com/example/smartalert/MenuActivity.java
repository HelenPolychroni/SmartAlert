package com.example.smartalert;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MenuActivity extends AppCompatActivity {

    private static final int MENU_HOME = R.id.action_home;
    private static final int MENU_SEND_INCIDENT = R.id.action_new_incident;
    private static final int MENU_STATISTICS = R.id.action_statistics;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        setupBottomNavigationView();
    }

    protected void setupBottomNavigationView() {
        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;

                Intent intent = null;

                int itemId = item.getItemId(); // Store the item ID in a variable

                // Use if-else statements instead of a switch for non-constant expressions
                if (itemId == MENU_HOME) {
                    // navigate to home
                    if (!isCurrentActivity(UserHomePage.class)) {
                        intent = new Intent(MenuActivity.this, UserHomePage.class);
                    }
                    //startActivity(intent);
                    //return true;
                } else if (itemId == MENU_SEND_INCIDENT) {
                    if (!isCurrentActivity(UserNewIncident.class)) {
                        intent = new Intent(MenuActivity.this, UserNewIncident.class);
                    }
                    //startActivity(intent);
                    //return true;
                } else if (itemId == MENU_STATISTICS) {
                    if (!isCurrentActivity(UserStatistics.class)) {
                        intent = new Intent(MenuActivity.this, UserStatistics.class);
                    }
                    //return true;
                }

                // Start the intent if it is not null
                if (intent != null) {
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });
    }

    // Helper method to check if the current activity is of a certain class
    private boolean isCurrentActivity(Class<?> activityClass) {
        return getClass().equals(activityClass);
    }

}