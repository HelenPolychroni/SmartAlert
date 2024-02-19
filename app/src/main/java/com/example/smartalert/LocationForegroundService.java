package com.example.smartalert;

import static androidx.core.app.ActivityCompat.startActivityForResult;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

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
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class LocationForegroundService extends Service {

    private static final String TAG = "LocationForegroundServ";

    // Notification channel ID and name
    private static final String CHANNEL_ID = "LocationForegroundServiceChannel";
    private static final String CHANNEL_NAME = "Location Updates";
    private static final long LOCATION_UPDATE_INTERVAL = TimeUnit.MINUTES.toMillis(20); // 20 minutes

    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private Handler handler;
    private String locationString;
    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 100;

    // Constructor
    public LocationForegroundService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG,"on create:");
        requestNotificationPermission();

        // Create the notification channel
        createNotificationChannel();

        // Build the notification and start the service as a foreground service
        startForegroundService();

        initLocationUpdates();
        startLocationUpdates();
        scheduleLocationUpdates();
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null && !notificationManager.areNotificationsEnabled()) {
                Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
                startActivity(intent);
            }
        }
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: ");

        // Start location updates
        // You will implement location update logic here

        // Return START_STICKY to ensure the service restarts if it's killed by the system
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // Method to create the notification channel
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    // Method to start the service as a foreground service with a notification
    private void startForegroundService() {
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Location Updates")
                .setContentText("Service is running in the background")
                .setSmallIcon(R.drawable.ic_home)
                .build();

        startForeground(1, notification);
    }

    private void initLocationUpdates() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(LOCATION_UPDATE_INTERVAL);
        locationRequest.setFastestInterval(LOCATION_UPDATE_INTERVAL);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    updateLocationInFirebase(location);
                }
            }
        };
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    private void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    private void scheduleLocationUpdates() {
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startLocationUpdates();
                scheduleLocationUpdates();
            }
        }, LOCATION_UPDATE_INTERVAL);
    }

    private void updateLocationInFirebase(Location location) {

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
                        String latitude = String.valueOf(location.getLatitude());
                        String longitude = String.valueOf(location.getLongitude());
                        locationString = "Lat: " + latitude + ", Long: " + longitude;

                       // locationString = "Lat: 123.456, Long: 78.910";

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
        }
    }
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
