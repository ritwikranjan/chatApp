package com.example.chatapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.sql.Timestamp;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class FirebaseNotificationService extends FirebaseMessagingService {

    private static final String CHANNEL_ID_REQUEST = "requests";
    NotificationChannel notificationChannel;
    NotificationManager notificationManager;

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name_request);
            String description = getString(R.string.channel_description_request);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            notificationChannel = new NotificationChannel(CHANNEL_ID_REQUEST, name, importance);
            notificationChannel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            notificationManager = getSystemService(NotificationManager.class);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(notificationChannel);
        }

        String notificationTitle = Objects.requireNonNull(remoteMessage.getNotification( )).getTitle();
        String notificationMessage = remoteMessage.getNotification().getBody();
        String fromUserId = remoteMessage.getData().get("user");
        String notificationId = remoteMessage.getData().get("notification");

        //Tap Intent
        Intent tapIntent = new Intent(this,ProfileActivity.class);
        tapIntent.putExtra("uid",fromUserId);
        //tapIntent.putExtra("acceptVal", 2);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                tapIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        //Accept Intent
        Intent acceptIntent = new Intent(this,ProfileActivity.class);
        acceptIntent.putExtra("uid",fromUserId);
        acceptIntent.putExtra("acceptVal", 1);
        PendingIntent pendingAcceptIntent = PendingIntent.getActivity(
                this,
                0,
                acceptIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        //Decline Intent
        Intent declineIntent = new Intent(this,ProfileActivity.class);
        declineIntent.putExtra("uid",fromUserId);
        declineIntent.putExtra("acceptVal", 0);
        PendingIntent pendingDeclineIntent = PendingIntent.getActivity(
                this,
                0,
                declineIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        assert notificationId != null;
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,CHANNEL_ID_REQUEST)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(notificationTitle)
                .setContentText(notificationMessage)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .addAction(R.drawable.ic_launcher_foreground,"Accept",pendingAcceptIntent)
                .addAction(R.drawable.ic_launcher_foreground,"Decline",pendingDeclineIntent)
                .setAutoCancel(true);

        int hashNotificationId = notificationId.hashCode();

        Log.d("Notification: ", notificationId);

        notificationManager.notify(hashNotificationId,notificationBuilder.build());


    }



}
