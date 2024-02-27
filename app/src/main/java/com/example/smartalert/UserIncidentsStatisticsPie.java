package com.example.smartalert;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class UserIncidentsStatisticsPie extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    static DatabaseReference statisticsRef;
    boolean isEnglishSelected;
    String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve language preference from SharedPreferences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        isEnglishSelected = preferences.getBoolean("english", true); // Default value is true if key "english" is not found

        // Change language based on the preference
        String lang = isEnglishSelected ? "en" : "el"; // Change this to the language code you want to switch to
        updateLocale(lang);

        setContentView(R.layout.activity_user_incidents_statistics_pie);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        statisticsRef = FirebaseDatabase.getInstance().getReference().child("Statistics");


        if (currentUser != null) {
            userEmail = currentUser.getEmail();

            // Call the function to count incidents and log the counts
            getCounts(new CountCallback() {
                @Override
                public void onCountsReceived(int earthquakeCount, int fireCount, int floodCount) {
                    // Save the counts in class-level variables

                    Log.d("UserIncidentsStatisticsPie", "Earthquake count: " + earthquakeCount);
                    Log.d("UserIncidentsStatisticsPie", "Fire count: " + fireCount);
                    Log.d("UserIncidentsStatisticsPie", "Flood count: " + floodCount);

                    setupPieChart(earthquakeCount, fireCount, floodCount);
                }

                @Override
                public void onError(String errorMessage) {
                    Log.e("UserIncidentsStatisticsPie", errorMessage);
                }

            });
        }
    }

    private void updateLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }

    private void setupPieChart(int earthquakeCount, int fireCount, int floodCount) {
        PieChart pieChart = findViewById(R.id.pie_chart);

        // Populate data for the pie chart
        PieData pieData = generatePieData(earthquakeCount, fireCount, floodCount);

        // Set up the pie chart with the populated data
        pieChart.setData(pieData);
        pieChart.invalidate();

        // Customize the pie chart appearance and legend as needed...
        // Set description label
        pieChart.getDescription().setEnabled(true);
        pieChart.getDescription().setText(isEnglishSelected ? "Incidents Distribution" : "Κατανομή Περιστατικών"); // Set the description label text
        if (ThemeUtils.isDarkTheme(this)) // Dark mode
            pieChart.getDescription().setTextColor(Color.WHITE);

        // Customize description label
        pieChart.getDescription().setTextSize(14f); // Set the text size for the description label

        // Customize legend
        Legend legend = pieChart.getLegend();
        legend.setEnabled(true);
        legend.setTextSize(12f); // Set the text size for legend entries
        legend.setXEntrySpace(15f); // Set the space between legend labels
        // Customize entry label text size
        pieChart.setEntryLabelTextSize(13f); // Set the text size for the entry labels

        if (ThemeUtils.isDarkTheme(this)) // Dark mode
            legend.setTextColor(Color.WHITE); // Set legend label color to white in dark theme
    }

    private PieData generatePieData(int earthquakeCount, int fireCount, int floodCount) {

        //System.out.println("fire: " + fireCount);

        // Create entries for the pie chart
        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(fireCount, isEnglishSelected ? "Fires" : "Φωτιές"));
        entries.add(new PieEntry(floodCount, isEnglishSelected ? "Floods" : "Πλημμύρες"));
        entries.add(new PieEntry(earthquakeCount, isEnglishSelected ? "Earthquakes" : "Σεισμοί"));

        // Create a dataset for the pie chart
        PieDataSet dataSet = new PieDataSet(entries, isEnglishSelected ? "Incidents" : "Περιστατικά");
        dataSet.setValueTextSize(16f); // Increase text size
        dataSet.setColors(Color.rgb(153,0,0), Color.rgb(0,51,102), Color.rgb(0,51,25));

        // Return the pie data
        return new PieData(dataSet);
    }

    // Function to count incidents and return the counts via a callback
    private void getCounts(final CountCallback callback) {
       statisticsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int earthquakeCount = 0;
                int fireCount = 0;
                int floodCount = 0;

                // Iterate through dataSnapshot to count incidents
                for (DataSnapshot incidentSnapshot : dataSnapshot.getChildren()) {
                    String incidentType = incidentSnapshot.getKey();
                    if (incidentType != null) {
                        for (DataSnapshot userSnapshot : incidentSnapshot.getChildren()) {
                            if (userSnapshot.child("usersEmails").exists()) {
                                ArrayList<String> userEmails = (ArrayList<String>) userSnapshot.child("usersEmails").getValue();
                                if (userEmails != null && userEmails.contains(userEmail)) {
                                    switch (incidentType) {
                                        case "Earthquake":
                                            earthquakeCount++;
                                            break;
                                        case "Fire":
                                            fireCount++;
                                            break;
                                        case "Flood":
                                            floodCount++;
                                            break;
                                    }
                                }
                            }
                        }
                    }
                }
                // Call the callback with the counts
                callback.onCountsReceived(earthquakeCount, fireCount, floodCount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Call the callback with the error message
                callback.onError("Error: " + databaseError.getMessage());
            }
        });
    }

    // Callback interface to return counts or errors
    interface CountCallback {
        void onCountsReceived(int earthquakeCount, int fireCount, int floodCount);

        void onError(String errorMessage);
    }
}