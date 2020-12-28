package com.iramml.uberclone.riderapp.retrofit;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class IFCMClient {
    private static Retrofit retrofit;

    public static Retrofit getClient(String baseURL){
        if (retrofit==null){
            retrofit=new Retrofit.Builder().baseUrl(baseURL).addConverterFactory(GsonConverterFactory.create()).build();

        }

        return retrofit;
    }
}
