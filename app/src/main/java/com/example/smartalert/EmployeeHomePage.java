package com.example.smartalert;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

public class EmployeeHomePage extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_home_page);

        mAuth = FirebaseAuth.getInstance();
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