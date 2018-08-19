package com.iramml.uberclone.Interfaces;

import com.iramml.uberclone.Model.FCMResponse;
import com.iramml.uberclone.Model.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IFCMService {
    @Headers({
        "Content-Type:application/json",
        "Authorization:key=AAAA4zxaLrc:APA91bH_nRR08YI7Xi8rFMlzAWTR9d5FBmEJR4h-RZ8a_yo2EbmRFqEAlcRID6DjpVy4zIWSwhhVmvsD-jVPZrkuaoifMldgxmSZ5OKzBzSMcIRI0iyD74DSwo36vEsHiwTCXg2i4GUa3YcUYK0137uaTfzIPzjzHw"
    })
    @POST("fcm/send")
    Call<FCMResponse> sendMessage(@Body Sender body);
}
