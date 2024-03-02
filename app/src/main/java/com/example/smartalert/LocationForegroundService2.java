package com.example.smartalert;

import static android.content.Intent.getIntent;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
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
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;
public class LocationForegroundService2 extends Service{

    private static final String TAG = "LocationForegroundServ";
    private static final String CHANNEL_ID = "LocationForegroundServiceChannel";
    private static final int NOTIFICATION_ID = 1;

    private static final long LOCATION_UPDATE_INTERVAL = TimeUnit.MINUTES.toMillis(4); // 20 minutes

    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

    private DatabaseReference locationRef;
    private String currentLocationString;
    boolean isEnglishSelected;

    @Override
    public void onCreate() {
        super.onCreate();

        SharedPreferences sharedPreferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        isEnglishSelected = sharedPreferences.getBoolean("isEnglishSelected", true);

        locationRef = FirebaseDatabase.getInstance().getReference("users");

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

        System.out.println("is english selected oncreate: " + isEnglishSelected);
        createNotificationChannel();
        startForegroundService();
        startLocationUpdates();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        isEnglishSelected = intent.getBooleanExtra("isEnglishSelected", true);
        System.out.println("isEnglishSelected fetching from home page: " + isEnglishSelected);
        // Use the boolean value as needed

        // Αποθηκεύστε την τιμή της μεταβλητής isEnglishSelected στο SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isEnglishSelected", isEnglishSelected);
        editor.apply();


        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            System.out.println("create notif channel lang engl: " + isEnglishSelected);
            CharSequence name = isEnglishSelected ? "Location Updates" :
                    "Ενημέρωση Τοποθεσίας";
            String description = isEnglishSelected ? "Service is running in the background" :
                    "Η υπηρεσία εκτελείται στο παρασκήνιο";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private void startForegroundService() {
        System.out.println("start foregr service lang engl: " + isEnglishSelected);

        Intent notificationIntent = new Intent(this, UserHomePage.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(isEnglishSelected ? "Location Updates" :
                        "Ενημέρωση Τοποθεσίας")
                .setContentText(isEnglishSelected ? "Service is running in the background" :
                        "Η υπηρεσία εκτελείται στο παρασκήνιο")
                .setSmallIcon(R.drawable.rotate)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    private void updateLocationInFirebase(Location location) {

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null && location != null) {
            String userId = currentUser.getUid();
            String email = currentUser.getEmail();

            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");

            usersRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        String latitude = String.valueOf(location.getLatitude());
                        String longitude = String.valueOf(location.getLongitude());
                        currentLocationString = "Lat: " + latitude + ", Long: " + longitude;

                        userSnapshot.getRef().child("location").setValue(currentLocationString)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        // Broadcast the updated location string
                                        Intent intent = new Intent("LocationUpdate");
                                        intent.putExtra("location", currentLocationString);
                                        sendBroadcast(intent);

                                        Log.d(TAG, "Location update in Firebase successful");
                                        //Toast.makeText(LocationForegroundService2.this, "Location update in Firebase successful", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Log.e(TAG, "Failed to update location in Firebase: " + task.getException());
                                        //Toast.makeText(LocationForegroundService2.this, "Failed to update location in Firebase", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e(TAG, "Database error: " + databaseError.getMessage());
                    Toast.makeText(LocationForegroundService2.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });
        } else {
            // Handle the case when the user is not logged in or location is null
            Log.e(TAG, "User is not logged in or location is null");
            Toast.makeText(LocationForegroundService2.this, "User is not logged in or location is null", Toast.LENGTH_SHORT).show();
        }
    }
}

