package com.iramml.uberclone.driverapp.activity.ui.home;

import android.app.Activity;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.iramml.uberclone.driverapp.activity.ui.home.HomeFragmentListener.ShowMessageListener;
import com.iramml.uberclone.driverapp.common.Common;
import com.iramml.uberclone.driverapp.interfaces.locationListener;
import com.iramml.uberclone.driverapp.message.Errors;
import com.iramml.uberclone.driverapp.message.Messages;
import com.iramml.uberclone.driverapp.util.LocationUtil;

public class HomeViewModel extends ViewModel {
    private LocationUtil location;

    private boolean isOnline = false;
    private MutableLiveData<LatLng> currentLatLng;

    private DatabaseReference drivers;
    private GeoFire geoFire;

    private ShowMessageListener showMessagesListener;

    public HomeViewModel() {
        currentLatLng = new MutableLiveData<>();
    }

    public LiveData<LatLng> getCurrentLocation() {
        return currentLatLng;
    }

    public void setShowMessagesListener(ShowMessageListener showMessagesListener) {
        this.showMessagesListener = showMessagesListener;
    }

    public void initializeLocation(Activity activity) {
        location = new LocationUtil(activity, new locationListener() {
            @Override
            public void locationResponse(LocationResult response) {
                Common.currentLat = response.getLastLocation().getLatitude();
                Common.currentLng = response.getLastLocation().getLongitude();
                currentLatLng.setValue(new LatLng(Common.currentLat, Common.currentLng));

                if (isOnline){
                    drivers = FirebaseDatabase.getInstance().getReference(Common.driver_tbl).child(Common.currentUser.getCarType());
                    geoFire = new GeoFire(drivers);
                    displayLocation();
                }
            }
        });
    }

    private void displayLocation() {

        if (Common.currentLat != null && Common.currentLng != null && isOnline) {
            geoFire.setLocation(Common.userID,
                    new GeoLocation(Common.currentLat, Common.currentLng),
                    new GeoFire.CompletionListener() {
                        @Override
                        public void onComplete(String key, DatabaseError error) {
                            LatLng currentLocation = new LatLng(Common.currentLat, Common.currentLng);
                            currentLatLng.setValue(currentLocation);
                        }
                    });
        } else {
            this.showErrorMessage(Errors.WITHOUT_LOCATION);
        }

    }

    public void startLocationTracking() {
        location.initializeLocation();
    }

    public void stopLocationTracking() {
        location.stopUpdateLocation();
    }

    public void setFirebaseOnlineStatus(boolean isOnline) {
        this.isOnline = isOnline;

        if (isOnline) {
            FirebaseDatabase.getInstance().goOnline();
            location.initializeLocation();
            drivers = FirebaseDatabase.getInstance().getReference(Common.driver_tbl).child(Common.currentUser.getCarType());
            geoFire = new GeoFire(drivers);
        }else{
            FirebaseDatabase.getInstance().goOffline();
            location.stopUpdateLocation();
        }
    }


    public void onRequestPermissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        location.onRequestPermissionResult(requestCode, permissions, grantResults);
    }

    private void showErrorMessage(Errors error) {
        showMessagesListener.showErrorMessage(error);
    }
}
