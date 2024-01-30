package com.example.smartalert;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class EmployeeAllEarthquakeIncidentsActivity extends AppCompatActivity {

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch sortEarthquakeIncidentsSwitch;
    private LinearLayout scrollViewLayout;
    private ScrollView scrollView;
    private DatabaseReference incidentsRef, sortedIncidentsRef;
    private FirebaseDatabase database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_earthquake_incidents);

        database = FirebaseDatabase.getInstance();
        sortEarthquakeIncidentsSwitch = findViewById(R.id.switch2);
        scrollViewLayout = findViewById(R.id.scrollViewLayout2);
        scrollView = findViewById(R.id.scrollview2);

        //scrollView.setBackgroundColor(Color.rgb(224,224,224));
        scrollView.setBackgroundColor(Color.TRANSPARENT);

        CreateIncidentsLayout();
    }

    public void CreateIncidentsLayout(){
        // Get a reference to the database
        incidentsRef = FirebaseDatabase.getInstance().getReference().child("incidents");

        incidentsRef.orderByChild("type").equalTo("Earthquake").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                scrollViewLayout.removeAllViews(); // Clear existing views

                if (dataSnapshot.exists()) {
                    // Earthquake incidents exist
                    for (DataSnapshot incidentSnapshot : dataSnapshot.getChildren()) {
                        // Create a vertical LinearLayout for each incident
                        LinearLayout incidentLayout = new LinearLayout(EmployeeAllEarthquakeIncidentsActivity.this);
                        incidentLayout.setOrientation(LinearLayout.VERTICAL);
                        incidentLayout.setLayoutParams(new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                        ));
                        incidentLayout.setBackgroundColor(Color.rgb(32, 32, 32));

                        // Add incident information to the layout
                        addIncidentInfoToLayout(incidentSnapshot, incidentLayout);

                        // Create Verify and Delete buttons

                    /*createVerifyButton(incidentSnapshot, incidentLayout);
                    createDeleteButton(incidentSnapshot, incidentLayout);*/

                        // Add the incident layout to the linear layout
                        scrollViewLayout.addView(incidentLayout);
                    }
                }else { // No Earthquake incidents
                    TextView noIncidentsTextView = new TextView(EmployeeAllEarthquakeIncidentsActivity.this);
                    noIncidentsTextView.setText("No incidents to show.");
                    noIncidentsTextView.setTextColor(Color.BLACK);
                    scrollViewLayout.addView(noIncidentsTextView);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors here
            }
        });
    }

    private void addIncidentInfoToLayout(DataSnapshot incidentSnapshot, LinearLayout incidentLayout) {
        // Extract incident information
        String location  = incidentSnapshot.child("location").getValue(String.class);
        String timestamp = incidentSnapshot.child("timestamp").getValue(String.class);
        String comment   = incidentSnapshot.child("comment").getValue(String.class);
        String userEmail = incidentSnapshot.child("userEmail").getValue(String.class);
        String image = incidentSnapshot.child("image").getValue(String.class);

        // Display incident information using TextViews
        TextView locationTextView = new TextView(this);
        locationTextView.setText("Location: " + location);
        locationTextView.setTextColor(Color.WHITE);

        TextView timestampTextView = new TextView(this);
        timestampTextView.setText("Timestamp: " + timestamp);
        timestampTextView.setTextColor(Color.WHITE);

        TextView commentTextView = new TextView(this);
        commentTextView.setText("Comment: " + comment);
        commentTextView.setTextColor(Color.WHITE);

        TextView userEmailTextView = new TextView(this);
        userEmailTextView.setText("User Email: " + userEmail);
        userEmailTextView.setTextColor(Color.WHITE);

        // Create an ImageView for the incident image
        ImageView imageView = new ImageView(this);

        if (!isDestroyed()) {
            // Load image using Glide
            Glide.with(this)
                    .load(image)
                    .into(imageView);
        }

        // Add TextViews and ImageView to the incident layout
        incidentLayout.addView(locationTextView);
        incidentLayout.addView(timestampTextView);
        incidentLayout.addView(commentTextView);
        incidentLayout.addView(userEmailTextView);
        incidentLayout.addView(imageView);
    }

    public void checkSwitch(View view) {
        // Check the state of the Switch when the Button is clicked
        boolean isSwitchOn = sortEarthquakeIncidentsSwitch.isChecked();
        sortedIncidentsRef = database.getReference("SortedIncidents/Earthquake");
        //incidentsRef = database.getReference().child("incidents");

        // Perform actions based on the Switch state
        if (isSwitchOn) {
            sortedIncidentsRef.removeValue();
            System.out.println("Switch (sort) is on!");

            //CreateSortIncidentsLayout();
            showToast("Sorting is ON");
            SortEarthquakeIncidents();

            //findAndStoreIncidents();
        } else { // Switch is OFF
            System.out.println("Switch (sort) is off!"); // so all fire incidents
            showToast("Sorting is OFF");

            CreateIncidentsLayout();
        }
    }

    public void SortEarthquakeIncidents() {

        incidentsRef = database.getReference().child("incidents");
        incidentsRef.orderByChild("type").equalTo("Earthquake").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                processAndStoreEarthquakeIncidents(dataSnapshot);
                //removeDuplicatesInSortedIncidents();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Error fetching data", databaseError.toException());
            }
        });
    }

    private void processAndStoreEarthquakeIncidents(DataSnapshot dataSnapshot) {

        //sortedIncidentsRef.removeValue();
        //Incident prevIncident = null;
        //String type_ = "Fire";

        for (DataSnapshot incidentSnapshot : dataSnapshot.getChildren()) {
            Map<String, Integer> userSubmissionCount = new HashMap<>(); // use 4 subNumber

            List<String> keys = new ArrayList<>();
            List<String> comments = new ArrayList<>();
            List<String> locations = new ArrayList<>();
            List<String> timestamps = new ArrayList<>();
            List<String> photos = new ArrayList<>();

            System.out.println("Incident: " + incidentSnapshot);

            String userEmail = incidentSnapshot.child("userEmail").getValue(String.class);
            String timestamp = incidentSnapshot.child("timestamp").getValue(String.class);
            String location = incidentSnapshot.child("location").getValue(String.class);
            String comment = incidentSnapshot.child("comment").getValue(String.class);
            String image = incidentSnapshot.child("image").getValue(String.class);

            userSubmissionCount.put(userEmail, 1);
            keys.add(incidentSnapshot.getKey());
            comments.add(comment);
            locations.add(location);
            timestamps.add(timestamp);
            photos.add(image);

            for (DataSnapshot incidentSnapshotInner : dataSnapshot.getChildren()) {
                System.out.println("Inner incidence: " + incidentSnapshotInner);
                String userEmailInner = incidentSnapshotInner.child("userEmail").getValue(String.class);
                String timestampInner = incidentSnapshotInner.child("timestamp").getValue(String.class);
                String locationInner = incidentSnapshotInner.child("location").getValue(String.class);
                String commentInner = incidentSnapshotInner.child("comment").getValue(String.class);
                String imageInner = incidentSnapshotInner.child("image").getValue(String.class);
                String keyInner = incidentSnapshotInner.getKey();

                if ((isWithin2Minutes(timestamp, timestampInner) /*&& (isWithin80Km(location, locationInner)))*/)) {
                    assert userEmail != null;
                    if (!userEmail.equals(userEmailInner) && !userSubmissionCount.containsKey(userEmailInner)) {
                        System.out.println("Able to sort");

                        userSubmissionCount.put(userEmailInner, userSubmissionCount.getOrDefault(userEmailInner, 0) + 1);
                        keys.add(keyInner);
                        comments.add(commentInner);
                        locations.add(locationInner);
                        timestamps.add(timestampInner);
                        photos.add(imageInner);
                    }
                }
            }

            // find number of submissions in the same incidence
            int numberOfEntries = userSubmissionCount.size();
            System.out.println("Number of submissions is: " + numberOfEntries);
            System.out.println(userSubmissionCount);

            // save them to firebase
            Incident incident = new Incident(keys, comments, locations, timestamps, photos, numberOfEntries, "not verified");


            saveDataInSortedIncidentsEarthquake(incident);
        }
    }

    private boolean isWithin2Minutes(String prevTimestamp, String timestamp) {
        try {
            // Define the timestamp format
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy-HH-mm-ss", Locale.getDefault());

            // Parse the timestamps into Date objects
            Date incidentDate1 = dateFormat.parse(prevTimestamp);
            Date incidentDate2 = dateFormat.parse(timestamp);

            // Calculate the time difference in minutes
            long diffInMinutes = Math.abs(incidentDate1.getTime() - incidentDate2.getTime()) / (60 * 1000);

            // Check if the difference is less than 2 minutes
            return diffInMinutes < 2;
        } catch (ParseException e) {
            e.printStackTrace();
            return false; // Return false in case of an error
        }
    }

    private void saveDataInSortedIncidentsEarthquake(Incident incident) {

        sortedIncidentsRef = database.getReference("SortedIncidents/Earthquake");

        sortedIncidentsRef.orderByChild("keys").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean isDuplicate = false;

                for (DataSnapshot incidentSnapshot : dataSnapshot.getChildren()) {
                    Incident existingIncident = incidentSnapshot.getValue(Incident.class);

                    if (existingIncident != null &&
                            areListsEqualIgnoreOrder(existingIncident.getKeys(), incident.getKeys())) {
                        // Duplicate found
                        isDuplicate = true;
                        break;
                    }
                }

                if (!isDuplicate) {
                    // Not a duplicate, save the new incident
                    sortedIncidentsRef.push().setValue(incident)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    System.out.println("Incident has been saved successfully");
                                } else {
                                    System.out.println("Error saving incident");
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors here
            }
        });
    }

    private boolean areListsEqualIgnoreOrder(List<String> list1, List<String> list2) {
        // Check if two lists contain the same elements regardless of order
        return new HashSet<>(list1).equals(new HashSet<>(list2));
    }

    private void showToast(String message) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
}