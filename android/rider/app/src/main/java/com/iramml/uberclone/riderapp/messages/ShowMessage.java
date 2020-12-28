package com.iramml.uberclone.riderapp.messages;

import android.content.Context;
import android.widget.Toast;

import com.iramml.uberclone.riderapp.R;

public class ShowMessage {
    public static void message(Context context, Messages message){
        String str="";
        switch (message){
            case PERMISSION_DENIED:
                str=context.getResources().getString(R.string.permission_denied);
                break;
            case RATIONALE:
                str=context.getResources().getString(R.string.permission_denied_location);
                break;
            case REQUEST_SUCCESS:
                str=context.getResources().getString(R.string.request_send);
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
                str=context.getResources().getString(R.string.device_not_supported);;
                break;
            case WITHOUT_LOCATION:
                str=context.getResources().getString(R.string.cannot_get_location);
                break;
            case SENT_FAILED:
                str=context.getResources().getString(R.string.sent_failed);
                break;
        }
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
    }
}
