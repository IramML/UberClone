package com.iramml.uberclone.driverapp.adapter.recyclerviewhistory;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.iramml.uberclone.driverapp.adapter.ClickListener;
import com.iramml.uberclone.driverapp.model.firebase.History;
import com.iramml.uberclone.driverapp.R;

import java.util.ArrayList;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryViewHolder>{
    private final Context context;
    private final ArrayList<History> items;
    private ClickListener listener;

    public HistoryAdapter(Context context, ArrayList<History> items, ClickListener listener ){
        this.context = context;
        this.items = items;
        this.listener = listener;
    }
    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.template_history,viewGroup,false);
        HistoryViewHolder viewHolder = new HistoryViewHolder(view, listener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder viewHolder, int i) {
        viewHolder.tvRiderName.setText(String.format("Rider Name: %s", items.get(i).getName()));
        viewHolder.tvTripDate.setText(String.format("Date: %s", items.get(i).getTripDate()));
    }
    @Override
    public int getItemCount() {
        return items.size();
    }

}
