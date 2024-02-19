package com.example.smartalert;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserHomePage extends AppCompatActivity{

    private FirebaseAuth mAuth;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    private String locationString;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home_page);

        mAuth = FirebaseAuth.getInstance();

        // Request location permissions
        requestLocationPermissions();
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
        Intent intent = new Intent(this, UserIncidentsStatistics.class);
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
                        if (locationString != null) {
                            Log.d(TAG, "User's location: " + locationString);
                            // Handle the location data, e.g., pass it to another method or update UI
                        } else {
                            Log.e(TAG, "Location not found for the user with email: " + userEmail);
                            // Handle the case when location data is not found
                        }
                    }
                } else {
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