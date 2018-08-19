package com.iramml.uberclone.Activities;

import android.animation.ValueAnimator;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.google.gson.Gson;
import com.iramml.uberclone.Common.Common;
import com.iramml.uberclone.GoogleAPIRoutesRequest.GoogleMapsAPIRequest;
import com.iramml.uberclone.Interfaces.IFCMService;
import com.iramml.uberclone.Interfaces.googleAPIInterface;
import com.iramml.uberclone.R;
import com.iramml.uberclone.Retrofit.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustommerCall extends AppCompatActivity {
    TextView tvTime, tvAddress, tvDistance;

    MediaPlayer mediaPlayer;

    googleAPIInterface mService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custommer_call);
        mService=Common.getGoogleAPI();
        tvTime=findViewById(R.id.tvTime);
        tvDistance=findViewById(R.id.tvDistance);
        tvAddress=findViewById(R.id.tvAddress);

        mediaPlayer=MediaPlayer.create(this, R.raw.ringtone);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        if (getIntent()!=null){
            double lat=getIntent().getDoubleExtra("lat", -1.0);
            double lng=getIntent().getDoubleExtra("lng", -1.0);

            getDirection(lat, lng);
        }else finish();
    }
    private void getDirection(double lat, double lng){

        final String requestApi;

        try{
            requestApi="https://maps.googleapis.com/maps/api/directions/json?mode=driving&" +
                    "transit_routing_preference=less_driving&origin="+ Common.currentLat+","+Common.currentLng+"&" +
                    "destination="+lat+","+lng;
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
