package com.iramml.uberclone.Service;

import android.content.Intent;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.iramml.uberclone.Activities.CustommerCall;
import com.iramml.uberclone.Model.Pickup;

public class firebaseMessaging extends FirebaseMessagingService{
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if(remoteMessage.getNotification().getTitle().equals("Pickup")){
            Pickup pickup=new Gson().fromJson(remoteMessage.getNotification().getBody(), Pickup.class);
            Intent intent=new Intent(getBaseContext(), CustommerCall.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("lat", pickup.getLastLocation().latitude);
            intent.putExtra("lng", pickup.getLastLocation().longitude);
            intent.putExtra("rider", pickup.getID());
            intent.putExtra("token", pickup.getToken().getToken());
            startActivity(intent);
        }

    }
}
