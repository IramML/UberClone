package com.iramml.uberclone.driverapp.activity.viewmodel;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.iramml.uberclone.driverapp.activity.LoginActivity;

public class HomeViewModel extends ViewModel {

    public HomeViewModel() {

    }

    public void signOut(Activity activity) {
        FirebaseAuth.getInstance().signOut();
        Intent intent=new Intent(activity, LoginActivity.class);
        activity.startActivity(intent);
        activity.finish();
    }
}
