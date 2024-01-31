package com.example.smartalert;

import static com.example.smartalert.R.*;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class EmployeeHomePage extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_employee_home_page);

        mAuth = FirebaseAuth.getInstance();

        //bottomNavigationView = findViewById(id.bottomNavigationView);
        //BottomNavigationUtils.setupBottomNavigation(bottomNavigationView, this);

    }

    public void logout(View view){
        mAuth.signOut();

        Intent intent = new Intent(this, StartUpActivity.class);
        startActivity(intent);
    }

    public void got2Incidents(View view){
        Intent intent = new Intent(this, EmployeeIncidentsActivity.class);
        startActivity(intent);
    }

    public void FireIncidents(View view){
        Intent intent = new Intent(this, EmployeeAllFireIncidentsActivity.class);
        startActivity(intent);
    }

    public void EarthquakeIncidents(View view){
        Intent intent = new Intent(this, EmployeeAllEarthquakeIncidentsActivity.class);
        startActivity(intent);
    }

    public void FloodIncidents(View view){
        Intent intent = new Intent(this, EmployeeAllFloodIncidentsActivity.class);
        startActivity(intent);
    }
}