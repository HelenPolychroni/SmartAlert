package com.example.smartalert;

import android.app.Activity;
import android.content.Intent;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BottomNavigationUtils {

    private static final int MENU_HOME = R.id.navigation_home;
    private static final int MENU_FIRES = R.id.navigation_fires;
    private static final int MENU_FLOODS = R.id.navigation_floods;
    private static final int MENU_EARTHQUAKES = R.id.navigation_earthquakes;


    public static void setupBottomNavigation(BottomNavigationView bottomNavigationView, Activity activity) {
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == MENU_HOME) {
                System.out.println("Home");
                activity.startActivity(new Intent(activity, EmployeeHomePage.class));
                return true;
            }
            else if (item.getItemId() == MENU_FIRES) {
                System.out.println("FIRES");
                activity.startActivity(new Intent(activity, EmployeeAllFireIncidentsActivity.class));
                return true;
            }
            else if (item.getItemId() == MENU_FLOODS) {
                System.out.println("FLOODS");
                activity.startActivity(new Intent(activity, EmployeeAllFloodIncidentsActivity.class));
                return true;
            }
            else if (item.getItemId() == MENU_EARTHQUAKES) {
                System.out.println("EARTHQUAKES");
                activity.startActivity(new Intent(activity, EmployeeAllEarthquakeIncidentsActivity.class));
                return true;
            }
            return false;
        });
    }
}
