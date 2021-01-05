package com.iramml.uberclone.driverapp.activity.ui.history;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.iramml.uberclone.driverapp.R;
import com.iramml.uberclone.driverapp.activity.ui.home.HomeViewModel;
import com.iramml.uberclone.driverapp.adapter.ClickListener;
import com.iramml.uberclone.driverapp.adapter.recyclerviewhistory.HistoryAdapter;
import com.iramml.uberclone.driverapp.common.Common;
import com.iramml.uberclone.driverapp.model.firebase.History;

import java.util.ArrayList;

public class HistoryFragment extends Fragment {
    private View root;
    private RecyclerView rvHistory;

    private HistoryViewModel historyViewModel;

    public HistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_history, container, false);
        historyViewModel =
                new ViewModelProvider(this).get(HistoryViewModel.class);
        initRecyclerView();
        historyViewModel.getDriverHistory();
        return root;
    }



    private void initRecyclerView(){
        rvHistory = root.findViewById(R.id.rv_history);
        rvHistory.setHasFixedSize(true);
        rvHistory.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        rvHistory.setItemAnimator(new DefaultItemAnimator());
        rvHistory.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        rvHistory.setAdapter(historyViewModel.getHistoryAdapter(getActivity()));
    }
}