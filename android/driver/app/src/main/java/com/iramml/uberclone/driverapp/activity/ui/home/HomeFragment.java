package com.iramml.uberclone.driverapp.activity.ui.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.iramml.uberclone.driverapp.R;
import com.iramml.uberclone.driverapp.common.Common;
import com.iramml.uberclone.driverapp.message.Errors;
import com.iramml.uberclone.driverapp.message.Messages;
import com.iramml.uberclone.driverapp.message.ShowMessage;
import com.iramml.uberclone.driverapp.util.Utilities;

public class HomeFragment extends Fragment implements OnMapReadyCallback, HomeFragmentListener.ShowMessageListener {
    private View root;
    private HomeViewModel homeViewModel;

    private GoogleMap mMap;

    private SwitchCompat locationSwitch;
    private Marker currentLocationMarket;


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
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        homeViewModel.initializeLocation(getActivity());
        homeViewModel.setShowMessagesListener(this);
        initViews();
        initListeners();
        initObservers();
        return root;
    }

    private void initViews() {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        locationSwitch = root.findViewById(R.id.locationSwitch);
    }

    private void initListeners() {
        locationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                homeViewModel.setFirebaseOnlineStatus(isChecked);
                if (!isChecked) {
                    if (currentLocationMarket != null)
                        currentLocationMarket.remove();
                }

            }
        });
    }

    private void initObservers() {
        homeViewModel.getCurrentLocation().observe(getViewLifecycleOwner(), new Observer<LatLng>() {
            @Override
            public void onChanged(LatLng latLng) {
                if (currentLocationMarket != null)
                    currentLocationMarket.remove();

                currentLocationMarket = mMap.addMarker(new MarkerOptions().position(latLng)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_marker))
                        .title("Your Location"));

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Common.currentLat, Common.currentLng), 15.0f));
            }
        });
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
            homeViewModel.startLocationTracking();
    }

    @Override
    public void onStop() {
        super.onStop();
        homeViewModel.stopLocationTracking();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        homeViewModel.onRequestPermissionResult(requestCode, permissions, grantResults);
    }

    @Override
    public void showErrorMessage(Errors error) {
        ShowMessage.messageError(root, getActivity(), error);
    }
}