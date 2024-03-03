package com.example.smartalert;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Locale;

public class EmployeeAllFloodIncidentsActivity extends EmployeeControlIncidentsActivity {

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch sortFloodIncidentsSwitch;
    private LinearLayout scrollViewLayout;
    private ScrollView scrollView;
    private DatabaseReference incidentsRef, sortedIncidentsRef, verifiedRef;
    private FirebaseDatabase database;
    private TextView Titletextview;
    private Button FloodsButton, CriteriaButton;
    //private TextView sortingmsg;
    boolean isEnglishSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve language preference from SharedPreferences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        isEnglishSelected = preferences.getBoolean("english", true); // Default value is true if key "english" is not found

        // Change language based on the preference
        String lang = isEnglishSelected ? "en" : "el"; // Change this to the language code you want to switch to
        updateLocale(lang);

        setContentView(R.layout.activity_employee_flood_incidents);

        database = FirebaseDatabase.getInstance();
        sortFloodIncidentsSwitch = findViewById(R.id.switch3);
        scrollViewLayout = findViewById(R.id.scrollViewLayout3);
        scrollView = findViewById(R.id.scrollview3);
        scrollView.setBackgroundColor(Color.TRANSPARENT);

        Titletextview = findViewById(R.id.TitletextView2);
        //sortingmsg = findViewById(R.id.sortingmsg3);
        FloodsButton = findViewById(R.id.buttonFloods);
        CriteriaButton = findViewById(R.id.button11);

        incidentsRef = FirebaseDatabase.getInstance().getReference().child("incidents");

        if (ThemeUtils.isDarkTheme(this)) { // Dark mode
            Titletextview.setTextColor(getResources().getColor(R.color.white));
            //sortingmsg.setTextColor(getResources().getColor(R.color.white));
            FloodsButton.setTextColor(getResources().getColor(R.color.white));
            CriteriaButton.setTextColor(getResources().getColor(R.color.white));
            sortFloodIncidentsSwitch.setTextColor(getResources().getColor(R.color.white));
        }

        // see pending incidents
        EmployeeControlIncidentsActivity.CreateIncidentsLayout(incidentsRef, "Flood", scrollViewLayout, EmployeeAllFloodIncidentsActivity.this);
    }

    private void updateLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }

    public void checkSwitch(View view) {  // Check the state of the Switch when the Button is clicked

        boolean isSwitchOn = sortFloodIncidentsSwitch.isChecked();

        incidentsRef = FirebaseDatabase.getInstance().getReference().child("incidents");
        sortedIncidentsRef = FirebaseDatabase.getInstance().getReference("SortedIncidents/Flood");
        verifiedRef = FirebaseDatabase.getInstance().getReference().child("Verified/Floods");

        // Perform actions based on the Switch state
        if (isSwitchOn) { // Switch is ON
            //sortedIncidentsRef.removeValue();
            if (isEnglishSelected)
                Titletextview.setText("Sort Flood\nIncidents");
            else
                Titletextview.setText("Ταξινόμιση Περιστατικών\nΠλυμμήρας");

            sortFloodIncidentsSwitch.setVisibility(View.VISIBLE);
            //sortingmsg.setVisibility(View.VISIBLE);
            CriteriaButton.setVisibility(View.VISIBLE);
            FloodsButton.setVisibility(View.VISIBLE);

            System.out.println("Switch (sort) is on!");

            if (isEnglishSelected)
                showToast("Sorting is ON");
            else
                showToast("Ταξινόμηση ενεργοποιημένη");

            EmployeeControlIncidentsActivity.findAndStoreIncidents(incidentsRef, sortedIncidentsRef, "Flood");
            EmployeeControlIncidentsActivity.CreateSortIncidentsLayout(sortedIncidentsRef, verifiedRef,
                    scrollViewLayout, EmployeeAllFloodIncidentsActivity.this);
        }
        else {  // Switch is OFF
            if (isEnglishSelected)
                Titletextview.setText("Pending Flood\nIncidents");
            else
                Titletextview.setText("Εκκρεμείς Περιστατικά Πλημμύρας");

            sortFloodIncidentsSwitch.setVisibility(View.VISIBLE);
            //sortingmsg.setVisibility(View.VISIBLE);
            CriteriaButton.setVisibility(View.VISIBLE);
            FloodsButton.setVisibility(View.VISIBLE);

            System.out.println("Switch (sort) is off!"); // so all fire incidents

            if (isEnglishSelected)
                showToast("Sorting is OFF");
            else
                showToast("Ταξινόμηση απανεργοποιημένη");


            EmployeeControlIncidentsActivity.CreateIncidentsLayout(incidentsRef, "Flood", scrollViewLayout, EmployeeAllFloodIncidentsActivity.this);
        }
    }

    public void seeVerifiedFloods(View view){

        if (isEnglishSelected)
            Titletextview.setText("\nVerified Flood\nIncidents");
        else {
            Titletextview.setTextSize(18);
            Titletextview.setText("\nΕπικαιροποιημένες Πλημμύρες");
        }

        sortFloodIncidentsSwitch.setVisibility(View.INVISIBLE);
        //sortingmsg.setVisibility(View.INVISIBLE);
        CriteriaButton.setVisibility(View.INVISIBLE);
        FloodsButton.setVisibility(View.INVISIBLE);

        verifiedRef = FirebaseDatabase.getInstance().getReference().child("Verified/Floods");

        EmployeeControlIncidentsActivity.seeVerifiedIncidents(verifiedRef,
                scrollViewLayout, EmployeeAllFloodIncidentsActivity.this);
    }

    public void showCriteria(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        String message;
        String closeText = "Close";

        if (isEnglishSelected) {
            message = "Examine flood incidents that happened the last 24 hours and located" +
                    " within a distance of 40 kilometers";
           closeText = "Close";
        }
        else {
            message = "Εξέτασε περιστατικά πλημμύρων που συνέβησαν τις τελευταίες 24 ώρες" +
                    " και βρίσκονται σε απόσταση 40 χιλιομέτρων";
            closeText = "Κλείσιμο";
        }

        builder.setMessage(message)
                .setNegativeButton(closeText, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Handle Close button click or dismiss the dialog
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
