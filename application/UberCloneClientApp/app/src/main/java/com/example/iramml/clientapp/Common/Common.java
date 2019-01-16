package com.example.iramml.clientapp.Common;

import com.example.iramml.clientapp.Interfaces.IFCMService;
import com.example.iramml.clientapp.Retrofit.GoogleMapsAPI;
import com.example.iramml.clientapp.Retrofit.IFCMClient;
import com.example.iramml.clientapp.Retrofit.IGoogleAPI;
import com.example.iramml.clientapp.Retrofit.RetrofitClient;

public class Common {
    public static final String driver_tbl="Drivers";
    public static final String user_driver_tbl="DriversInformation";
    public static final String user_rider_tbl="RidersInformation";
    public static final String pickup_request_tbl="PickupRequest";
    public static final String CHANNEL_ID_ARRIVED="ARRIVED";
    public static String token_tbl="Tokens";
    public static String rate_detail_tbl="RateDetails";

    public static boolean driverFound=false;
    public static String driverID="";

    public static final String fcmURL="https://fcm.googleapis.com/";
    public static final String googleAPIUrl="https://maps.googleapis.com";

    private static double baseFare=2.55;
    private static double timeRate=0.35;
    private static double distanceRate=1.75;

    public static double getPrice(double km, int min){
        return (baseFare+(timeRate*min)+(distanceRate*km));
    }

    public static IFCMService getFCMService(){
        return IFCMClient.getClient(fcmURL).create(IFCMService.class);
    }
    public static IGoogleAPI getGoogleService(){
        return GoogleMapsAPI.getClient(googleAPIUrl).create(IGoogleAPI.class);
    }
}
