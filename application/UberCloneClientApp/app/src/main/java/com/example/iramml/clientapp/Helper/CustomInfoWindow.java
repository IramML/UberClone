package com.example.iramml.clientapp.Helper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.iramml.clientapp.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class CustomInfoWindow implements GoogleMap.InfoWindowAdapter{
    View view0;
    public CustomInfoWindow(Context context){
        view0= LayoutInflater.from(context).inflate(R.layout.custom_rider_info_window, null);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        TextView tvPickupTitle=view0.findViewById(R.id.tvPickupInfo);
        tvPickupTitle.setText(marker.getTitle());

        TextView tvPicupSnippet=view0.findViewById(R.id.tvPickupSnippet);
        tvPicupSnippet.setText(marker.getSnippet());
        return view0;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
}
