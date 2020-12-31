package com.iramml.uberclone.riderapp.adapter.RecyclerViewPlaces;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.iramml.uberclone.riderapp.adapter.ClickListener;
import com.iramml.uberclone.riderapp.model.placesapi.Results;
import com.iramml.uberclone.riderapp.R;

import java.util.ArrayList;

public class PlacesAdapter extends RecyclerView.Adapter<PlacesViewHolder>{
    Context context;
    ArrayList<Results> items;
    ClickListener listener;
    PlacesViewHolder viewHolder;
    DatabaseReference favLocations;
    FirebaseDatabase database;

    public PlacesAdapter(Context context, ArrayList<Results> items, ClickListener listener ){
        this.context=context;
        this.items=items;
        this.listener=listener;
        database= FirebaseDatabase.getInstance();
    }

    @NonNull
    @Override
    public PlacesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(context).inflate(R.layout.template_place_item,viewGroup,false);
        viewHolder=new PlacesViewHolder(view, listener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final PlacesViewHolder viewHolder, final int i) {
        viewHolder.tvPlaceName.setText(items.get(i).formatted_address);
    }
    @Override
    public int getItemCount() {
        return items.size();
    }

}
