package com.iramml.uberclone.riderapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.iramml.uberclone.riderapp.common.Common;
import com.iramml.uberclone.riderapp.interfaces.IFCMService;
import com.iramml.uberclone.riderapp.model.firebase.User;
import com.iramml.uberclone.riderapp.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class CallDriverActivity extends AppCompatActivity {
    CircleImageView imgAvatar;
    TextView tvName, tvPhone, tvRate;
    Button btnCallDriver;

    String driverID;
    LatLng lastLocation;

    IFCMService mService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_driver);
        mService = Common.getFCMService();

        imgAvatar = (CircleImageView)findViewById(R.id.imgAvatar);
        tvName = findViewById(R.id.tvDriverName);
        tvPhone = findViewById(R.id.tvPhone);
        tvRate = findViewById(R.id.tvRate);
        btnCallDriver = findViewById(R.id.btnCallDriver);

        if(getIntent()!=null){
            driverID=getIntent().getStringExtra("driverID");
            double lat=getIntent().getDoubleExtra("lat", 0.0);
            double lng=getIntent().getDoubleExtra("lng", 0.0);
            lastLocation=new LatLng(lat, lng);
            loadDriverInfo(driverID);
        }else finish();
        btnCallDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(driverID!=null && !driverID.isEmpty())
                    Common.sendRequestToDriver(Common.driverID, mService, getApplicationContext(),
                            new LatLng(lastLocation.latitude, lastLocation.longitude));
            }
        });
    }

    private void loadDriverInfo(String driverID) {
        FirebaseDatabase.getInstance().getReference(Common.user_driver_tbl)
                .child(driverID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user=dataSnapshot.getValue(User.class);

                if(user.getAvatarUrl()!=null &&
                        !TextUtils.isEmpty(user.getAvatarUrl()))
                    Picasso.get().load(user.getAvatarUrl()).into(imgAvatar);
                tvName.setText(user.getName());
                tvPhone.setText(user.getPhone());
                tvRate.setText(user.getRates());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
