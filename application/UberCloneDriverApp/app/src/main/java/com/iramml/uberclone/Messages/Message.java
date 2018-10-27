package com.iramml.uberclone.Messages;

import android.content.Context;
import android.widget.Toast;

import com.iramml.uberclone.R;

public class Message {
    public static void message(Context context, Messages message){
        String str="";
        switch (message){
            case PERMISSION_DENIED:
                str=context.getResources().getString(R.string.permission_denied);
                break;
            case RATIONALE:
                str=context.getResources().getString(R.string.permission_denied_location);
                break;
            case CANCELLED:
                str=context.getResources().getString(R.string.cancelled);
                break;
        }
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
    }

    public static void messageError(Context context, Errors message) {
        String str="";
        switch (message){
            case ERROR_LOGIN_GOOGLE:
                str=context.getResources().getString(R.string.could_not_login);
                break;
            case NOT_SUPPORT:
                str=context.getResources().getString(R.string.device_not_supported);
                break;
            case WITHOUT_LOCATION:
                str=context.getResources().getString(R.string.cannot_get_location);
                break;
        }
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
    }
}
