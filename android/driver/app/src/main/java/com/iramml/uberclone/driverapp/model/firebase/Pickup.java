package com.iramml.uberclone.driverapp.model.firebase;

import com.google.android.gms.maps.model.LatLng;

public class Pickup {
    private Double lat, lng;
    private String ID;
    private String token;

    public Pickup() {
    }

    public Pickup(Double lat, Double lng, String ID, String token) {
        this.lat = lat;
        this.lng = lng;
        this.ID = ID;
        this.token = token;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
