package com.iramml.uberclone.driverapp.message;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.iramml.uberclone.driverapp.R;
import com.iramml.uberclone.driverapp.util.Utilities;

public class ShowMessage {
    private static final Utilities utilities = new Utilities();

    public static void message(View root, Context context, Messages message) {
        String messageStr = "";
        switch (message){
            case PERMISSION_DENIED:
                messageStr = context.getResources().getString(R.string.permission_denied);
                break;
            case RATIONALE:
                messageStr = context.getResources().getString(R.string.permission_denied_location);
                break;
            case CANCELLED:
                messageStr = context.getResources().getString(R.string.cancelled);
                break;
        }

        utilities.displayMessage(root, context, messageStr);
    }

    public static void messageError(View root, Context context, Errors message) {
        String messageStr = "";
        switch (message){
            case ERROR_LOGIN_GOOGLE:
                messageStr = context.getResources().getString(R.string.could_not_login);
                break;
            case NOT_SUPPORT:
                messageStr = context.getResources().getString(R.string.device_not_supported);
                break;
            case WITHOUT_LOCATION:
                messageStr = context.getResources().getString(R.string.cannot_get_location);
                break;
        }

        utilities.displayMessage(root, context, messageStr);
    }
}
