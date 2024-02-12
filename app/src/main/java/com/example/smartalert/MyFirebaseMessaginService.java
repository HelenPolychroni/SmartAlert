package com.example.smartalert;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessaginService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMessaging";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        // Check if the message contains a notification payload
        if (remoteMessage.getNotification() != null) {
            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();
            Log.d(TAG, "Message Notification Title: " + title);
            Log.d(TAG, "Message Notification Body: " + body);

            // Display the notification
            showNotification(title, body);
        }
    }

    private void showNotification(String title, String body) {
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        String channelId = String.valueOf(R.string.default_notification_channel_id);
        String channelName = "My Channel";
        String channelDescription = "This is my notification channel";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    channelName,
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription(channelDescription);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.drawable.ic_home)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        // Use a unique notification ID
        int notificationId = 1;

        // Show the notification
        notificationManager.notify(notificationId, builder.build());
    }
}
