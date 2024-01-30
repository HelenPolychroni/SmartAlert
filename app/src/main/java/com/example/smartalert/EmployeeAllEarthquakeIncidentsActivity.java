package com.example.smartalert;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class EmployeeAllEarthquakeIncidentsActivity extends EmployeeControlIncidentsActivity {

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch sortEarthquakeIncidentsSwitch;
    private LinearLayout scrollViewLayout;
    private ScrollView scrollView;
    private DatabaseReference incidentsRef, sortedIncidentsRef, verifiedRef;
    private FirebaseDatabase database;
    private TextView Titletextview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_earthquake_incidents);

        database = FirebaseDatabase.getInstance();
        sortEarthquakeIncidentsSwitch = findViewById(R.id.switch2);
        scrollViewLayout = findViewById(R.id.scrollViewLayout2);
        scrollView = findViewById(R.id.scrollview2);
        scrollView.setBackgroundColor(Color.TRANSPARENT);

        Titletextview = findViewById(R.id.TitletextView3);
        incidentsRef = FirebaseDatabase.getInstance().getReference().child("incidents");

        //CreateIncidentsLayout();
        EmployeeControlIncidentsActivity.CreateIncidentsLayout(incidentsRef, "Earthquake", scrollViewLayout, EmployeeAllEarthquakeIncidentsActivity.this);
    }

    public void checkSwitch(View view) {
        // Check the state of the Switch when the Button is clicked
        boolean isSwitchOn = sortEarthquakeIncidentsSwitch.isChecked();

        incidentsRef = FirebaseDatabase.getInstance().getReference().child("incidents");
        sortedIncidentsRef = FirebaseDatabase.getInstance().getReference("SortedIncidents/Earthquake");
        verifiedRef = FirebaseDatabase.getInstance().getReference().child("Verified/Earthquakes");

        // Perform actions based on the Switch state
        if (isSwitchOn) {
            //sortedIncidentsRef.removeValue();
            Titletextview.setText("Sort Earthquake\nIncidents");
            System.out.println("Switch (sort) is on!");

            //CreateSortIncidentsLayout();
            showToast("Sorting is ON");

            //SortEarthquakeIncidents();
            //findAndStoreIncidents();

            EmployeeControlIncidentsActivity.findAndStoreIncidents(incidentsRef, sortedIncidentsRef, "Earthquake");
            EmployeeControlIncidentsActivity.CreateSortIncidentsLayout(sortedIncidentsRef, verifiedRef,
                    scrollViewLayout, EmployeeAllEarthquakeIncidentsActivity.this);
        }
        else { // Switch is OFF
            Titletextview.setText("Pending Earthquake\nIncidents");
            System.out.println("Switch (sort) is off!"); // so all fire incidents
            showToast("Sorting is OFF");

            //CreateIncidentsLayout();
            EmployeeControlIncidentsActivity.CreateIncidentsLayout(incidentsRef, "Earthquake", scrollViewLayout, EmployeeAllEarthquakeIncidentsActivity.this);
        }
    }

    public void seeVerifiedEarthquakes(View view){
        Titletextview.setText("Verified Earthquake\nIncidents");
        EmployeeControlIncidentsActivity.seeVerifiedIncidents(verifiedRef,
                scrollViewLayout, EmployeeAllEarthquakeIncidentsActivity.this);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}