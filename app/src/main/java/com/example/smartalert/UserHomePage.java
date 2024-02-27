package com.example.smartalert;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.Locale;

public class UserHomePage extends AppCompatActivity{

    private FirebaseAuth mAuth;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    private String locationString;
    private TextView greetingTextView;
    private DatabaseReference usersRef;

    private Button button3, button4, button5;
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

        setContentView(R.layout.activity_user_home_page);

        mAuth = FirebaseAuth.getInstance();

        greetingTextView = findViewById(R.id.welcomemsg);


        if (ThemeUtils.isDarkTheme(this)) { // Dark mode
            greetingTextView.setTextColor(getResources().getColor(R.color.white));

            button3 = findViewById(R.id.button3);
            button4 = findViewById(R.id.button4);
            button5 = findViewById(R.id.button5);

            button3.setTextColor(getResources().getColor(R.color.white));
            button4.setTextColor(getResources().getColor(R.color.white));
            button5.setTextColor(getResources().getColor(R.color.white));
        }

        // Get the current user's email address
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String email = currentUser.getEmail();

            // Reference to the Firebase database
            usersRef = FirebaseDatabase.getInstance().getReference("users");

            // Query the database to find the user's full name by email
            usersRef.orderByChild("email").equalTo(email)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                // Retrieve the full name from the database
                                String fullName = dataSnapshot.getChildren().iterator().next().child("fullname").getValue(String.class);
                                if (fullName != null) {
                                    if (isEnglishSelected)
                                        // Update the greeting text view with the full name
                                        greetingTextView.setText("Hello "+ fullName.split(" ")[0]+
                                            ",\nchoose the actions you want to perform");
                                    else
                                        greetingTextView.setText("Γεια σου "+ fullName.split(" ")[0]+
                                                ",\nεπέλεξε τις ενέργειες που επιθυμείς να εκτελέσεις");

                                    // Make the root ConstraintLayout visible
                                    ConstraintLayout rootLayout = findViewById(R.id.userHome);
                                    rootLayout.setVisibility(View.VISIBLE);
                                } else {
                                    // Handle the case when the full name is not found
                                    //Toast.makeText(YourActivity.this, "Full name not found", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                // Handle the case when the user is not found
                                //Toast.makeText(YourActivity.this, "User not found", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Handle the error
                            //Toast.makeText(YourActivity.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            // Handle the case when the current user is null (not authenticated)
            Toast.makeText(this, "No authenticated user", Toast.LENGTH_SHORT).show();
        }

        // Request location permissions
        requestLocationPermissions();
    }

    private void updateLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }

    private void requestLocationPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            // Permissions already granted, start the service
            startLocationForegroundService();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Location permissions granted, start the service
                startLocationForegroundService();
            } else {
                // Location permissions denied, handle accordingly
                // You may show a message to the user or disable location-related functionality
            }
        }
    }

    private void startLocationForegroundService() {
        Intent serviceIntent = new Intent(this, LocationForegroundService2.class);
        startService(serviceIntent);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            findUserByEmailAndRetrieveLocation(currentUser.getEmail());
        }
    }

    // OPTIONS MENU
    public void logout(View view){
        mAuth.signOut();

        // Finish the current activity
        finish();

        Intent intent = new Intent(this, StartUpActivity.class);
        startActivity(intent);
    }

    public void upload(View view) {

            if (locationString != null) {  // Check if the location string is available
                System.out.println("Location string: " + locationString);
                Intent intent = new Intent(this, UserNewIncident.class);
                intent.putExtra("location", locationString);
                startActivity(intent);
            }
            else Log.e(TAG, "Location string is null");
    }

    public void incidents_statistics(View view){
        Intent intent = new Intent(this, UserIncidentsStatisticsPie.class);
        startActivity(intent);
    }

    private void findUserByEmailAndRetrieveLocation(String userEmail) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");

        // Query the database to find the user with the specified email
        usersRef.orderByChild("email").equalTo(userEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // User with the specified email found
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        // Retrieve the location from the user node
                        locationString = userSnapshot.child("location").getValue(String.class);
                        //firstName = Objects.requireNonNull(userSnapshot.child("fullname").getValue(String.class)).split("")[0];
                        if (locationString != null) {
                            Log.d(TAG, "User's location: " + locationString);
                            // Handle the location data, e.g., pass it to another method or update UI
                        } else {
                            Log.e(TAG, "Location not found for the user with email: " + userEmail);
                            // Handle the case when location data is not found
                        }
                    }
                }
                else {
                    Log.e(TAG, "User with email " + userEmail + " not found");
                    // Handle the case when user with the specified email is not found
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle potential errors, such as network issues or permission denied
                Log.e(TAG, "Database error: " + databaseError.getMessage());
            }
        });
    }
}