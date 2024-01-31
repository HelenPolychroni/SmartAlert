package com.example.smartalert;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.smartalert.R.menu.*;
//import android.view.MenuItem;
import com.example.smartalert.R;






public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    View view;
    private static final int MENU_HOME = R.id.navigation_home;
    private static final int MENU_FIRES = R.id.navigation_fires;
    private static final int MENU_FLOODS = R.id.navigation_floods;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == MENU_HOME) {
                System.out.println("Home");
                startActivity(new Intent(MainActivity.this, EmployeeAllFireIncidentsActivity.class));
                return true;
            }
                /*case R.id.navigation_fires:
                    startActivity(new Intent(MainActivity.this, EmployeeAllFireIncidentsActivity.class));
                    return true;
                case R.id.navigation_floods:
                    startActivity(new Intent(MainActivity.this, EmployeeAllFloodIncidentsActivity.class));
                    return true;
            }
            */
            // }
            return false;
        });
    }
}