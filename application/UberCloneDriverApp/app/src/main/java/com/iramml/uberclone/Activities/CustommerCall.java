package com.iramml.uberclone.Activities;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.iramml.uberclone.Common.Common;
import com.iramml.uberclone.GoogleAPIRoutesRequest.GoogleMapsAPIRequest;
import com.iramml.uberclone.Interfaces.IFCMService;
import com.iramml.uberclone.Interfaces.googleAPIInterface;
import com.iramml.uberclone.Messages.Message;
import com.iramml.uberclone.Messages.Messages;
import com.iramml.uberclone.Model.FCMResponse;
import com.iramml.uberclone.Model.Notification;
import com.iramml.uberclone.Model.Sender;
import com.iramml.uberclone.Model.Token;
import com.iramml.uberclone.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustommerCall extends AppCompatActivity {
    TextView tvTime, tvAddress, tvDistance;
    Button btnAccept, btnDecline;
    MediaPlayer mediaPlayer;

    googleAPIInterface mService;
    IFCMService mFCMService;
    String riderID, token;

    double lat, lng;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custommer_call);
        mService=Common.getGoogleAPI();
        mFCMService=Common.getFCMService();
        tvTime=findViewById(R.id.tvTime);
        tvDistance=findViewById(R.id.tvDistance);
        tvAddress=findViewById(R.id.tvAddress);
        btnDecline=findViewById(R.id.btnDecline);
        btnAccept=findViewById(R.id.btnAccept);

        mediaPlayer=MediaPlayer.create(this, R.raw.ringtone);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        if (getIntent()!=null){
            lat=getIntent().getDoubleExtra("lat", -1.0);
            lng=getIntent().getDoubleExtra("lng", -1.0);
            riderID=getIntent().getStringExtra("rider");
            token=getIntent().getStringExtra("token");
            getDirection(lat, lng);
        }else finish();
        btnDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(riderID)) cancelRequest(riderID);
            }
        });
        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(CustommerCall.this, DriverTracking.class);
                intent.putExtra("lat", lat);
                intent.putExtra("lng", lng);
                intent.putExtra("riderID", riderID);
                intent.putExtra("token", token);
                startActivity(intent);
                finish();
            }
        });
    }

    private void cancelRequest(String riderID) {
        Token token=new Token(riderID);

        Notification notification=new Notification("Cancel", "Driver has cancelled your request");
        Sender sender=new Sender(token.getToken(), notification);

        mFCMService.sendMessage(sender).enqueue(new Callback<FCMResponse>() {
            @Override
            public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
                if(response.body().success==1){
                    Message.message(getApplicationContext(), Messages.CANCELLED);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<FCMResponse> call, Throwable t) {

            }
        });
    }

    private void getDirection(double lat, double lng){

        final String requestApi;

        try{
            requestApi="https://maps.googleapis.com/maps/api/directions/json?mode=driving&" +
                    "transit_routing_preference=less_driving&origin="+ Common.currentLat+","+Common.currentLng+"&" +
                    "destination="+lat+","+lng+"&key="+getResources().getString(R.string.google_direction_api);
            Log.d("URL_MAPS", requestApi);
            mService.getPath(requestApi).enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    Gson gson = new Gson();
                    GoogleMapsAPIRequest requestObject = gson.fromJson(response.body().toString(), GoogleMapsAPIRequest.class);
                    Log.d("RESPONSE", response.body().toString());

                    tvDistance.setText(requestObject.routes.get(0).legs.get(0).distance.text);
                    tvTime.setText(requestObject.routes.get(0).legs.get(0).duration.text);
                    tvAddress.setText(requestObject.routes.get(0).legs.get(0).end_address);
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), t.getMessage().toString(), Toast.LENGTH_SHORT).show();
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        mediaPlayer.release();
        super.onStop();
    }

    @Override
    protected void onPause() {
        mediaPlayer.release();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mediaPlayer.start();
        super.onResume();
    }
}
