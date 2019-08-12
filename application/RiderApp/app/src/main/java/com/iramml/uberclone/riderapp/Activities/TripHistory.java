package com.iramml.uberclone.riderapp.Activities;

import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.iramml.uberclone.riderapp.Common.Common;
import com.iramml.uberclone.riderapp.Model.History;
import com.iramml.uberclone.riderapp.R;
import com.iramml.uberclone.riderapp.recyclerViewHistory.ClickListener;
import com.iramml.uberclone.riderapp.recyclerViewHistory.historyAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TripHistory extends AppCompatActivity {
    FirebaseDatabase database;
    DatabaseReference riderHistory;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView rvHistory;
    historyAdapter adapter;
    FirebaseAuth mAuth;
    ArrayList<History> listData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_history);
        initToolbar();
        initRecyclerView();

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        riderHistory = database.getReference(Common.history_rider);
        listData = new ArrayList<>();
        adapter = new historyAdapter(this, (ArrayList<History>) listData, new ClickListener() {
            @Override
            public void onClick(View view, int index) {

            }
        });
        rvHistory.setAdapter(adapter);
        getHistory();
    }
    private void getHistory(){
        riderHistory.child(Common.userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    History history = postSnapshot.getValue(History.class);
                    listData.add(history);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    private void initRecyclerView(){
        rvHistory = findViewById(R.id.rvHistory);
        rvHistory.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        rvHistory.setLayoutManager(layoutManager);
        rvHistory.setItemAnimator(new DefaultItemAnimator());
        rvHistory.addItemDecoration(new DividerItemDecoration(getApplicationContext(),LinearLayoutManager.VERTICAL));
    }
    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Register");

        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }
}
