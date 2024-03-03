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

public class EmployeeAllEarthquakeIncidentsActivity extends EmployeeControlIncidentsActivity {

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch sortEarthquakeIncidentsSwitch;
    private LinearLayout scrollViewLayout;
    private ScrollView scrollView;
    private DatabaseReference incidentsRef, sortedIncidentsRef, verifiedRef;
    private FirebaseDatabase database;
    private TextView Titletextview/*, sortingmsg*/;
    private Button EarthquakesButton, CriteriaButton;
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

        setContentView(R.layout.activity_employee_earthquake_incidents);

        database = FirebaseDatabase.getInstance();
        sortEarthquakeIncidentsSwitch = findViewById(R.id.switch2);
        scrollViewLayout = findViewById(R.id.scrollViewLayout2);
        scrollView = findViewById(R.id.scrollview2);
        scrollView.setBackgroundColor(Color.TRANSPARENT);

        Titletextview = findViewById(R.id.TitletextView3);
        //sortingmsg = findViewById(R.id.sortingmsg2);
        CriteriaButton = findViewById(R.id.button12);
        EarthquakesButton = findViewById(R.id.buttonEarthquakes);

        incidentsRef = FirebaseDatabase.getInstance().getReference().child("incidents");

        if (ThemeUtils.isDarkTheme(this)) { // Dark mode
            Titletextview.setTextColor(getResources().getColor(R.color.white));
            //sortingmsg.setTextColor(getResources().getColor(R.color.white));
            CriteriaButton.setTextColor(getResources().getColor(R.color.white));
            EarthquakesButton.setTextColor(getResources().getColor(R.color.white));
            sortEarthquakeIncidentsSwitch.setTextColor(getResources().getColor(R.color.white));
        }

        EmployeeControlIncidentsActivity.CreateIncidentsLayout(incidentsRef, "Earthquake", scrollViewLayout,
                EmployeeAllEarthquakeIncidentsActivity.this);
    }

    private void updateLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
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
            if (isEnglishSelected)
                Titletextview.setText("Sort Earthquake\nIncidents");
            else
                Titletextview.setText("Ταξινόμιση Περιστατικών\nΣεισμού");

            sortEarthquakeIncidentsSwitch.setVisibility(View.VISIBLE);
            //sortingmsg.setVisibility(View.VISIBLE);
            CriteriaButton.setVisibility(View.VISIBLE);
            EarthquakesButton.setVisibility(View.VISIBLE);

            System.out.println("Switch (sort) is on!");

            if (isEnglishSelected)
                showToast("Sorting is ON");
            else
                showToast("Ταξινόμηση ενεργοποιημένη");

            EmployeeControlIncidentsActivity.findAndStoreIncidents(incidentsRef, sortedIncidentsRef, "Earthquake");
            EmployeeControlIncidentsActivity.CreateSortIncidentsLayout(sortedIncidentsRef, verifiedRef,
                    scrollViewLayout, EmployeeAllEarthquakeIncidentsActivity.this);
        }
        else { // Switch is OFF
            if (isEnglishSelected)
                Titletextview.setText("Pending Earthquake\nIncidents");
            else
                Titletextview.setText("Εκκρεμείς Περιστατικά Σεισμού");
            sortEarthquakeIncidentsSwitch.setVisibility(View.VISIBLE);
            //sortingmsg.setVisibility(View.VISIBLE);
            CriteriaButton.setVisibility(View.VISIBLE);
            EarthquakesButton.setVisibility(View.VISIBLE);

            System.out.println("Switch (sort) is off!"); // so all fire incidents

            if (isEnglishSelected)
                showToast("Sorting is OFF");
            else
                showToast("Ταξινόμηση απανεργοποιημένη");

            EmployeeControlIncidentsActivity.CreateIncidentsLayout(incidentsRef, "Earthquake", scrollViewLayout, EmployeeAllEarthquakeIncidentsActivity.this);
        }
    }

    public void seeVerifiedEarthquakes(View view){

        if (isEnglishSelected)
            Titletextview.setText("\nVerified Earthquake\nIncidents");
        else {
            Titletextview.setTextSize(17);
            Titletextview.setText("\nΕπικαιροποιημένοι Σεισμοί");
        }
        sortEarthquakeIncidentsSwitch.setVisibility(View.INVISIBLE);
        //sortingmsg.setVisibility(View.INVISIBLE);
        CriteriaButton.setVisibility(View.INVISIBLE);
        EarthquakesButton.setVisibility(View.INVISIBLE);

        verifiedRef = FirebaseDatabase.getInstance().getReference().child("Verified/Earthquakes");

        EmployeeControlIncidentsActivity.seeVerifiedIncidents(verifiedRef,
                scrollViewLayout, EmployeeAllEarthquakeIncidentsActivity.this);
    }

    public void showCriteria(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        String message;
        String closeText = "Close";

        if (isEnglishSelected) {
            message = "Examine earthquake incidents that happened the last 20 minutes and located" +
                    " within a distance of 100 kilometers";
            closeText = "Close";
        } else {
            message = "Εξέτασε περιστατικά σεισμών που συνέβησαν τα τελευταία 20 λεπτά" +
                    " και βρίσκονται σε απόσταση 100 χιλιομέτρων";
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