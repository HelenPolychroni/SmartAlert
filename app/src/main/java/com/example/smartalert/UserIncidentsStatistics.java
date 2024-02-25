package com.example.smartalert;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
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
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class UserIncidentsStatistics extends AppCompatActivity {

    List<BarEntry> mixedEntries = new ArrayList<>();
    List<BarEntry> firesData = new ArrayList<>();
    List<BarEntry> floodsData = new ArrayList<>();
    List<BarEntry> earthquakesData = new ArrayList<>();

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

        setContentView(R.layout.activity_user_incidents_statistics);

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

                for (Map.Entry<String, List<Integer>> entry : incidentCountsPerType.entrySet()) {
                    String incidentType = entry.getKey();
                    List<Integer> incidentCounts = entry.getValue();

                    Log.d("IncidentCounts", "Incident type: " + incidentType + ", Counts: " + incidentCounts);
                }
                // Process the data and set up the chart
                getDataAndSetupChart();
            });
        }
       // BarChart barChart = findViewById(R.id.barchart);



        //BarDataSet barDataSet = new BarDataSet(barArrayList, "Incident Category");

        // Create datasets for each incident type
        //BarDataSet firesDataSet = new BarDataSet(firesData, "Fires");
        //BarDataSet floodsDataSet = new BarDataSet(floodsData, "Floods");
        //BarDataSet earthquakesDataSet = new BarDataSet(earthquakesData, "Earthquakes");

        // color bar data set
        //barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);

        // Customize dataset properties if needed
        //firesDataSet.setColor(Color.RED);
        //floodsDataSet.setColor(Color.BLUE);
        //earthquakesDataSet.setColor(Color.GREEN);

        // Add datasets to a list
       // List<IBarDataSet> dataSets = new ArrayList<>();
        //dataSets.add(firesDataSet);
        //dataSets.add(floodsDataSet);
        //dataSets.add(earthquakesDataSet);

        // Create a BarData object with the list of datasets
        //BarData barData1 = new BarData(dataSets);

        /*
        // Calculate the width for each group of bars
        float groupSpace = 0.3f; // Adjust this value as needed
        float barWidth = 0.2f; // Adjust this value as needed
        float barSpace = 0.1f; // Adjust this value as needed

        // Set the spacing properties for the bar chart
        barData1.setBarWidth(barWidth);
        */

        //barChart.setData(barData1);
        //barChart.groupBars(0, groupSpace, barSpace); // Adjust the first parameter if needed

        // Set up your BarChart
        //BarChart barChart = findViewById(R.id.barChart); // Assuming you have a BarChart view in your layout


        // Refresh the chart
       // barChart.invalidate();
    }

    private void updateLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }

    private void getDataAndSetupChart() {
        System.out.println("You are here");

        // Populate firesData, floodsData, and earthquakesData based on the retrieved data
        getData();

        // Set up the chart with the populated data
        setupChart();
    }

    private void getData() {
        // Ensure that FiresIncidents, FloodIncidents, and EarthquakeIncidents are populated
        // before accessing them in this method
        if (FiresIncidents == null || FloodIncidents == null || EarthquakeIncidents == null) {
            Log.e("getData", "Incidents data is null");
            return;
        }

        // Clear existing data in the lists
        firesData.clear();
        floodsData.clear();
        earthquakesData.clear();

        System.out.println("Fires " + FiresIncidents);
        System.out.println("Floods: " + FloodIncidents);
        System.out.println("Earthquakes: " + EarthquakeIncidents);

        for (int month = 0; month < 12; month++) {
            // Add the count for fires to the fires dataset
            firesData.add(new BarEntry(FiresIncidents.get(month), month + 1 ));

            // Add the count for floods to the floods dataset
            floodsData.add(new BarEntry(FloodIncidents.get(month), month + 1));

            // Add the count for earthquakes to the earthquakes dataset
            earthquakesData.add(new BarEntry(EarthquakeIncidents.get(month), month + 1));
        }

        mixedEntries.add(new BarEntry(0,0, "Fire and Flood"));
        mixedEntries.add(new BarEntry(0,0, "Fire and Earthquake"));
        mixedEntries.add(new BarEntry(0,0, "Flood and Earthquake"));
        mixedEntries.add(new BarEntry(0,0, "Fire, Flood, and Earthquake"));
    }

    private void setupChart() {
        BarChart barChart = findViewById(R.id.barchart);

        // Create datasets for each incident type
        BarDataSet firesDataSet = new BarDataSet(firesData, "Fires");
        BarDataSet floodsDataSet = new BarDataSet(floodsData, "Floods");
        BarDataSet earthquakesDataSet = new BarDataSet(earthquakesData, "Earthquakes");

        // Create a dataset for mixed overlapping incidents
        BarDataSet mixedDataSet = new BarDataSet(mixedEntries, "Mixed Incidents");

        // Customize dataset properties if needed
        //mixedDataSet.setColors(Color.MAGENTA, Color.YELLOW, Color.CYAN, Color.WHITE); // Assign colors for mixed incidents


        // Customize dataset properties if needed
        firesDataSet.setColor(Color.RED);
        floodsDataSet.setColor(Color.BLUE);
        earthquakesDataSet.setColor(Color.GREEN);

        // Add datasets to a list
        List<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(firesDataSet);
        dataSets.add(floodsDataSet);
        dataSets.add(earthquakesDataSet);

        //dataSets.add(mixedDataSet);

        // Create a BarData object with the list of datasets
        BarData barData = new BarData(dataSets);
        barChart.setData(barData);

        // Refresh the chart
        barChart.invalidate();


        if (ThemeUtils.isDarkTheme(this)) { // Dark mode
            for (IBarDataSet barDataSet : dataSets) {
                barDataSet.setDrawValues(true);
                barDataSet.setValueTextColor(Color.WHITE);
                // setting text size
                barDataSet.setValueTextSize(13f);
            }
        }
        else {
            for (IBarDataSet barDataSet : dataSets) {
                barDataSet.setDrawValues(true);
                barDataSet.setValueTextColor(Color.BLACK);
                // setting text size
                barDataSet.setValueTextSize(16f);
            }
        }

        //barChart.getDescription().setText("Incidents Statistics");
        //barChart.getDescription().setTextColor(Color.WHITE);



        // Customize the legend
        Legend legend = barChart.getLegend();
        legend.setEnabled(true);
        legend.setTextColor(Color.BLACK);
        if (ThemeUtils.isDarkTheme(this)) { // Dark mode
            legend.setTextColor(Color.WHITE);
        }

        // Set the position of the legend above the chart
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false); // Ensure the legend is drawn outside the chart area

        // Adjust the legend offset to leave space for the second row
        legend.setYOffset(legend.getYOffset() + 50f); // Adjust the offset as needed
        legend.setTextSize(10f);

       // Set the spacing between legend entries
        legend.setXEntrySpace(28f); // Adjust the spacing as needed

        // Customize the x-axis
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setAxisLineColor(Color.BLACK);

        if (ThemeUtils.isDarkTheme(this)) { // Dark mode
            xAxis.setTextColor(Color.WHITE);
            xAxis.setAxisLineColor(Color.WHITE);
        }
        xAxis.setTextSize(12f);
        xAxis.setDrawAxisLine(true);
        //xAxis.setDrawGridLines(false); // Hide grid lines
        xAxis.setDrawLabels(true);

        xAxis.setAxisLineWidth(2f);

        // Customize the y-axis
        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setAxisMinimum(0f); // Start the axis from zero
        leftAxis.setTextColor(Color.BLACK);
        leftAxis.setAxisLineColor(Color.BLACK);

        if (ThemeUtils.isDarkTheme(this)) { // Dark mode
            leftAxis.setTextColor(Color.WHITE);
            leftAxis.setAxisLineColor(Color.WHITE);
        }
        leftAxis.setTextSize(12f);
        leftAxis.setDrawAxisLine(true);
        //leftAxis.setDrawGridLines(false); // Hide grid lines
        leftAxis.setDrawLabels(true);

        leftAxis.setAxisLineWidth(2f);
        leftAxis.setDrawZeroLine(true); // Draw zero line

        // Hide the right y-axis
        barChart.getAxisRight().setEnabled(false);

        LegendEntry[] legendEntries;

        if (isEnglishSelected) {
            // Add data to the legend

            legendEntries = new LegendEntry[]{
                    new LegendEntry("Fires", Legend.LegendForm.SQUARE, 10f, 2f, null, Color.RED),
                    new LegendEntry("Floods", Legend.LegendForm.SQUARE, 10f, 2f, null, Color.BLUE),
                    new LegendEntry("Earthquakes", Legend.LegendForm.SQUARE, 10f, 2f, null, Color.GREEN),

                    // Purple (Red + Blue)
                    //new LegendEntry("Fire and Flood", Legend.LegendForm.SQUARE, 10f, 2f, null, Color.MAGENTA),
            };
        }
        else {
             legendEntries = new LegendEntry[]{
                    new LegendEntry("Φωτιές", Legend.LegendForm.SQUARE, 10f, 2f, null, Color.RED),
                    new LegendEntry("Πλημμύρες", Legend.LegendForm.SQUARE, 10f, 2f, null, Color.BLUE),
                    new LegendEntry("Σεισμοί", Legend.LegendForm.SQUARE, 10f, 2f, null, Color.GREEN),

                    // Purple (Red + Blue)
                    //new LegendEntry("Fire and Flood", Legend.LegendForm.SQUARE, 10f, 2f, null, Color.MAGENTA),
            };
        }

       /* // Define the additional legend entries for mixed colors
        LegendEntry[] mixedLegendEntries = new LegendEntry[]{

                // Yellow (Red + Green)
                new LegendEntry("Fire and Earthquake", Legend.LegendForm.SQUARE, 10f, 2f, null, Color.YELLOW),

                // Cyan (Blue + Green)
                new LegendEntry("Flood and Earthquake", Legend.LegendForm.SQUARE, 10f, 2f, null, Color.CYAN),

                // White (Red + Blue + Green)
                new LegendEntry("Fire, Flood, and Earthquake", Legend.LegendForm.SQUARE, 10f, 2f, null, Color.WHITE)
        };

        // Combine the legend entries
        LegendEntry[] allLegendEntries = new LegendEntry[legendEntries.length + mixedLegendEntries.length];
        System.arraycopy(legendEntries, 0, allLegendEntries, 0, legendEntries.length);
        System.arraycopy(mixedLegendEntries, 0, allLegendEntries, legendEntries.length, mixedLegendEntries.length);

        // Set the complete legend entries*/

        legend.setCustom(legendEntries);
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