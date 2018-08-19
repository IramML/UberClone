package com.iramml.uberclone.Service;

import android.content.Intent;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.iramml.uberclone.Activities.CustommerCall;

public class firebaseMessaging extends FirebaseMessagingService{
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        LatLng customerLocation=new Gson().fromJson(remoteMessage.getNotification().getBody(), LatLng.class);

        Intent intent=new Intent(getBaseContext(), CustommerCall.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("lat", customerLocation.latitude);
        intent.putExtra("lng", customerLocation.longitude);
        startActivity(intent);
    }
}
