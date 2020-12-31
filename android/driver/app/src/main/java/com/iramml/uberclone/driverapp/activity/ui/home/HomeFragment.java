package com.iramml.uberclone.driverapp.activity.ui.home;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.iramml.uberclone.driverapp.R;
import com.iramml.uberclone.driverapp.common.Common;
import com.iramml.uberclone.driverapp.interfaces.locationListener;
import com.iramml.uberclone.driverapp.message.Errors;
import com.iramml.uberclone.driverapp.message.ShowMessage;
import com.iramml.uberclone.driverapp.util.Location;

public class HomeFragment extends Fragment implements OnMapReadyCallback {
    private View root;
    private HomeViewModel homeViewModel;

    private GoogleMap mMap;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private DatabaseReference drivers, onlineRef, currentUserRef;
    private GeoFire geoFire;

    private SwitchCompat locationSwitch;
    private Marker currentLocationMarket;

    private Location location;
    private final int REQUEST_CODE_PERMISSION = 100;


    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_home, container, false);
        homeViewModel = new HomeViewModel();
        initViews();
        return root;
    }

    private void initViews() {
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        location=new Location(getActivity(), new locationListener() {
            @Override
            public void locationResponse(LocationResult response) {
                Common.currentLat=response.getLastLocation().getLatitude();
                Common.currentLng=response.getLastLocation().getLongitude();
                if (locationSwitch.isChecked()){
                    drivers = FirebaseDatabase.getInstance().getReference(Common.driver_tbl).child(Common.currentUser.getCarType());
                    geoFire =new GeoFire(drivers);
                    displayLocation();
                }
                //displayLocation();

            }
        });

        locationSwitch = root.findViewById(R.id.locationSwitch);
        locationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    FirebaseDatabase.getInstance().goOnline();
                    location.initializeLocation();
                    drivers = FirebaseDatabase.getInstance().getReference(Common.driver_tbl).child(Common.currentUser.getCarType());
                    geoFire = new GeoFire(drivers);
                }else{
                    FirebaseDatabase.getInstance().goOffline();
                    location.stopUpdateLocation();
                    currentLocationMarket.remove();
                    mMap.clear();
                    //handler.removeCallbacks(drawPathRunnable);
                    if (currentLocationMarket != null)
                        currentLocationMarket.remove();
                }
            }
        });
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void displayLocation(){
        if (Common.currentLat!=null && Common.currentLng!=null){
            if (locationSwitch.isChecked()) {
                geoFire.setLocation(Common.userID,
                        new GeoLocation(Common.currentLat, Common.currentLng),
                        new GeoFire.CompletionListener() {
                            @Override
                            public void onComplete(String key, DatabaseError error) {
                                LatLng currentLocation = new LatLng(Common.currentLat, Common.currentLng);
                                if (currentLocationMarket != null) currentLocationMarket.remove();

                                currentLocationMarket = mMap.addMarker(new MarkerOptions().position(currentLocation)
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_marker))
                                        .title("Your Location"));
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Common.currentLat, Common.currentLng), 15.0f));

                            }
                        });
            }
        }else{
            ShowMessage.messageError(getActivity(), Errors.WITHOUT_LOCATION);
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setTrafficEnabled(false);
        mMap.setIndoorEnabled(false);
        mMap.setBuildingsEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getActivity(), R.raw.uber_style_map));
    }

    @Override
    public void onStart() {
        super.onStart();
        if (locationSwitch.isChecked())
            location.initializeLocation();
    }

    @Override
    public void onStop() {
        super.onStop();
        location.stopUpdateLocation();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        location.onRequestPermissionResult(requestCode, permissions, grantResults);
    }
}