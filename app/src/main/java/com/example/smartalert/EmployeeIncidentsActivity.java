package com.example.smartalert;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;
import static com.example.smartalert.R.*;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class EmployeeIncidentsActivity extends AppCompatActivity {
    AutoCompleteTextView incidentType;
    String incident_type;
    Class<?> page;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_employee_incidents);

        incidentType = findViewById(id.autoCompleteTextView2);

        //bottomNavigationView = findViewById(id.bottomNavigationView);
        //BottomNavigationUtils.setupBottomNavigation(bottomNavigationView, this);

    }


    public void examineIncidents(View view) {
        incident_type = incidentType.getText().toString();
        boolean flag = false;

        switch (incident_type) {
            case "Fire":
                page = EmployeeAllFireIncidentsActivity.class;
                flag = true;
                break;
            case "Flood":
                page = EmployeeAllFloodIncidentsActivity.class;
                flag = true;
                break;
            case "Earthquake":
                page = EmployeeAllEarthquakeIncidentsActivity.class;
                flag = true;
                break;
            default:
                // If none of the specified incident types are selected, show a toast message
                Toast.makeText(this, "Please select incident type", Toast.LENGTH_SHORT).show();
                return;
        }
        if (flag)
            Toast.makeText(this, "Incident type " + incident_type.toLowerCase() + " is selected", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, page);
        startActivity(intent);
    }









}