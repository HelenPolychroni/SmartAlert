package com.example.smartalert;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

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

    public void CreateIncidentsLayout(){
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
                }else { // No Flood incidents
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
        //incidentLayout.addView(typeTextView);
        incidentLayout.addView(locationTextView);
        incidentLayout.addView(timestampTextView);
        incidentLayout.addView(userEmailTextView);
        incidentLayout.addView(imageView);
    }

    public void checkSwitch(View view){
        // Check the state of the Switch when the Button is clicked
        boolean isSwitchOn = sortFloodIncidentsSwitch.isChecked();
        sortedIncidentsRef = database.getReference("SortedIncidents/Fire");

        //incidentsRef = database.getReference().child("incidents");

        // Perform actions based on the Switch state
        if (isSwitchOn) {
            sortedIncidentsRef.removeValue();
            System.out.println("Switch (sort) is on!");

            //CreateSortIncidentsLayout();

            // Switch is ON, show a Toast message
            showToast("Sorting is ON");
            //findAndStoreIncidents();

            // Switch is ON
            // Call method activity to handle this
        } else {
            System.out.println("Switch (sort) is off!"); // so all fire incidents
            showToast("Sorting is OFF");
            // Switch is OFF
            CreateIncidentsLayout();
        }
    }
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}