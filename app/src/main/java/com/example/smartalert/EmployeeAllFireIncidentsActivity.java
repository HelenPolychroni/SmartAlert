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

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EmployeeAllFireIncidentsActivity extends EmployeeControlIncidentsActivity {

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch sortFireIncidentsSwitch;
    private LinearLayout scrollViewLayout;
    private TextView Titletextview, sortingmsg;
    private ScrollView scrollView;
    private DatabaseReference incidentsRef, sortedIncidentsRef, verifiedRef;
    private FirebaseDatabase database;
    private Button FiresButton;
    private BottomNavigationView bottomNavigationView;
    @SuppressLint("MissingInflatedId")

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_all_fire_incidents);

        database = FirebaseDatabase.getInstance();

        sortFireIncidentsSwitch = findViewById(R.id.switch1);  // switch

        scrollViewLayout = findViewById(R.id.scrollViewLayout1);
        scrollView = findViewById(R.id.scrollview);
        scrollView.setBackgroundColor(Color.TRANSPARENT);

        Titletextview = findViewById(R.id.TitletextView);
        sortingmsg = findViewById(R.id.sortingmsg);
        FiresButton = findViewById(R.id.buttonFires);

        incidentsRef = FirebaseDatabase.getInstance().getReference().child("incidents");

        if (ThemeUtils.isDarkTheme(this)) { // Dark mode
           Titletextview.setTextColor(getResources().getColor(R.color.white));
           sortingmsg.setTextColor(getResources().getColor(R.color.white));
           FiresButton.setTextColor(getResources().getColor(R.color.white));
           sortFireIncidentsSwitch.setTextColor(getResources().getColor(R.color.white));
        }

        EmployeeControlIncidentsActivity.CreateIncidentsLayout(incidentsRef, "Fire", scrollViewLayout, EmployeeAllFireIncidentsActivity.this);
    }

    public void checkSwitch(View view){ // Check the state of the Switch when the switch is triggered

        boolean isSwitchOn = sortFireIncidentsSwitch.isChecked();

        incidentsRef = FirebaseDatabase.getInstance().getReference().child("incidents");
        sortedIncidentsRef = FirebaseDatabase.getInstance().getReference("SortedIncidents/Fire");
        verifiedRef = FirebaseDatabase.getInstance().getReference().child("Verified/Fires");

        //sortFireIncidentsSwitch.setVisibility(View.VISIBLE);

        // Perform actions based on the Switch state
        if (isSwitchOn) {  // sort on
            //sortedIncidentsRef.removeValue();
            Titletextview.setText("Sort Fire\nIncidents");
            sortFireIncidentsSwitch.setVisibility(View.VISIBLE);
            sortingmsg.setVisibility(View.VISIBLE);
            FiresButton.setVisibility(View.VISIBLE);

            System.out.println("Switch (sort) is on!");
            showToast("Sorting is ON");

            EmployeeControlIncidentsActivity.findAndStoreIncidents(incidentsRef, sortedIncidentsRef, "Fire");
            EmployeeControlIncidentsActivity.CreateSortIncidentsLayout(sortedIncidentsRef, verifiedRef,
                    scrollViewLayout, EmployeeAllFireIncidentsActivity.this);
        }
        else {
            System.out.println("Switch (sort) is off!"); // so all fire incidents
            Titletextview.setText("Pending Fire\nIncidents");
            sortFireIncidentsSwitch.setVisibility(View.VISIBLE);
            sortingmsg.setVisibility(View.VISIBLE);
            FiresButton.setVisibility(View.VISIBLE);
            showToast("Sorting is OFF");

            EmployeeControlIncidentsActivity.CreateIncidentsLayout(incidentsRef,"Fire", scrollViewLayout,
                    EmployeeAllFireIncidentsActivity.this);
        }
    }

    public void seeVerifiedFires(View view){
        Titletextview.setText("Verified Fire\nIncidents");
        sortFireIncidentsSwitch.setVisibility(View.INVISIBLE);
        sortingmsg.setVisibility(View.INVISIBLE);
        FiresButton.setVisibility(View.INVISIBLE);

        verifiedRef = FirebaseDatabase.getInstance().getReference().child("Verified/Fires");

        EmployeeControlIncidentsActivity.seeVerifiedIncidents(verifiedRef,
                scrollViewLayout, EmployeeAllFireIncidentsActivity.this);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}