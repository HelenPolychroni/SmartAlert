package com.example.smartalert;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import com.google.firebase.messaging.FirebaseMessagingService;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    public static void sendNotificationToTopic(String topic, String title, String message) {
        System.out.println("subscribe");
        RemoteMessage notificationMessage = new RemoteMessage.Builder(topic)
                .setMessageId(Integer.toString((int) System.currentTimeMillis()))
                .addData("title", title)
                .addData("message", message)
                .build();

        FirebaseMessaging.getInstance().send(notificationMessage);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Channel Name";
            String description = "Channel Description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("channel_id", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}