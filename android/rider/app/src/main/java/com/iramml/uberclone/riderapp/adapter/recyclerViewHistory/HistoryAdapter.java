package com.iramml.uberclone.riderapp.adapter.recyclerViewHistory;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.iramml.uberclone.riderapp.adapter.ClickListener;
import com.iramml.uberclone.riderapp.model.firebase.History;
import com.iramml.uberclone.riderapp.R;

import java.util.ArrayList;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryViewHolder>{
    Context context;
    ArrayList<History> items;
    ClickListener listener;
    HistoryViewHolder viewHolder;

    public HistoryAdapter(Context context, ArrayList<History> items, ClickListener listener ) {
        this.context = context;
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.template_history,viewGroup,false);
        viewHolder = new HistoryViewHolder(view, listener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder viewHolder, int i) {
        viewHolder.tvDriverName.setText(String.format("Driver Name: %s", items.get(i).getName()));
        viewHolder.tvTripDate.setText(String.format("Date: %s", items.get(i).getTripDate()));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

}
