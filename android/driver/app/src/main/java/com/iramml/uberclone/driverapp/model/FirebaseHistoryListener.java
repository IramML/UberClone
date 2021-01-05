package com.iramml.uberclone.driverapp.model;

import com.iramml.uberclone.driverapp.model.firebase.History;

import java.util.ArrayList;

public interface FirebaseHistoryListener {
    interface GetFirebaseHistoryListener {
        void onFirebaseHistoryRetrieved(ArrayList<History> historyList);
    }
}
