package com.example.smartalert;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class EmployeeIncidentsActivity extends AppCompatActivity {
    AutoCompleteTextView incidentType;
    String incident_type;
    Class<?> page;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_incidents);

        incidentType = findViewById(R.id.autoCompleteTextView2);
    }


    public void examineIncidents(View view) throws ClassNotFoundException {
        incident_type = incidentType.getText().toString();
        switch (incident_type) {
            case "Fire":
                page = Class.forName("com.example.smartalert.EmployeeAllFireIncidentsActivity");
                break;
            case "Flood":
                page = Class.forName("com.example.smartalert.EmployeeAllFloodIncidentsActivity");
                break;
            case "Earthquake":
                page = Class.forName("com.example.smartalert.EmployeeAllEarthquakeIncidentsActivity");
                break;
        }
        Intent intent = new Intent(this, page);
        startActivity(intent);
    }








}