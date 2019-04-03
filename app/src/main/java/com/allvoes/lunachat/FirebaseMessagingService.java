package com.allvoes.lunachat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.RemoteMessage;

import java.nio.channels.Channel;

public class FirebaseMessagingService extends  com.google.firebase.messaging.FirebaseMessagingService {
    private DatabaseReference mDatabase;
    private FirebaseUser mAuth;


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String mNotifi_title = remoteMessage.getNotification().getTitle();
        String mNotifi_body = remoteMessage.getNotification().getBody();
        String click_action = remoteMessage.getNotification().getClickAction();
        String from_user_ID = remoteMessage.getData().get("from_user_id");
        sendNotification(mNotifi_title,mNotifi_body,click_action,from_user_ID);
    }


    public void sendNotification(String mNotifi_title,String mNotifi_body,String click_action, String from_user_ID){

        Intent i = new Intent(this, ProfileActivity.class);
        i.putExtra("from_user_id",from_user_ID);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,i,PendingIntent.FLAG_UPDATE_CURRENT);
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        String channelId = getString(R.string.default_notification_channel_id);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this,channelId)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(mNotifi_title)
                .setContentText(mNotifi_body)
                .setSound(sound)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        int mNotifi = (int) System.currentTimeMillis();
        NotificationManager mNoMager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(channelId, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        mNoMager.notify(mNotifi,mBuilder.build());

    }


}
