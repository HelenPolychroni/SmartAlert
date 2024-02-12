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

public class EmployeeAllFloodIncidentsActivity extends EmployeeControlIncidentsActivity {

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch sortFloodIncidentsSwitch;
    private LinearLayout scrollViewLayout;
    private ScrollView scrollView;
    private DatabaseReference incidentsRef, sortedIncidentsRef, verifiedRef;
    private FirebaseDatabase database;
    private TextView Titletextview;
    private Button FloodsButton;
    private TextView sortingmsg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_flood_incidents);

        database = FirebaseDatabase.getInstance();
        sortFloodIncidentsSwitch = findViewById(R.id.switch3);
        scrollViewLayout = findViewById(R.id.scrollViewLayout3);
        scrollView = findViewById(R.id.scrollview3);
        scrollView.setBackgroundColor(Color.TRANSPARENT);

        Titletextview = findViewById(R.id.TitletextView2);
        sortingmsg = findViewById(R.id.sortingmsg3);
        FloodsButton = findViewById(R.id.buttonFloods);

        incidentsRef = FirebaseDatabase.getInstance().getReference().child("incidents");

        // see pending incidents
        EmployeeControlIncidentsActivity.CreateIncidentsLayout(incidentsRef, "Flood", scrollViewLayout, EmployeeAllFloodIncidentsActivity.this);
    }

    public void checkSwitch(View view) {  // Check the state of the Switch when the Button is clicked

        boolean isSwitchOn = sortFloodIncidentsSwitch.isChecked();

        incidentsRef = FirebaseDatabase.getInstance().getReference().child("incidents");
        sortedIncidentsRef = FirebaseDatabase.getInstance().getReference("SortedIncidents/Flood");
        verifiedRef = FirebaseDatabase.getInstance().getReference().child("Verified/Floods");

        // Perform actions based on the Switch state
        if (isSwitchOn) { // Switch is ON
            //sortedIncidentsRef.removeValue();
            Titletextview.setText("Sort Flood\nIncidents");
            sortFloodIncidentsSwitch.setVisibility(View.VISIBLE);
            sortingmsg.setVisibility(View.VISIBLE);
            FloodsButton.setVisibility(View.VISIBLE);

            System.out.println("Switch (sort) is on!");
            showToast("Sorting is ON");

            EmployeeControlIncidentsActivity.findAndStoreIncidents(incidentsRef, sortedIncidentsRef, "Flood");
            EmployeeControlIncidentsActivity.CreateSortIncidentsLayout(sortedIncidentsRef, verifiedRef,
                    scrollViewLayout, EmployeeAllFloodIncidentsActivity.this);
        }
        else {  // Switch is OFF
            Titletextview.setText("Pending Flood\nIncidents");
            sortFloodIncidentsSwitch.setVisibility(View.VISIBLE);
            sortingmsg.setVisibility(View.VISIBLE);
            FloodsButton.setVisibility(View.VISIBLE);

            System.out.println("Switch (sort) is off!"); // so all fire incidents
            showToast("Sorting is OFF");


            EmployeeControlIncidentsActivity.CreateIncidentsLayout(incidentsRef, "Flood", scrollViewLayout, EmployeeAllFloodIncidentsActivity.this);
        }
    }

    public void seeVerifiedFloods(View view){
        Titletextview.setText("Verified Flood\nIncidents");
        sortFloodIncidentsSwitch.setVisibility(View.INVISIBLE);
        sortingmsg.setVisibility(View.INVISIBLE);
        FloodsButton.setVisibility(View.INVISIBLE);

        verifiedRef = FirebaseDatabase.getInstance().getReference().child("Verified/Floods");

        EmployeeControlIncidentsActivity.seeVerifiedIncidents(verifiedRef,
                scrollViewLayout, EmployeeAllFloodIncidentsActivity.this);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}