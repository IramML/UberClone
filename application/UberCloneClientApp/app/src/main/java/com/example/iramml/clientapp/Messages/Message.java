package com.example.iramml.clientapp.Messages;

import android.content.Context;
import android.widget.Toast;

public class Message {
    public static void message(Context context, Messages message){
        String str="";
        switch (message){
            case PERMISSION_DENIED:
                str="Permission denied";
                break;
            case RATIONALE:
                str="Permission is required to obtain location";
                break;
        }
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
    }

    public static void messageError(Context context, Errors message) {
        String str="";
        switch (message){
            case ERROR_LOGIN_GOOGLE:
                str="Could not login";
                break;
            case NOT_SUPPORT:
                str="This device is not supported";
                break;
            case WITHOUT_LOCATION:
                str="Cannot get your location";
                break;
        }
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
    }
}
