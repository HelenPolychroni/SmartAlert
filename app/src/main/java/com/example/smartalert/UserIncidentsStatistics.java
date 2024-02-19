package com.example.smartalert;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class UserIncidentsStatistics extends AppCompatActivity {

    ArrayList<BarEntry> barArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_incidents_statistics);

        BarChart barChart = findViewById(R.id.barchart);

        getData();

        BarDataSet barDataSet = new BarDataSet(barArrayList, "Incident Category");
        // color bar data set
        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);

        BarData barData = new BarData(barDataSet);
        barChart.setData(barData);

        // text color
        barDataSet.setValueTextColor(Color.BLACK);

        // setting text size
        barDataSet.setValueTextSize(16f);

        // Set description for the chart
        barChart.getDescription().setText("Incidents Statistics");


        // Customize the legend
        Legend legend = barChart.getLegend();
        legend.setEnabled(true);
        legend.setTextColor(Color.BLACK);

        // Customize the x-axis
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setTextSize(12f);
        xAxis.setDrawAxisLine(true);
        //xAxis.setDrawGridLines(false); // Hide grid lines
        xAxis.setDrawLabels(true);
        xAxis.setAxisLineColor(Color.BLACK);
        xAxis.setAxisLineWidth(2f);
        /*xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                // Convert float value to incident category
                String category = "fire"; // Implement conversion here based on your data
                return category;
            }
        });*/


        // Customize the y-axis
        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setAxisMinimum(0f); // Start the axis from zero
        leftAxis.setTextColor(Color.BLACK);
        leftAxis.setTextSize(12f);
        leftAxis.setDrawAxisLine(true);
        //leftAxis.setDrawGridLines(false); // Hide grid lines
        leftAxis.setDrawLabels(true);
        leftAxis.setAxisLineColor(Color.BLACK);
        leftAxis.setAxisLineWidth(2f);
        leftAxis.setDrawZeroLine(true); // Draw zero line


        /*leftAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                // Convert float value to month name
                String month = "may"; // Implement conversion here based on your data
                return month;
            }
        });*/

        // Hide the right y-axis
        barChart.getAxisRight().setEnabled(false);

        // Add data to the legend
        LegendEntry[] legendEntries = new LegendEntry[]{
                new LegendEntry("Fire", Legend.LegendForm.SQUARE, 10f, 2f, null, Color.RED),
                new LegendEntry("Flood", Legend.LegendForm.SQUARE, 10f, 2f, null, Color.BLUE),
                new LegendEntry("Earthquake", Legend.LegendForm.SQUARE, 10f, 2f, null, Color.GREEN),
                // Add more entries for other incident categories as needed
        };
        legend.setCustom(legendEntries);

    }

    private void getData(){
        barArrayList = new ArrayList<>();
        barArrayList.add(new BarEntry(2f, 10));
        barArrayList.add(new BarEntry(6f, 20));
        barArrayList.add(new BarEntry(12f, 50));
        barArrayList.add(new BarEntry(22f, 90));
    }
}