package com.example.iramml.clientapp.Retrofit;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class GoogleMapsAPI {
    private static Retrofit retrofit;
    public static Retrofit getClient(String baseURL){
        if (retrofit==null){
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseURL)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build();

        }
        return retrofit;
    }
}
