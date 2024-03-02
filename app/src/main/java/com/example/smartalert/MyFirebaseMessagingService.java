package com.example.smartalert;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String CHANNEL_ID = "IncidentsNear";
    private static final String CHANNEL_NAME = "Incident Near";
    private static final String CHANNEL_DESCRIPTION = "Show alert for near incidents";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if (remoteMessage.getNotification() != null) {
            System.out.println("On (remote) message received");
            String title = remoteMessage.getNotification().getTitle();
            System.out.println("Title:" + title);
            String body = remoteMessage.getNotification().getBody();
            System.out.println("Body: " + body);

            // Display notification
            sendNotification(title, body);
        }
    }

    void sendNotification(String title, String body) {
        // Create an intent to open your activity when notification is clicked
        Intent intent = new Intent(this, UserHomePage.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_IMMUTABLE);

        // Create a notification channel for Android Oreo and higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(CHANNEL_DESCRIPTION);

            // Configure notification channel for emergency messages
            channel.enableLights(true);  // Enable notification light
            channel.setLightColor(Color.RED);  // Set notification light color to red
            channel.enableVibration(true);  // Enable vibration
            channel.setVibrationPattern(new long[]{0, 1000, 500, 1000});  // Set vibration pattern

            // Set custom notification sound
            AudioAttributes attributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();
            Uri soundUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.sound1);  // Replace "your_sound_file" with the name of your sound file
            channel.setSound(soundUri, attributes);  // Set custom notification sound

            // Register the channel with the system
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        // Create a Bitmap object from the drawable resource
        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.ambulance_lights);

        // Create a notification builder
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ambulance_lights) // Set the icon
                .setLargeIcon(largeIcon) // Set the picture
                .setContentTitle(title) // Set the title
                .setContentText(body) // Set the body
                .setAutoCancel(true) // Automatically remove the notification when tapped
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setFullScreenIntent(pendingIntent, true); // Set full-screen intent

        //.setContentIntent(pendingIntent); // Set the intent to open your activity

        // Get the notification manager
        NotificationManager notificationManager =
             (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        //NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);


        // Display the notification
       /* if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }*/
        notificationManager.notify(0, notificationBuilder.build());
    }
}

