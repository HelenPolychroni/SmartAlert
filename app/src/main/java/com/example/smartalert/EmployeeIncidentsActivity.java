package com.example.smartalert;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import static com.example.smartalert.R.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Locale;

public class EmployeeIncidentsActivity extends AppCompatActivity {
    AutoCompleteTextView incidentType;
    String incident_type;
    Class<?> page;
    private BottomNavigationView bottomNavigationView;

    private TextView textView9;
    private Button button9;
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

        setContentView(layout.activity_employee_incidents);

        incidentType = findViewById(id.autoCompleteTextView2);

        if (ThemeUtils.isDarkTheme(this)) { // Dark mode

            textView9 = findViewById(R.id.textView9);
            button9 = findViewById(id.button9);

            textView9.setTextColor(getResources().getColor(R.color.white));
            button9.setTextColor(getResources().getColor(R.color.white));
        }
    }

    private void updateLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
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
                if (isEnglishSelected)
                    Toast.makeText(this, "Please select incident type", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(this, "Παρακαλώ επιλέξτε κατηγορία περιστατικού", Toast.LENGTH_SHORT).show();
                return;
        }
        if (flag)
            if (isEnglishSelected)
                Toast.makeText(this, "Incident type " + incident_type.toLowerCase() + " is selected", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(this, "Επιλέχθηκε η κατηγορία περιστατικού " + incident_type.toLowerCase(), Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, page);
        startActivity(intent);
    }









}