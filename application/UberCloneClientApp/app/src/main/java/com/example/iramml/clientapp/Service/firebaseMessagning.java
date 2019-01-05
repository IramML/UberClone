package com.example.iramml.clientapp.Service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.example.iramml.clientapp.Activities.Home;
import com.example.iramml.clientapp.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

public class firebaseMessagning extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(final RemoteMessage remoteMessage) {
        if(remoteMessage.getNotification().getTitle().equals("Cancel")) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(firebaseMessagning.this,""+remoteMessage.getNotification().getBody(), Toast.LENGTH_SHORT).show();

                }
            });
        }else if(remoteMessage.getNotification().getTitle().equals("Arrived")) {
            try {
                showArrivedNotification(remoteMessage.getNotification().getBody());
            }catch (Exception ex) {

            }
        }
    }
    private void showArrivedNotification(String body) {
        try {
            PendingIntent contentIntent = PendingIntent.getActivity(getBaseContext(),
                    0,new Intent(),PendingIntent.FLAG_ONE_SHOT);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(getBaseContext());
            builder.setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_LIGHTS|Notification.DEFAULT_SOUND)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setContentTitle("Arrived")
                    .setContentText(body)
                    .setContentIntent(contentIntent);
            NotificationManager manager = (NotificationManager)getBaseContext().getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify(1,builder.build());

            Intent intent = new Intent(this, Home.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        catch (Exception ex)
        {
            Toast.makeText(this, ""+ex, Toast.LENGTH_SHORT).show();
        }



    }
}
