package com.example.iramml.clientapp.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.iramml.clientapp.R;

public class BottomSheetRiderFragment extends BottomSheetDialogFragment {
    String mLocation, mDestination;

    public static BottomSheetRiderFragment newInstance(String location, String destination){
        BottomSheetRiderFragment fragment=new BottomSheetRiderFragment();
        Bundle args=new Bundle();
        args.putString("location", location);
        args.putString("destination", destination);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocation=getArguments().getString("location");
        mDestination=getArguments().getString("destination");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view0=inflater.inflate(R.layout.bottom_sheet_rider, container, false);
        TextView txtLocation=(TextView)view0.findViewById(R.id.txtLocation);
        TextView txtDestination=(TextView)view0.findViewById(R.id.txtDestination);
        TextView txtCalculate=(TextView)view0.findViewById(R.id.txtCalculate);

        txtLocation.setText(mLocation);
        txtDestination.setText(mDestination);
        return view0;
    }
}
