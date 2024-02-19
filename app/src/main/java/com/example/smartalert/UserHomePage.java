package com.example.smartalert;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
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
    private FirebaseUser currentUser;

    //private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private static final long LOCATION_UPDATE_INTERVAL = 20 * 60 * 1000; // 20 minutes
    private static final long LOCATION_FASTEST_UPDATE_INTERVAL = 25 * 60 * 1000; // 25 minutes

    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

    private DatabaseReference locationRef;
    String locationString;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home_page);

        mAuth = FirebaseAuth.getInstance();

        /*locationRef = FirebaseDatabase.getInstance().getReference("users");

        // Initialize fused location client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Create location request
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(LOCATION_UPDATE_INTERVAL);
        locationRequest.setFastestInterval(LOCATION_FASTEST_UPDATE_INTERVAL);

        // Create location callback
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    // Save location to Firebase
                    updateLocationInFirebase(location);
                }
            }
        };

        // Check location permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
        else {
            startLocationUpdates();
        }*/
        // Start the location foreground service
        //startLocationForegroundService();

        /*Intent serviceIntent = new Intent(this, LocationUpdateService.class);
        this.startService(serviceIntent);*/

        //startLocationForegroundService();

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
    }

   /* private void startLocationForegroundService() {
        Intent serviceIntent = new Intent(this, LocationForegroundService.class);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            ContextCompat.startForegroundService(this, serviceIntent);
        } else {
            startService(serviceIntent);
        }
    }*/

    /*private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }*/

    /*private void updateLocationInFirebase(Location location) {

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser!= null && location != null) {
            String userId = currentUser.getUid();
            String email = currentUser.getEmail();

            // Get a reference to the Firebase Realtime Database
            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");

            // Define the query to find the user by email
            usersRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    // Iterate through the results (there should be only one result)
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        // Construct the location string (replace latitude and longitude with actual values)
                        locationString = "Lat: 123.456, Long: 78.910";

                        System.out.println("User snapshot ref: " + userSnapshot.getRef());
                        // Update the location for the specific user in the database
                        userSnapshot.getRef().child("location").setValue(locationString);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle potential errors
                }
            });
        } else {
            // User is not logged in
            // Handle the case when there is no logged-in user
        }
        //System.out.println("Userid is: " + userId);
            // Construct the location string
            /*String locationString = "Lat: " + location.getLatitude() + ", Long: " + location.getLongitude();

            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
            //System.out.println("Users ref: "+ currentUser.getIdToken());
            locationRef.child(userId).child("location").setValue(locationString);*/
        //}*/
   // }

    /*@Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE &&
                grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates();
        } else {
            Log.e(TAG, "Location permission denied");
        }
    }*/

    /*@Override
    protected void onDestroy() {
        super.onDestroy();
        // Stop location updates when the activity is destroyed
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }*/


    // OPTIONS MENU
    public void logout(View view){
        mAuth.signOut();

        finish();
        Intent intent = new Intent(this, StartUpActivity.class);
        startActivity(intent);
    }

    public void upload(View view){
        Intent intent = new Intent(this, UserNewIncident.class);
        intent.putExtra("location",locationString);
        startActivity(intent);
    }

    public void incidents_statistics(View view){
        Intent intent = new Intent(this, UserIncidentsStatistics.class);
        startActivity(intent);
    }
}