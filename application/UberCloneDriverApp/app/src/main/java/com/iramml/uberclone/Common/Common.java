package com.iramml.uberclone.Common;

import com.iramml.uberclone.Interfaces.IFCMService;
import com.iramml.uberclone.Interfaces.googleAPIInterface;
import com.iramml.uberclone.Model.User;
import com.iramml.uberclone.Retrofit.FCMClient;
import com.iramml.uberclone.Retrofit.RetrofitClient;

public class Common {
    public static final String driver_tbl="Drivers";
    public static final String user_driver_tbl="DriversInformation";
    public static final String user_rider_tbl="RidersInformation";
    public static final String pickup_request_tbl="PickupRequest";
    public static final String token_tbl="Tokens";
    public static User currentUser;

    public static Double currentLat;
    public static Double currentLng;

    public static final String baseURL="https://maps.googleapis.com";
    public static final String fcmURL="https://fcm.googleapis.com/";
    public static googleAPIInterface getGoogleAPI(){
        return RetrofitClient.getClient(baseURL).create(googleAPIInterface.class);
    }
    public static IFCMService getFCMService(){
        return FCMClient.getClient(fcmURL).create(IFCMService.class);
    }
}
