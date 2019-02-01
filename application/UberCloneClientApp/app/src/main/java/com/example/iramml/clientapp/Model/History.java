package com.example.iramml.clientapp.Model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class History {
    private String startAddress,endAddress,time,distance,locationStart,locationEnd,tripDate,name;
    private double total;
    public History(){

    }

    @Exclude
    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("distance", distance);
        result.put("endAddress", endAddress);
        result.put("time", time);
        result.put("locationEnd", locationEnd);
        result.put("locationStart", locationStart);
        result.put("name", name);
        result.put("startAddress", startAddress);
        result.put("total", total);
        result.put("tripDate", tripDate);

        return result;
    }

    public History(String startAddress, String endAddress, String time, String distance, String locationStart, String locationEnd, String tripDate, String name, double total) {
        this.startAddress = startAddress;
        this.endAddress = endAddress;
        this.time = time;
        this.distance = distance;
        this.locationStart = locationStart;
        this.locationEnd = locationEnd;
        this.tripDate = tripDate;
        this.name = name;
        this.total = total;
    }

    public String getStartAddress() {
        return startAddress;
    }

    public void setStartAddress(String startAddress) {
        this.startAddress = startAddress;
    }

    public String getEndAddress() {
        return endAddress;
    }

    public void setEndAddress(String endAddress) {
        this.endAddress = endAddress;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getLocationStart() {
        return locationStart;
    }

    public void setLocationStart(String locationStart) {
        this.locationStart = locationStart;
    }

    public String getLocationEnd() {
        return locationEnd;
    }

    public void setLocationEnd(String locationEnd) {
        this.locationEnd = locationEnd;
    }

    public String getTripDate() {
        return tripDate;
    }

    public void setTripDate(String tripDate) {
        this.tripDate = tripDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}
