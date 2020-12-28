package com.iramml.uberclone.driverapp.model.RoutesAPI;

import com.google.android.gms.maps.model.LatLng;

public class LatLon{
    public Double lat=0.0;
    public Double lng=0.0;
    public LatLng toLatLng(){
        return new LatLng(lat, lng);
    }
}