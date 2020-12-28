package com.iramml.uberclone.riderapp.util;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;

import com.iramml.uberclone.riderapp.interfaces.locationListener;
import com.iramml.uberclone.riderapp.messages.ShowMessage;
import com.iramml.uberclone.riderapp.messages.Messages;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;

public class Location {
    AppCompatActivity activity;
    private final String permissionFineLocation=android.Manifest.permission.ACCESS_FINE_LOCATION;
    private final String permissionCoarseLocation=android.Manifest.permission.ACCESS_COARSE_LOCATION;

    private final int REQUEST_CODE_LOCATION=100;

    private FusedLocationProviderClient fusedLocationClient;

    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    public Location(AppCompatActivity activity, final locationListener locationListener) {
        this.activity=activity;
        fusedLocationClient=new FusedLocationProviderClient(activity.getApplicationContext());

        inicializeLocationRequest();
        locationCallback=new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                locationListener.locationResponse(locationResult);
            }
        };
    }
    private void inicializeLocationRequest(){
        locationRequest=new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }
    private Boolean validatePermissionsLocation(){
        final Boolean fineLocationAvailable= ActivityCompat.checkSelfPermission(activity.getApplicationContext(), permissionFineLocation)== PackageManager.PERMISSION_GRANTED;
        final Boolean coarseLocationAvailable= ActivityCompat.checkSelfPermission(activity.getApplicationContext(), permissionCoarseLocation)== PackageManager.PERMISSION_GRANTED;

        return fineLocationAvailable && coarseLocationAvailable;
    }
    private void permissionRequest(){
        ActivityCompat.requestPermissions(activity, new String[]{permissionFineLocation, permissionCoarseLocation}, REQUEST_CODE_LOCATION);
    }
    private void requestPermissions(){
        Boolean contextProvider=ActivityCompat.shouldShowRequestPermissionRationale(activity, permissionFineLocation);

        if (contextProvider) ShowMessage.message(activity.getApplicationContext(), Messages.RATIONALE);
        permissionRequest();
    }
    public void onRequestPermissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        switch (requestCode){
            case REQUEST_CODE_LOCATION:
                if (grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED)getLocation();
                else ShowMessage.message(activity.getApplicationContext(), Messages.PERMISSION_DENIED);
                break;
        }
    }
    public void inicializeLocation(){
        if (validatePermissionsLocation())getLocation();
        else requestPermissions();
    }
    public void stopUpdateLocation(){
        this.fusedLocationClient.removeLocationUpdates(locationCallback);
    }
    @SuppressLint("MissingPermission")
    private void getLocation(){
        validatePermissionsLocation();
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }
}
