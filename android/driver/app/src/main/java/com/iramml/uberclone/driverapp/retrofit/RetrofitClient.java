package com.iramml.uberclone.driverapp.retrofit;

import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit;

    public static Retrofit getClient(){
        final String baseURL = "https://maps.googleapis.com";
        if (retrofit==null){
            retrofit=new Retrofit.Builder().baseUrl(baseURL).addConverterFactory(ScalarsConverterFactory.create()).build();

        }
        return retrofit;
    }
}
