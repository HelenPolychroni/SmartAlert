package com.example.smartalert;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class EmployeeControlIncidentsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_control_incidents);

    }

    public static void CreateIncidentsLayout(DatabaseReference incidentsRef, String type, LinearLayout scrollViewLayout,
                                             Context context) {
        Query IncidentsQuery = incidentsRef.orderByChild("type").equalTo(type);
        IncidentsQuery.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                scrollViewLayout.removeAllViews(); // Clear existing views

                if (dataSnapshot.exists()) { // Incidents exist

                    for (DataSnapshot incidentSnapshot : dataSnapshot.getChildren()) {
                        // Create a vertical LinearLayout for each incident
                        LinearLayout incidentLayout = new LinearLayout(context);
                        incidentLayout.setOrientation(LinearLayout.VERTICAL);
                        incidentLayout.setLayoutParams(new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                        ));
                        incidentLayout.setBackgroundColor(Color.rgb(32, 32, 32));

                        // Add incident information to the layout
                        addIncidentInfoToLayout(incidentSnapshot, incidentLayout, context);

                        // Add the incident layout to the linear layout
                        scrollViewLayout.addView(incidentLayout);
                    }
                } else { // No pending Incidents
                    TextView noIncidentsTextView = new TextView(context);
                    noIncidentsTextView.setText("No pending incidents to show.");
                    noIncidentsTextView.setTextColor(Color.BLACK);
                    scrollViewLayout.addView(noIncidentsTextView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors here
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private static void addIncidentInfoToLayout(DataSnapshot incidentSnapshot, LinearLayout incidentLayout, Context context) {
        // Extract incident information
        String location = incidentSnapshot.child("location").getValue(String.class);
        String timestamp = incidentSnapshot.child("timestamp").getValue(String.class);
        String userEmail = incidentSnapshot.child("userEmail").getValue(String.class);
        String comment = incidentSnapshot.child("comment").getValue(String.class);
        String image = incidentSnapshot.child("image").getValue(String.class);

        // Display incident information using TextViews
        TextView locationTextView = new TextView(context);
        locationTextView.setText("Location: " + location);
        locationTextView.setTextColor(Color.WHITE);

        TextView timestampTextView = new TextView(context);
        timestampTextView.setText("Timestamp: " + timestamp);
        timestampTextView.setTextColor(Color.WHITE);

        TextView userEmailTextView = new TextView(context);
        userEmailTextView.setText("User Email: " + userEmail);
        userEmailTextView.setTextColor(Color.WHITE);

        TextView commentTextView = new TextView(context);
        commentTextView.setText("Comment: " + comment);
        commentTextView.setTextColor(Color.WHITE);

        // Create an ImageView for the incident image
        ImageView imageView = new ImageView(context);

        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            if (!activity.isDestroyed()) {
                // Load image using Glide
                Glide.with(context)
                        .load(image)
                        .into(imageView);
            }
        }

        // Add TextViews and ImageView to the incident layout
        incidentLayout.addView(locationTextView);
        incidentLayout.addView(timestampTextView);
        incidentLayout.addView(commentTextView);
        incidentLayout.addView(userEmailTextView);
        incidentLayout.addView(imageView);
    }

    static void findAndStoreIncidents(DatabaseReference incidentsRef, DatabaseReference sortedIncidentsRef, String type) {

        //sortedIncidentsRef.removeValue();  // refresh feed

        incidentsRef.orderByChild("type").equalTo(type).addValueEventListener(new ValueEventListener() {   // look 4 fires
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                processAndStoreIncidents(dataSnapshot, sortedIncidentsRef, type);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Error fetching data", databaseError.toException());
            }
        });
    }

    private static void processAndStoreIncidents(DataSnapshot dataSnapshot, DatabaseReference sortedIncidentsRef, String type) {

        sortedIncidentsRef.removeValue();
        List<Incident> incidentList = new ArrayList<>();
        int hours = 0;
        int minutes = 0;

        double distanceKm = 0, distanceMeters = 0;

        switch (type) {
            case "Fire":
                hours = 24;
                distanceKm = 30;
                break;
            case "Flood":
                hours = 24;
                distanceKm = 40;
                break;
            case "Earthquake":
                minutes = 20;
                distanceKm = 100;
                break;
        }

        for (DataSnapshot incidentSnapshot : dataSnapshot.getChildren()) {
            Map<String, Integer> userSubmissionCount = new HashMap<>(); // use 4 subNumber

            List<String> keys = new ArrayList<>();
            List<String> comments = new ArrayList<>();
            List<String> locations = new ArrayList<>();
            List<String> timestamps = new ArrayList<>();
            List<String> photos = new ArrayList<>();

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
                String userEmailInner = incidentSnapshotInner.child("userEmail").getValue(String.class);
                String timestampInner = incidentSnapshotInner.child("timestamp").getValue(String.class);
                String locationInner = incidentSnapshotInner.child("location").getValue(String.class);
                String commentInner = incidentSnapshotInner.child("comment").getValue(String.class);
                String imageInner = incidentSnapshotInner.child("image").getValue(String.class);

                if (!Objects.equals(incidentSnapshot.getKey(), incidentSnapshotInner.getKey())) {

                    if ((Incident.isWithinTimeframe(timestamp, timestampInner, hours, minutes) &&
                            (Incident.isWithinDistance(location, locationInner, distanceKm, distanceMeters)))) {
                        assert userEmail != null;
                        if (!userEmail.equals(userEmailInner) && !userSubmissionCount.containsKey(userEmailInner)) {
                            System.out.println("Able to sort");

                            userSubmissionCount.put(userEmailInner, userSubmissionCount.getOrDefault(userEmailInner, 0) + 1);
                            keys.add(incidentSnapshotInner.getKey());
                            comments.add(commentInner);
                            locations.add(locationInner);
                            timestamps.add(timestampInner);
                            photos.add(imageInner);
                        }
                    }
                }
            }
            // find number of submissions
            int numberOfEntries = userSubmissionCount.size();
            System.out.println("Number of submissions is: " + numberOfEntries);
            System.out.println(userSubmissionCount);


            Incident incident = new Incident(keys, comments, locations, timestamps, photos, numberOfEntries, "not verified");
            incidentList.add(incident);
        }

        System.out.println("Incident list keys b4:");
        for (Incident incident : incidentList) {
            System.out.println(incident.getKeys());
        }

        Set<Set<String>> uniqueKeyCombinations = new HashSet<>();
        List<Incident> filteredIncidents = new ArrayList<>();

        for (Incident incident : incidentList) {
            Set<String> keySet = new HashSet<>(incident.getKeys());
            if (uniqueKeyCombinations.add(keySet)) {
                // This is a unique key combination, add the incident to the filtered list
                filteredIncidents.add(incident);
            }
        }
        System.out.println("After");
        for (Incident incident : filteredIncidents) {
            System.out.println(incident.getKeys());
        }
        saveIncidentsInSortedIncidents(filteredIncidents, sortedIncidentsRef);
    }

    public static void saveIncidentsInSortedIncidents(List<Incident> incidentList, DatabaseReference sortedIncidentsRef) {

        for (Incident incident : incidentList) {
            saveDataInSortedIncidents(incident, sortedIncidentsRef);
        }
    }

    private static void saveDataInSortedIncidents(Incident incident, DatabaseReference sortedIncidentsRef) {
        // Use sortedIncidentsRef to push the incident data to the "SortedIncidents/Fire" dataset
        sortedIncidentsRef.push().setValue(incident).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                System.out.println("Incident has been saved successfully");
            } else {
                System.out.println("Error saving incident");
            }
        });
    }

    public static void CreateSortIncidentsLayout(DatabaseReference sortedIncidentsRef, DatabaseReference verifiedRef,
                                                 LinearLayout scrollViewLayout, Context context) {
        //sortedIncidentsRef.removeValue();

        sortedIncidentsRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                scrollViewLayout.removeAllViews(); // Clear existing views

                if (dataSnapshot.exists()) {
                    for (DataSnapshot sortedIncidentSnapshot : dataSnapshot.getChildren()) {
                        // Create a vertical LinearLayout for each incident
                        LinearLayout incidentLayout = new LinearLayout(context);
                        incidentLayout.setOrientation(LinearLayout.VERTICAL);
                        incidentLayout.setLayoutParams(new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                        ));
                        incidentLayout.setBackgroundColor(Color.rgb(32, 32, 32));

                        // Add incident information to the layout
                        addSortedIncidentInfoToLayout(sortedIncidentSnapshot, incidentLayout, context);

                        // Create Verify and Delete buttons
                        createVerifyButton(sortedIncidentSnapshot, incidentLayout, verifiedRef, context);
                        createDeleteButton(sortedIncidentSnapshot, incidentLayout, context);

                        // Add the incident layout to the linear layout
                        scrollViewLayout.addView(incidentLayout);
                    }
                } else {
                    TextView noIncidentsTextView = new TextView(context);
                    noIncidentsTextView.setText("No incidents to examine.");
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

    @SuppressLint("SetTextI18n")
    private static void addSortedIncidentInfoToLayout(DataSnapshot sortedIncidentSnapshot, LinearLayout incidentLayout, Context context) {

        Incident incident = sortedIncidentSnapshot.getValue(Incident.class);

        if (incident != null) {
            // Create a TextView for each incident and append it to the ScrollView
            TextView incidentTextView = new TextView(context);
            incidentTextView.setText(new StringBuilder().append("Number of Submissions: ").append(incident.getSubNumber()).
                    append("\nComments: ").append(String.join(", ", incident.getComments())).
                    append("\nLocations: ").append(String.join(", ", incident.getLocations())).
                    append("\nTimestamps: ").append(String.join(", ", incident.getTimestamps())).
                    append("\n\nStatus: ").append(String.join(", ", incident.getStatus())).toString());

            incidentTextView.setTextColor(Color.WHITE);
            incidentTextView.setBackgroundColor(Color.BLACK);


            // Assuming photos is a List<String> in your Incident class
            List<String> photos = incident.getPhotos();

            if (photos != null && !photos.isEmpty()) {

                // Create a vertical LinearLayout for each incident
                LinearLayout verticalLayout = new LinearLayout(context);
                verticalLayout.setOrientation(LinearLayout.VERTICAL);

                // Add the incident information TextView to the vertical layout
                verticalLayout.addView(incidentTextView);

                // Add some space between incidents (adjust margin values as needed)
                LinearLayout.LayoutParams verticalLayoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                verticalLayout.setLayoutParams(verticalLayoutParams);


                // Create a horizontal LinearLayout for images
                LinearLayout horizontalLayout = new LinearLayout(context);
                horizontalLayout.setOrientation(LinearLayout.VERTICAL);

                // Create an ImageView for each photo and add it to the layout
                for (String photoUrl : photos) {
                    ImageView photoImageView = new ImageView(context);

                    // Set fixed dimensions for each ImageView
                    int imageSizeInDp = 400; // Adjust as needed
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams
                            (imageSizeInDp, imageSizeInDp);
                                /*LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT)*/


                    photoImageView.setLayoutParams(layoutParams);


                    if (context instanceof Activity) {
                        Activity activity = (Activity) context;
                        if (!activity.isDestroyed()) {
                            // Load image using Glide
                            Glide.with(context)
                                    .load(photoUrl)
                                    .into(photoImageView);
                        }
                    }

                    // Add the ImageView to the horizontal layout
                    horizontalLayout.addView(photoImageView);
                }
                //incidentLayout.addView(incidentTextView);
                // Add the horizontal layout to the vertical layout
                verticalLayout.addView(horizontalLayout);

                // Load the first photo using Glide
                incidentLayout.addView(verticalLayout);
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private static void createVerifyButton(DataSnapshot sortedIncidentSnapshot, LinearLayout incidentLayout,
                                           DatabaseReference verifiedRef, Context context) {

        List<String> keysList = new ArrayList<>();

        // Assuming "keys" is a child under sortedIncidentSnapshot
        DataSnapshot keysSnapshot = sortedIncidentSnapshot.child("keys");

        for (DataSnapshot keySnapshot : keysSnapshot.getChildren()) {
            String key = keySnapshot.getValue(String.class);
            keysList.add(key);
        }


        Button verifyButton = new Button(context);
        verifyButton.setText("VERIFY AND SEND EMERGENCY ALERT MESSAGE");
        verifyButton.setTextColor(Color.BLACK);
        verifyButton.setBackgroundColor(Color.rgb(0, 102, 51));
        verifyButton.setOnClickListener(v -> {
            // Build a confirmation dialog
            new AlertDialog.Builder(context)
                    .setTitle("Verify Incident")
                    .setMessage("Are you sure you want to verify this alert and send an emergency alert message?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        // User clicked Yes, perform verification logic
                        // User clicked Yes, delete the sorted incident along with the users' incidents connected to them
                        sortedIncidentSnapshot.getRef().child("status").setValue("verified", (databaseError, databaseReference) -> {
                            if (databaseError == null) {
                                // The update was successful, proceed with saving the incident
                                System.out.println("Verify status");
                                verifiedRef.push().setValue(sortedIncidentSnapshot.getValue(Incident.class));

                                DatabaseReference sortedIncidentRef = sortedIncidentSnapshot.getRef();
                                sortedIncidentRef.removeValue();

                                // Remove incidents based on keys
                                DatabaseReference incidentsRef = FirebaseDatabase.getInstance().getReference().child("incidents");
                                for (String key : keysList) {
                                    incidentsRef.child(key).removeValue();
                                }
                                // Show a Toast indicating verification
                                Toast.makeText(context, "Incident Verified and Alert Sent", Toast.LENGTH_SHORT).show();

                            } else {
                                // There was an error, handle it accordingly
                                Log.e(TAG, "Error updating status", databaseError.toException());
                            }
                        });
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> {
                        // User clicked Cancel, do nothing
                    })
                    .show();
        });
        // Add Verify button to the incident layout
        incidentLayout.addView(verifyButton);
    }

    @SuppressLint("SetTextI18n")
    private static void createDeleteButton(DataSnapshot sortedIncidentSnapshot, LinearLayout incidentLayout,
                                           Context context) {
        List<String> keysList = new ArrayList<>();

        // Assuming "keys" is a child under sortedIncidentSnapshot
        DataSnapshot keysSnapshot = sortedIncidentSnapshot.child("keys");

        for (DataSnapshot keySnapshot : keysSnapshot.getChildren()) {
            String key = keySnapshot.getValue(String.class);
            keysList.add(key);
        }

        Button deleteButton = new Button(context);
        deleteButton.setText("Delete");
        deleteButton.setBackgroundColor(Color.rgb(204, 0, 0));
        deleteButton.setOnClickListener(v -> {

            // Build a confirmation dialog
            new AlertDialog.Builder(context)
                    .setTitle("Delete Incident")
                    .setMessage("Are you sure you want to delete this incident?")
                    .setPositiveButton("Yes", (dialog, which) -> {

                        // User clicked Yes, delete the sorted incident along with the users' incidents connected to them
                        DatabaseReference sortedIncidentRef = sortedIncidentSnapshot.getRef();
                        sortedIncidentRef.removeValue();

                        // Remove incidents based on keys
                        DatabaseReference incidentsRef = FirebaseDatabase.getInstance().getReference().child("incidents");
                        for (String key : keysList) {
                            incidentsRef.child(key).removeValue();
                        }

                        // Show a Toast indicating verification
                        Toast.makeText(context, "Incident has been successfully deleted", Toast.LENGTH_SHORT).show();

                        // Optionally remove the incident layout from its parent layout
                        ((ViewGroup) incidentLayout.getParent()).removeView(incidentLayout);
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> {
                        // User clicked Cancel, do nothing
                    })
                    .show();
        });
        // Add Delete button to the incident layout
        incidentLayout.addView(deleteButton);
    }

    public static void seeVerifiedIncidents(DatabaseReference verifiedRef, LinearLayout scrollViewLayout, Context context) {
        verifiedRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                scrollViewLayout.removeAllViews(); // Clear existing views

                if (dataSnapshot.exists()) { // Verified incidents exist

                    for (DataSnapshot incidentSnapshot : dataSnapshot.getChildren()) {
                        // Create a vertical LinearLayout for each incident
                        LinearLayout incidentLayout = new LinearLayout(context);
                        incidentLayout.setOrientation(LinearLayout.VERTICAL);
                        incidentLayout.setLayoutParams(new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                        ));
                        incidentLayout.setBackgroundColor(Color.rgb(32, 32, 32));

                        // Add incident information to the layout
                        addVerifiedIncidentInfoToLayout(incidentSnapshot, incidentLayout, context);

                        // Add the incident layout to the linear layout
                        scrollViewLayout.addView(incidentLayout);
                    }
                } else { // No Verified Incidents
                    TextView noIncidentsTextView = new TextView(context);
                    noIncidentsTextView.setText("No verified incidents to show.");
                    noIncidentsTextView.setTextColor(Color.BLACK);
                    scrollViewLayout.addView(noIncidentsTextView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors here
            }
        });
    }

    private static void addVerifiedIncidentInfoToLayout(DataSnapshot incidentSnapshot, LinearLayout incidentLayout, Context context) {

        Incident incident = incidentSnapshot.getValue(Incident.class);

        if (incident != null) {
            // Create a TextView for each incident and append it to the ScrollView
            TextView incidentTextView = new TextView(context);

            incidentTextView.setText(new StringBuilder().append("Number of Submissions: ").append(incident.getSubNumber()).
                    append("\nComments: ").append(String.join(", ", incident.getComments())).
                    append("\nLocations: ").append(String.join(", ", incident.getLocations())).
                    append("\nTimestamps: ").append(String.join(", ", incident.getTimestamps())).
                    append("\n\nStatus: ").append("verified").toString());

            incidentTextView.setTextColor(Color.WHITE);
            incidentTextView.setBackgroundColor(Color.BLACK);

            // Assuming photos is a List<String> in your Incident class
            List<String> photos = incident.getPhotos();

            if (photos != null && !photos.isEmpty()) {

                // Create a vertical LinearLayout for each incident
                LinearLayout verticalLayout = new LinearLayout(context);
                verticalLayout.setOrientation(LinearLayout.VERTICAL);

                // Add the incident information TextView to the vertical layout
                verticalLayout.addView(incidentTextView);

                // Add some space between incidents (adjust margin values as needed)
                LinearLayout.LayoutParams verticalLayoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                verticalLayout.setLayoutParams(verticalLayoutParams);


                // Create a horizontal LinearLayout for images
                LinearLayout horizontalLayout = new LinearLayout(context);
                horizontalLayout.setOrientation(LinearLayout.VERTICAL);

                // Create an ImageView for each photo and add it to the layout
                for (String photoUrl : photos) {
                    ImageView photoImageView = new ImageView(context);

                    // Set fixed dimensions for each ImageView
                    int imageSizeInDp = 400; // Adjust as needed
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams
                            (imageSizeInDp, imageSizeInDp);
                                /*LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT)*/


                    photoImageView.setLayoutParams(layoutParams);

                    if (context instanceof Activity) {
                        Activity activity = (Activity) context;
                        if (!activity.isDestroyed()) {
                            // Load image using Glide
                            Glide.with(context)
                                    .load(photoUrl)
                                    .into(photoImageView);
                        }

                        // Add the ImageView to the horizontal layout
                        horizontalLayout.addView(photoImageView);
                    }

                }
                // Add the horizontal layout to the vertical layout
                verticalLayout.addView(horizontalLayout);

                // Load the first photo using Glide
                incidentLayout.addView(verticalLayout);
            }
        }
    }
}