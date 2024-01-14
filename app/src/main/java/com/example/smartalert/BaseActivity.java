package com.example.smartalert;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;


import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BaseActivity extends AppCompatActivity {

    private static final int MENU_HOME = R.id.action_home;
    private static final int MENU_SEND_INCIDENT = R.id.action_new_incident;
    private static final int MENU_STATISTICS = R.id.action_statistics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        setupBottomNavigationView();

        // Load the default fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, new HomeFragment())
                    .commit();
        }
    }
    protected void setupBottomNavigationView() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = new StatisticsFragment();

            int itemId = item.getItemId(); // Store the item ID in a variable

            // Use if-else statements instead of a switch for non-constant expressions
            if (itemId == MENU_HOME) {
                System.out.println("home");
                selectedFragment = new HomeFragment();
            }
            else if (itemId == MENU_SEND_INCIDENT) {
                System.out.println("incident");
                selectedFragment = new StatisticsFragment();
            }
            else if (itemId == MENU_STATISTICS) {
                System.out.println("statistics");
                selectedFragment = new StatisticsFragment();
            }

                /*case MENU_SEND_INCIDENT:
                    selectedFragment = new NewIncidentFragment();
                    break;*/


            if (selectedFragment != null) {
                System.out.println("not null");
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, selectedFragment)
                        .commit();
            }

            return true;
        });
    }
}