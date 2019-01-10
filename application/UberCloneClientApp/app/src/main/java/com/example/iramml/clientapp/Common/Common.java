package com.example.iramml.clientapp.Common;

import com.example.iramml.clientapp.Interfaces.IFCMService;
import com.example.iramml.clientapp.Retrofit.IFCMClient;
import com.example.iramml.clientapp.Retrofit.RetrofitClient;

public class Common {
    public static final String driver_tbl="Drivers";
    public static final String user_driver_tbl="DriversInformation";
    public static final String user_rider_tbl="RidersInformation";
    public static final String pickup_request_tbl="PickupRequest";
    public static final String CHANNEL_ID_ARRIVED="ARRIVED";
    public static String token_tbl="Tokens";

    public static final String fcmURL="https://fcm.googleapis.com/";
    public static IFCMService getFCMService(){
        return IFCMClient.getClient(fcmURL).create(IFCMService.class);
    }
}
