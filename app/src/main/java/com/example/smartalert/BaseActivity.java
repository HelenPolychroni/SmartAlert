package com.example.smartalert;

import androidx.annotation.LayoutRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public abstract class BaseActivity extends AppCompatActivity {

    private static final int MENU_HOME = R.id.action_home;
    private static final int MENU_SEND_INCIDENT = R.id.action_new_incident;
    private static final int MENU_STATISTICS = R.id.action_statistics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        System.out.println("I am here");
        //setupBottomNavigationView();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            setupBottomNavigationView(item.getItemId());
            return true;
        });

        // Load the default fragment
       /* if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, new HomeFragment())
                    .commit();
        }*/
    }


    protected abstract void setupBottomNavigationView(int itemId);
    //{
        /*System.out.println("here again");

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            System.out.println("inside listener");
            //Fragment selectedFragment = new StatisticsFragment();
            Intent intent = null;

            int itemId = item.getItemId(); // Store the item ID in a variable

            // Use if-else statements instead of a switch for non-constant expressions
            if (itemId == MENU_HOME) {
                System.out.println("home");
                // Start UserHomePage activity
                intent = new Intent(BaseActivity.this, UserHomePage.class);
            } else if (itemId == MENU_SEND_INCIDENT) {
                System.out.println("incident");
                // Start UserNewIncident activity
                intent = new Intent(BaseActivity.this, UserNewIncident.class);
            } else if (itemId == MENU_STATISTICS) {
                System.out.println("statistics");
                // Start StatisticsActivity activity
                intent = new Intent(BaseActivity.this, LogInActivity.class);
            }else{
                System.out.println("nada");
            }

            // Check if intent is not null before starting it
            if (intent != null) {
                startActivity(intent);
                return true; // Return true to indicate the item has been handled
            }

            return false;
        });*/
    //}
}
