package com.iramml.uberclone.driverapp.model;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.iramml.uberclone.driverapp.common.Common;
import com.iramml.uberclone.driverapp.model.firebase.History;

import java.util.ArrayList;

public class FirebaseHistoryModel {
    private FirebaseDatabase database;
    private DatabaseReference driverHistory;

    private FirebaseAuth firebaseAuth;


    public FirebaseHistoryModel() {
        database = FirebaseDatabase.getInstance();
        driverHistory = database.getReference(Common.history_driver);

        firebaseAuth = FirebaseAuth.getInstance();
    }

    public void getHistory(FirebaseHistoryListener.GetFirebaseHistoryListener getFirebaseHistoryListener){
        final String userID = firebaseAuth.getCurrentUser().getUid();

        final ArrayList<History> historyList = new ArrayList<>();
        driverHistory.child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    History history = postSnapshot.getValue(History.class);
                    historyList.add(history);
                }
                driverHistory.removeEventListener(this);
                getFirebaseHistoryListener.onFirebaseHistoryRetrieved(historyList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
