package com.example.smartalert;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.google.firebase.database.Query;
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
import java.util.Set;

public class EmployeeAllFloodIncidentsActivity extends AppCompatActivity {

    private Switch sortFloodIncidentsSwitch;
    private LinearLayout scrollViewLayout;
    private ScrollView scrollView;
    private DatabaseReference incidentsRef, sortedIncidentsRef;
    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_flood_incidents);

        database = FirebaseDatabase.getInstance();
        sortFloodIncidentsSwitch = findViewById(R.id.switch3);
        scrollViewLayout = findViewById(R.id.scrollViewLayout3);
        scrollView = findViewById(R.id.scrollview3);

        scrollView.setBackgroundColor(Color.TRANSPARENT);

        CreateIncidentsLayout();
    }

    public void CreateIncidentsLayout() {
        // Get a reference to the database
        incidentsRef = FirebaseDatabase.getInstance().getReference().child("incidents");

        Query fireIncidentsQuery = incidentsRef.orderByChild("type").equalTo("Flood");
        fireIncidentsQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                scrollViewLayout.removeAllViews(); // Clear existing views

                if (dataSnapshot.exists()) {
                    // Flood incidents exist
                    for (DataSnapshot incidentSnapshot : dataSnapshot.getChildren()) {
                        // Create a vertical LinearLayout for each incident
                        LinearLayout incidentLayout = new LinearLayout(EmployeeAllFloodIncidentsActivity.this);
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
                } else { // No Flood incidents
                    TextView noIncidentsTextView = new TextView(EmployeeAllFloodIncidentsActivity.this);
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
        //String type = incidentSnapshot.child("type").getValue(String.class);
        String location = incidentSnapshot.child("location").getValue(String.class);
        String timestamp = incidentSnapshot.child("timestamp").getValue(String.class);
        String userEmail = incidentSnapshot.child("userEmail").getValue(String.class);
        String comment = incidentSnapshot.child("comment").getValue(String.class);
        String image = incidentSnapshot.child("image").getValue(String.class);

        // Display incident information using TextViews
        TextView typeTextView = new TextView(this);
        //typeTextView.setText("Type: " + type);

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
        boolean isSwitchOn = sortFloodIncidentsSwitch.isChecked();
        sortedIncidentsRef = database.getReference("SortedIncidents/Flood");
        //incidentsRef = database.getReference().child("incidents");

        // Perform actions based on the Switch state
        if (isSwitchOn) { // Switch is ON
            sortedIncidentsRef.removeValue();
            System.out.println("Switch (sort) is on!");

            SortFloodIncidents();
            removeDuplicatesInSortedIncidents();
            CreateSortFloodIncidentsLayout();

            // Switch is ON, show a Toast message
            showToast("Sorting is ON");
        }
        else {// Switch is OFF
            System.out.println("Switch (sort) is off!"); // so all fire incidents
            showToast("Sorting is OFF");

            CreateIncidentsLayout();
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void SortFloodIncidents() {
        incidentsRef.orderByChild("type").equalTo("Flood").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                processAndStoreFloodIncidents(dataSnapshot);
                removeDuplicatesInSortedIncidents();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Error fetching data", databaseError.toException());
            }
        });

    }

    private void processAndStoreFloodIncidents(DataSnapshot dataSnapshot) {

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

                if ((isWithin24Hours(timestamp, timestampInner) /*&& (isWithin80Km(location, locationInner)))*/)) {
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


            saveDataInSortedIncidentsFlood(incident);
        }
    }

    private boolean isWithin24Hours(String prevTimestamp, String timestamp) {
        try {
            // Ορίζουμε το format του timestamp
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy-HH-mm-ss", Locale.getDefault());

            // Μετατρέπουμε τα timestamp σε αντικείμενα Date
            Date incidentDate1 = dateFormat.parse(prevTimestamp);
            Date incidentDate2 = dateFormat.parse(timestamp);

            // Ελέγχουμε αν τα περιστατικά είναι σε διαφορετικές ημερομηνίες
            if (incidentDate1.getDate() != incidentDate2.getDate() || incidentDate1.getMonth() != incidentDate2.getMonth() || incidentDate1.getYear() != incidentDate2.getYear()) {
                return false;
            }

            // Υπολογίζουμε τη διάφορα σε ώρες
            long diffInHours = Math.abs(incidentDate1.getTime() - incidentDate2.getTime()) / (60 * 60 * 1000);

            // Ελέγχουμε αν η διάφορα είναι μικρότερη από 24
            return diffInHours < 24;
        } catch (ParseException e) {
            e.printStackTrace();
            return false; // Σε περίπτωση σφάλματος επιστρέφουμε false
        }
    }

    private void saveDataInSortedIncidentsFlood(Incident incident) {

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

    private void removeDuplicatesInSortedIncidents() {
        DatabaseReference sortedIncidentsRef = database.getReference("SortedIncidents/Flood");

        sortedIncidentsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Set<List<String>> uniqueKeyLists = new HashSet<>();

                for (DataSnapshot incidentSnapshot : dataSnapshot.getChildren()) {
                    List<String> incidentKeys = new ArrayList<>();
                    for (DataSnapshot keySnapshot : incidentSnapshot.child("keys").getChildren()) {
                        incidentKeys.add(keySnapshot.getValue(String.class));
                    }

                    if (!uniqueKeyLists.contains(incidentKeys)) {
                        // Add the key list to the set
                        System.out.println("yessss");
                        uniqueKeyLists.add(incidentKeys);
                    } else {
                        // Duplicate found, remove it
                        incidentSnapshot.getRef().removeValue();
                    }
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


    // CREATE SORT FIRE INCIDENTS LAYOUT
    public void CreateSortFloodIncidentsLayout(){
        // Get a reference to the database
        sortedIncidentsRef = FirebaseDatabase.getInstance().getReference().child("SortedIncidents/Flood");
        //sortedIncidentsRef.removeValue();

        sortedIncidentsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                scrollViewLayout.removeAllViews(); // Clear existing views

                if (dataSnapshot.exists()) {
                    for (DataSnapshot sortedIncidentSnapshot : dataSnapshot.getChildren()) {
                        // Create a vertical LinearLayout for each incident
                        LinearLayout incidentLayout = new LinearLayout(EmployeeAllFloodIncidentsActivity.this);
                        incidentLayout.setOrientation(LinearLayout.VERTICAL);
                        incidentLayout.setLayoutParams(new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                        ));
                        incidentLayout.setBackgroundColor(Color.rgb(32, 32, 32));

                        // Add incident information to the layout
                        addSortedIncidentInfoToLayout(sortedIncidentSnapshot, incidentLayout);

                        // Create Verify and Delete buttons
                        createVerifyButton(sortedIncidentSnapshot, incidentLayout);
                        createDeleteButton(sortedIncidentSnapshot, incidentLayout);

                        // Add the incident layout to the linear layout
                        scrollViewLayout.addView(incidentLayout);
                    }
                }
                else { // No Flood incidents
                        TextView noIncidentsTextView = new TextView(EmployeeAllFloodIncidentsActivity.this);
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

    private void addSortedIncidentInfoToLayout(DataSnapshot sortedIncidentSnapshot, LinearLayout incidentLayout) {

        Incident incident = sortedIncidentSnapshot.getValue(Incident.class);

        if (incident != null) {
            System.out.println(incident.getLocations());
            // Create a TextView for each incident and append it to the ScrollView
            TextView incidentTextView = new TextView(EmployeeAllFloodIncidentsActivity.this);
            /*"\nPhotos: " + incident.getPhotos() +*/
            incidentTextView.setText(/*"Type: " + incident.getType() +
                                "\n*/new StringBuilder().append("Number of Submissions: ").append(incident.getSubNumber()).
                    append("\nComments: ").append(String.join(", ", incident.getComments())).
                    append("\nLocations: ").append(String.join(", ", incident.getLocations())).
                    append("\nTimestamps: ").append(String.join(", ", incident.getTimestamps())).toString());

            incidentTextView.setTextColor(Color.WHITE);
            incidentTextView.setBackgroundColor(Color.BLACK);


            // Assuming photos is a List<String> in your Incident class
            List<String> photos = incident.getPhotos();

            if (photos != null && !photos.isEmpty()) {

                // Create a vertical LinearLayout for each incident
                LinearLayout verticalLayout = new LinearLayout(EmployeeAllFloodIncidentsActivity.this);
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
                LinearLayout horizontalLayout = new LinearLayout(EmployeeAllFloodIncidentsActivity.this);
                horizontalLayout.setOrientation(LinearLayout.HORIZONTAL);

                // Create an ImageView for each photo and add it to the layout
                for (String photoUrl : photos) {
                    ImageView photoImageView = new ImageView(EmployeeAllFloodIncidentsActivity.this);

                    // Set fixed dimensions for each ImageView
                    int imageSizeInDp = 400; // Adjust as needed
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams
                            (imageSizeInDp, imageSizeInDp);
                                /*LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT)*/


                    photoImageView.setLayoutParams(layoutParams);

                    if (!isDestroyed()) {
                        // Load the photo using Glide
                        Glide.with(EmployeeAllFloodIncidentsActivity.this)
                                .load(photoUrl)
                                .into(photoImageView); }

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

    private void createVerifyButton(DataSnapshot sortedIncidentSnapshot, LinearLayout incidentLayout) {
        Button verifyButton = new Button(this);
        verifyButton.setText("VERIFY AND SEND EMERGENCY ALERT MESSAGE");
        verifyButton.setTextColor(Color.BLACK);
        verifyButton.setBackgroundColor(Color.rgb(0,102,51));
        verifyButton.setOnClickListener(v -> {
            // Build a confirmation dialog
            new AlertDialog.Builder(this)
                    .setTitle("Verify Incident")
                    .setMessage("Are you sure you want to verify this alert and send an emergency alert message?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        // User clicked Yes, perform verification logic
                        // For example, you can update the incident status in the database
                        // incidentSnapshot.getRef().child("status").setValue("verified");

                        // Show a Toast indicating verification
                        Toast.makeText(this, "Incident Verified and Alert Sent", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> {
                        // User clicked Cancel, do nothing
                    })
                    .show();
        });

        // Add Verify button to the incident layout
        incidentLayout.addView(verifyButton);

    }

    private void createDeleteButton(DataSnapshot sortedIncidentSnapshot, LinearLayout incidentLayout) {
        List<String> keysList = new ArrayList<>();

        // Assuming "keys" is a child under sortedIncidentSnapshot
        DataSnapshot keysSnapshot = sortedIncidentSnapshot.child("keys");

        for (DataSnapshot keySnapshot : keysSnapshot.getChildren()) {
            String key = keySnapshot.getValue(String.class);
            keysList.add(key);
        }

        Button deleteButton = new Button(this);
        deleteButton.setText("Delete");
        deleteButton.setBackgroundColor(Color.rgb(204,0,0));
        deleteButton.setOnClickListener(v -> {

            // Build a confirmation dialog
            new AlertDialog.Builder(this)
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
                        Toast.makeText(this, "Incident has been successfully deleted", Toast.LENGTH_SHORT).show();

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
}
