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

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Locale;

public class EmployeeAllFireIncidentsActivity extends EmployeeControlIncidentsActivity {

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch sortFireIncidentsSwitch;
    private LinearLayout scrollViewLayout;
    private TextView Titletextview/*, sortingmsg*/;
    private ScrollView scrollView;
    private DatabaseReference incidentsRef, sortedIncidentsRef, verifiedRef;
    private FirebaseDatabase database;
    private Button FiresButton, CriteriaButton;
    boolean isEnglishSelected;
    @SuppressLint("MissingInflatedId")

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve language preference from SharedPreferences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        isEnglishSelected = preferences.getBoolean("english", true); // Default value is true if key "english" is not found

        // Change language based on the preference
        String lang = isEnglishSelected ? "en" : "el"; // Change this to the language code you want to switch to
        updateLocale(lang);

        setContentView(R.layout.activity_employee_all_fire_incidents);

        database = FirebaseDatabase.getInstance();

        sortFireIncidentsSwitch = findViewById(R.id.switch1);  // switch

        scrollViewLayout = findViewById(R.id.scrollViewLayout1);
        scrollView = findViewById(R.id.scrollview);
        scrollView.setBackgroundColor(Color.TRANSPARENT);

        Titletextview = findViewById(R.id.TitletextView);
        //sortingmsg = findViewById(R.id.sortingmsg);
        FiresButton = findViewById(R.id.buttonFires);
        CriteriaButton = findViewById(R.id.button2);

        incidentsRef = FirebaseDatabase.getInstance().getReference().child("incidents");

        if (ThemeUtils.isDarkTheme(this)) { // Dark mode
           Titletextview.setTextColor(getResources().getColor(R.color.white));
           //sortingmsg.setTextColor(getResources().getColor(R.color.white));
            CriteriaButton.setTextColor(getResources().getColor(R.color.white));
           FiresButton.setTextColor(getResources().getColor(R.color.white));
           sortFireIncidentsSwitch.setTextColor(getResources().getColor(R.color.white));
        }

        EmployeeControlIncidentsActivity.CreateIncidentsLayout(incidentsRef, "Fire", scrollViewLayout, EmployeeAllFireIncidentsActivity.this);
    }

    private void updateLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
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
            if (isEnglishSelected)
                Titletextview.setText("Sort Fire\nIncidents");
            else
                Titletextview.setText("Ταξινόμιση Περιστατικών\nΦωτιάς");
            sortFireIncidentsSwitch.setVisibility(View.VISIBLE);
            //sortingmsg.setVisibility(View.VISIBLE);
            CriteriaButton.setVisibility(View.VISIBLE);
            FiresButton.setVisibility(View.VISIBLE);

            System.out.println("Switch (sort) is on!");
            if (isEnglishSelected)
                showToast("Sorting is ON");
            else
                showToast("Ταξινόμηση ενεργοποιημένη");

            EmployeeControlIncidentsActivity.findAndStoreIncidents(incidentsRef, sortedIncidentsRef, "Fire");
            EmployeeControlIncidentsActivity.CreateSortIncidentsLayout(sortedIncidentsRef, verifiedRef,
                    scrollViewLayout, EmployeeAllFireIncidentsActivity.this);
        }
        else {
            System.out.println("Switch (sort) is off!"); // so all fire incidents
            if (isEnglishSelected)
                Titletextview.setText("Pending Fire\nIncidents");
            else
                Titletextview.setText("Εκκρεμείς Περιστατικά\nΦωτιάς");
            sortFireIncidentsSwitch.setVisibility(View.VISIBLE);
            //sortingmsg.setVisibility(View.VISIBLE);
            CriteriaButton.setVisibility(View.VISIBLE);
            FiresButton.setVisibility(View.VISIBLE);

            if (isEnglishSelected)
                showToast("Sorting is OFF");
            else
                showToast("Ταξινόμηση απανεργοποιημένη");

            EmployeeControlIncidentsActivity.CreateIncidentsLayout(incidentsRef,"Fire", scrollViewLayout,
                    EmployeeAllFireIncidentsActivity.this);
        }
    }

    public void seeVerifiedFires(View view){
        if (isEnglishSelected)
            Titletextview.setText("\nVerified Fire\nIncidents");
        else {
            Titletextview.setTextSize(19);
            Titletextview.setText("\nΕπικαιροποιημένες Φωτιές");
        }
        sortFireIncidentsSwitch.setVisibility(View.INVISIBLE);
        //sortingmsg.setVisibility(View.INVISIBLE);
        CriteriaButton.setVisibility(View.INVISIBLE);
        FiresButton.setVisibility(View.INVISIBLE);

        verifiedRef = FirebaseDatabase.getInstance().getReference().child("Verified/Fires");

        EmployeeControlIncidentsActivity.seeVerifiedIncidents(verifiedRef,
                scrollViewLayout, EmployeeAllFireIncidentsActivity.this);
    }

    public void showCriteria(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        String message;
        String closeText = "Close";

        if (isEnglishSelected) {
           message = "Examine fire incidents that happened the last 24 hours and located" +
                    " within a distance of 30 kilometers";
            closeText = "Close";

        }
        else {
            message = "Εξέτασε περιστατικά φωτιών που συνέβησαν τις τελευταίες 24 ώρες" +
                    " και βρίσκονται σε απόσταση 30 χιλιομέτρων";
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