package com.iramml.uberclone.Common;

import com.iramml.uberclone.Interfaces.googleAPIInterface;
import com.iramml.uberclone.Retrofit.RetrofitClient;

public class Common {
    public static final String baseURL="https://maps.googleapis.com";
    public static googleAPIInterface getGoogleAPI(){
        return RetrofitClient.getClient(baseURL).create(googleAPIInterface.class);
    }
}
