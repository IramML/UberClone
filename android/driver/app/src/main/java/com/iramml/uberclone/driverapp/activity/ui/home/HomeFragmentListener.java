package com.iramml.uberclone.driverapp.activity.ui.home;

import com.iramml.uberclone.driverapp.message.Errors;
import com.iramml.uberclone.driverapp.message.Messages;

public interface HomeFragmentListener {
    public interface ShowMessageListener {
        void showErrorMessage(Errors error);
    }
}
