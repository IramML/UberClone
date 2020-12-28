package com.iramml.uberclone.driverapp.adapter.recyclerviewhistory;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.iramml.uberclone.driverapp.R;
import com.iramml.uberclone.driverapp.adapter.ClickListener;

public class HistoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    TextView tvRiderName, tvTripDate;
    ClickListener listener;

    public HistoryViewHolder(View itemView, ClickListener listener) {
        super(itemView);
        tvRiderName=itemView.findViewById(R.id.tvRiderName);
        tvTripDate=itemView.findViewById(R.id.tvTripDate);
        this.listener=listener;
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
            this.listener.onClick(view, getAdapterPosition());
    }

}
