package com.example.smartalert;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class EmployeeFireIncidentsActivity extends AppCompatActivity {

    private LinearLayout scrollViewLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_fire_incidents);

        scrollViewLayout = findViewById(R.id.ScrollViewLayout2);
        // Get a reference to the database
        DatabaseReference incidentsRef = FirebaseDatabase.getInstance().getReference().child("sorted_incidents");

        incidentsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                scrollViewLayout.removeAllViews(); // Clear existing views

                for (DataSnapshot incidentSnapshot : dataSnapshot.getChildren()) {
                    Incident incident = incidentSnapshot.getValue(Incident.class);

                    if (incident != null) {
                        System.out.println(incident.getLocations());
                        // Create a TextView for each incident and append it to the ScrollView
                        TextView incidentTextView = new TextView(EmployeeFireIncidentsActivity.this);
                        /*"\nPhotos: " + incident.getPhotos() +*/
                        incidentTextView.setText(/*"Type: " + incident.getType() +
                                "\n*/new StringBuilder().append("Number of Submissions: ").append(incident.getSubNumber()).
                                append("\n\nComments:\n").append(String.join(", ", incident.getComments())).
                                append("\n\nLocations:\n").append(String.join(", ", incident.getLocations())).
                                append("\n\nTimestamps:\n").append(String.join(", ", incident.getTimestamps())).toString());

                        incidentTextView.setTextColor(Color.WHITE);
                        incidentTextView.setBackgroundColor(Color.BLACK);

                        //scrollViewLayout.addView(incidentTextView);

                        // Assuming there's an ImageView in your layout with the ID "incidentImageView"
                        //ImageView incidentImageView = findViewById(R.id.incidentImageView);

                        // Assuming photos is a List<String> in your Incident class
                        List<String> photos = incident.getPhotos();

                        if (photos != null && !photos.isEmpty()) {

                            // Create a vertical LinearLayout for each incident
                            LinearLayout verticalLayout = new LinearLayout(EmployeeFireIncidentsActivity.this);
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
                            LinearLayout horizontalLayout = new LinearLayout(EmployeeFireIncidentsActivity.this);
                            horizontalLayout.setOrientation(LinearLayout.HORIZONTAL);

                            // Create an ImageView for each photo and add it to the layout
                            for (String photoUrl : photos) {
                                ImageView photoImageView = new ImageView(EmployeeFireIncidentsActivity.this);

                                // Set fixed dimensions for each ImageView
                                int imageSizeInDp = 400; // Adjust as needed
                                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams
                                        (imageSizeInDp, imageSizeInDp);
                                /*LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT)*/


                                photoImageView.setLayoutParams(layoutParams);

                                // Load the photo using Glide
                                Glide.with(EmployeeFireIncidentsActivity.this)
                                        .load(photoUrl)
                                        .into(photoImageView);

                                // Add the ImageView to the container

                                // Add the ImageView to the horizontal layout
                                horizontalLayout.addView(photoImageView);


                                //scrollViewLayout.addView(photoImageView);
                                scrollViewLayout.setBackgroundColor(Color.BLACK);
                            }

                            // Add the horizontal layout to the vertical layout
                            verticalLayout.addView(horizontalLayout);

                            // Load the first photo using Glide
                            scrollViewLayout.addView(verticalLayout);

                            // Load the first photo using Glide
                            /*Glide.with(EmployeeFireIncidentsActivity.this)
                                    .load(photos.get(0)) // Assuming the first photo URL
                                    .into(incidentImageView);
                        }
                        scrollViewLayout.addView(incidentTextView);*/

                            // Create Notify Button
                            Button notifyButton = new Button(EmployeeFireIncidentsActivity.this);
                            notifyButton.setBackgroundColor(Color.RED);
                            notifyButton.setText("VERIFY AND SEND EMERGENCY ALERT MESSAGE");
                            notifyButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // Handle Notify button click
                                    // You can implement the notification logic here
                                }
                            });

                            // Create Delete Button
                            Button deleteButton = new Button(EmployeeFireIncidentsActivity.this);
                            deleteButton.setText("Delete");
                            deleteButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // Handle Delete button click
                                    // You can implement the deletion logic here
                                }
                            });

                            // Add buttons to the layout
                            scrollViewLayout.addView(notifyButton);
                            scrollViewLayout.addView(deleteButton);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors here
            }
        });
    }
}