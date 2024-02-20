package com.example.smartalert;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EmployeeAllEarthquakeIncidentsActivity extends EmployeeControlIncidentsActivity {

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch sortEarthquakeIncidentsSwitch;
    private LinearLayout scrollViewLayout;
    private ScrollView scrollView;
    private DatabaseReference incidentsRef, sortedIncidentsRef, verifiedRef;
    private FirebaseDatabase database;
    private TextView Titletextview, sortingmsg;
    private Button EarthquakesButton;

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
        sortingmsg = findViewById(R.id.sortingmsg2);
        EarthquakesButton = findViewById(R.id.buttonEarthquakes);

        incidentsRef = FirebaseDatabase.getInstance().getReference().child("incidents");

        if (ThemeUtils.isDarkTheme(this)) { // Dark mode
            Titletextview.setTextColor(getResources().getColor(R.color.white));
            sortingmsg.setTextColor(getResources().getColor(R.color.white));
            EarthquakesButton.setTextColor(getResources().getColor(R.color.white));
           sortEarthquakeIncidentsSwitch.setTextColor(getResources().getColor(R.color.white));
        }

        EmployeeControlIncidentsActivity.CreateIncidentsLayout(incidentsRef, "Earthquake", scrollViewLayout,
                EmployeeAllEarthquakeIncidentsActivity.this);
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
            sortEarthquakeIncidentsSwitch.setVisibility(View.VISIBLE);
            sortingmsg.setVisibility(View.VISIBLE);
            EarthquakesButton.setVisibility(View.VISIBLE);

            System.out.println("Switch (sort) is on!");
            showToast("Sorting is ON");

            EmployeeControlIncidentsActivity.findAndStoreIncidents(incidentsRef, sortedIncidentsRef, "Earthquake");
            EmployeeControlIncidentsActivity.CreateSortIncidentsLayout(sortedIncidentsRef, verifiedRef,
                    scrollViewLayout, EmployeeAllEarthquakeIncidentsActivity.this);
        }
        else { // Switch is OFF
            Titletextview.setText("Pending Earthquake\nIncidents");
            sortEarthquakeIncidentsSwitch.setVisibility(View.VISIBLE);
            sortingmsg.setVisibility(View.VISIBLE);
            EarthquakesButton.setVisibility(View.VISIBLE);

            System.out.println("Switch (sort) is off!"); // so all fire incidents
            showToast("Sorting is OFF");

            EmployeeControlIncidentsActivity.CreateIncidentsLayout(incidentsRef, "Earthquake", scrollViewLayout, EmployeeAllEarthquakeIncidentsActivity.this);
        }
    }

    public void seeVerifiedEarthquakes(View view){
        Titletextview.setText("Verified Earthquake\nIncidents");
        sortEarthquakeIncidentsSwitch.setVisibility(View.INVISIBLE);
        sortingmsg.setVisibility(View.INVISIBLE);
        EarthquakesButton.setVisibility(View.INVISIBLE);

        verifiedRef = FirebaseDatabase.getInstance().getReference().child("Verified/Earthquakes");

        EmployeeControlIncidentsActivity.seeVerifiedIncidents(verifiedRef,
                scrollViewLayout, EmployeeAllEarthquakeIncidentsActivity.this);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}