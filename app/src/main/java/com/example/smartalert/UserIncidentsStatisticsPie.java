package com.example.smartalert;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;

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

    private List<Integer> FiresIncidents = new ArrayList<>();
    private List<Integer> FloodIncidents = new ArrayList<>();
    private List<Integer> EarthquakeIncidents = new ArrayList<>();
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

        setContentView(R.layout.activity_user_incidents_statistics_pie);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        statisticsRef = FirebaseDatabase.getInstance().getReference().child("Statistics");


        if (currentUser != null) {
            String userEmail = currentUser.getEmail();

            countIncidentsPerMonth(userEmail, incidentCountsPerType -> {
                // Process the received incident counts map here
                // Store the incident counts in global variables
                FiresIncidents = incidentCountsPerType.get("Fire");
                FloodIncidents = incidentCountsPerType.get("Flood");
                EarthquakeIncidents = incidentCountsPerType.get("Earthquake");

                // Process the data and set up the pie chart
                setupPieChart();
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

    private void setupPieChart() {
        PieChart pieChart = findViewById(R.id.pie_chart);

        // Populate data for the pie chart
        PieData pieData = generatePieData();

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

    private PieData generatePieData() {
        // Calculate total counts for each incident type
        int totalFires = calculateTotal(FiresIncidents);
        int totalFloods = calculateTotal(FloodIncidents);
        int totalEarthquakes = calculateTotal(EarthquakeIncidents);

        // Create entries for the pie chart
        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(totalFires, isEnglishSelected ? "Fires" : "Φωτιές"));
        entries.add(new PieEntry(totalFloods, isEnglishSelected ? "Floods" : "Πλημμύρες"));
        entries.add(new PieEntry(totalEarthquakes, isEnglishSelected ? "Earthquakes" : "Σεισμοί"));

        // Create a dataset for the pie chart
        PieDataSet dataSet = new PieDataSet(entries, isEnglishSelected ? "Incidents" : "Περιστατικά");
        dataSet.setValueTextSize(16f); // Increase text size
        dataSet.setColors(Color.rgb(153,0,0), Color.rgb(0,51,102), Color.rgb(0,51,25));

        // Customize dataset properties if needed...

        // Return the pie data
        return new PieData(dataSet);
    }

    private int calculateTotal(List<Integer> counts) {
        int total = 0;
        for (Integer count : counts) {
            total += count;
        }
        return total;
    }

    public static void countIncidentsPerMonth(String userEmail, Consumer<Map<String, List<Integer>>> callback) {
        DatabaseReference statisticsRef = FirebaseDatabase.getInstance().getReference().child("Statistics");

        // Initialize a map to store incident counts for each incident type
        Map<String, List<Integer>> incidentCountsPerType = new HashMap<>();

        // Counter for the number of incident types processed
        AtomicInteger count = new AtomicInteger(0);

        for (String incidentType : Arrays.asList("Fire", "Flood", "Earthquake")) {
            List<Integer> incidentCountsPerMonth = new ArrayList<>();
            for (int month = 1; month <= 12; month++) {
                final int finalMonth = month; // Need to use final variable in lambda expression
                final int[] incidentCount = {0}; // Use an array to make it effectively final

                DatabaseReference incidentTypeRef = statisticsRef.child(incidentType);
                incidentTypeRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot incidentSnapshot : dataSnapshot.getChildren()) {
                            // Check if user's email is in the list of user emails
                            List<String> usersEmails = new ArrayList<>();
                            DataSnapshot usersEmailsSnapshot = incidentSnapshot.child("usersEmails");
                            for (DataSnapshot emailSnapshot : usersEmailsSnapshot.getChildren()) {
                                usersEmails.add(emailSnapshot.getValue(String.class));
                            }
                            if (usersEmails.contains(userEmail)) {
                                // Check if the timestamp falls within the desired month
                                String timestamp = incidentSnapshot.child("timestamp").getValue(String.class);
                                int incidentMonth = Integer.parseInt(timestamp.split("-")[1]);
                                if (incidentMonth == finalMonth) {
                                    incidentCount[0]++;
                                }
                            }
                        }
                        // Add the incident count for the current month to the list
                        incidentCountsPerMonth.add(incidentCount[0]);

                        // Check if all months have been processed for this incident type
                        if (incidentCountsPerMonth.size() == 12) {
                            // Add the incident counts for the current incident type to the map
                            incidentCountsPerType.put(incidentType, incidentCountsPerMonth);

                            // Increment the counter and check if all incident types have been processed
                            if (count.incrementAndGet() == 3) {
                                // All incident types have been processed, invoke the callback
                                callback.accept(incidentCountsPerType);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        System.out.println("Error retrieving incident data: " + databaseError.getMessage());
                    }
                });
            }
        }
    }

}