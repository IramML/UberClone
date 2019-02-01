package com.iramml.uberclone.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.iramml.uberclone.Common.Common;
import com.iramml.uberclone.Helpers.DirectionJSONParser;
import com.iramml.uberclone.Interfaces.IFCMService;
import com.iramml.uberclone.Interfaces.googleAPIInterface;
import com.iramml.uberclone.Interfaces.locationListener;
import com.iramml.uberclone.Model.FCMResponse;
import com.iramml.uberclone.Model.History;
import com.iramml.uberclone.Model.Notification;
import com.iramml.uberclone.Model.Sender;
import com.iramml.uberclone.Model.Token;
import com.iramml.uberclone.Model.User;
import com.iramml.uberclone.R;
import com.iramml.uberclone.Util.Location;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.iramml.uberclone.Common.Common.userID;

public class DriverTracking extends AppCompatActivity implements OnMapReadyCallback , GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    private GoogleMap mMap;
    Location location=null;

    private GoogleApiClient mGoogleApiClient;
    double riderLat, riderLng;
    private Circle riderMarker;
    private Marker driverMarker;

    GoogleSignInAccount account;

    private Polyline direction;
    private googleAPIInterface mService;
    IFCMService mFCMService;

    GeoFire geoFire;
    String riderID, riderToken;

    Button btnStartTrip;

    LatLng pickupLocation;

    DatabaseReference historyDriver, historyRider, riderInformation, drivers, tokens;
    FirebaseDatabase database;

    User riderData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_tracking);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        if(getIntent()!=null){
            riderLat=getIntent().getDoubleExtra("lat",-1.0);
            riderLng=getIntent().getDoubleExtra("lng",-1.0);
            riderID = getIntent().getStringExtra("riderID");
            riderToken=getIntent().getStringExtra("token");
        }
        database = FirebaseDatabase.getInstance();
        historyDriver = database.getReference(Common.history_driver).child(Common.userID);
        historyRider = database.getReference(Common.history_rider).child(riderID);
        riderInformation=database.getReference(Common.user_rider_tbl);
        tokens=database.getReference(Common.token_tbl);
        drivers= FirebaseDatabase.getInstance().getReference(Common.driver_tbl).child(Common.currentUser.getCarType());
        geoFire=new GeoFire(drivers);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        verifyGoogleAccount();


        mService = Common.getGoogleAPI();
        mFCMService=Common.getFCMService();
        location=new Location(this, new locationListener() {
            @Override
            public void locationResponse(LocationResult response) {
                // refresh current location
                Common.currentLat=response.getLastLocation().getLatitude();
                Common.currentLng=response.getLastLocation().getLongitude();
                displayLocation();

            }
        });
        btnStartTrip=(Button)findViewById(R.id.btnStartTrip);
        btnStartTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btnStartTrip.getText().equals("START TRIP")){
                    pickupLocation=new LatLng(Common.currentLat, Common.currentLng);
                    btnStartTrip.setText("DROP OFF HERE");
                }else if(btnStartTrip.getText().equals("DROP OFF HERE")){
                    calculateCashFree(pickupLocation, new LatLng(Common.currentLat, Common.currentLng));
                }
            }
        });
        getRiderData();
    }

    private void getRiderData() {
        riderInformation.child(riderID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                riderData=dataSnapshot.getValue(User.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void calculateCashFree(final LatLng pickupLocation, LatLng latLng) {
        String requestApi = null;
        try {
            requestApi = "https://maps.googleapis.com/maps/api/directions/json?" +
                    "mode=driving&" +
                    "transit_routing_preference=less_driving&" +
                    "origin=" + pickupLocation.latitude + "," + pickupLocation.longitude + "&" +
                    "destination=" + latLng.latitude + "," + latLng.longitude + "&" +
                    "key=" + getResources().getString(R.string.google_direction_api);
            mService.getPath(requestApi)
                    .enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            try {
                                JSONObject jsonObject=new JSONObject(response.body().toString());
                                JSONArray routes=jsonObject.getJSONArray("routes");

                                JSONObject object=routes.getJSONObject(0);
                                JSONArray legs=object.getJSONArray("legs");

                                JSONObject legsObject=legs.getJSONObject(0);

                                JSONObject distance=legsObject.getJSONObject("distance");
                                String distanceText=distance.getString("text");

                                Double distanceValue=Double.parseDouble(distanceText.replaceAll("[^0-9\\\\.]+", ""));

                                JSONObject timeObject=legsObject.getJSONObject("duration");
                                String timeText=timeObject.getString("text");

                                Double timeValue=Double.parseDouble(timeText.replaceAll("[^0-9\\\\.]+", ""));

                                sendDropOffNotification(riderToken);
                                Calendar calendar = Calendar.getInstance();
                                String date = String.format("%s, %d/%d", convertToDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK)), calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH));

                                History driverHistory = new History();
                                driverHistory.setName(riderData.getName());
                                driverHistory.setStartAddress(legsObject.getString("start_address"));
                                driverHistory.setEndAddress(legsObject.getString("end_address"));
                                driverHistory.setTime(String.valueOf(timeValue));
                                driverHistory.setDistance(String.valueOf(distanceValue));
                                driverHistory.setTotal(Common.formulaPrice(distanceValue, timeValue));
                                driverHistory.setLocationStart(String.format("%f,%f", pickupLocation.latitude, pickupLocation.longitude));
                                driverHistory.setLocationEnd(String.format("%f,%f", Common.currentLat, Common.currentLng));
                                driverHistory.setTripDate(date);
                                historyDriver.push().setValue(driverHistory);

                                History riderHistory = new History();
                                riderHistory.setName(Common.currentUser.getName());
                                riderHistory.setStartAddress(legsObject.getString("start_address"));
                                riderHistory.setEndAddress(legsObject.getString("end_address"));
                                riderHistory.setTime(String.valueOf(timeValue));
                                riderHistory.setDistance(String.valueOf(distanceValue));
                                riderHistory.setTotal(Common.formulaPrice(distanceValue, timeValue));
                                riderHistory.setLocationStart(String.format("%f,%f", pickupLocation.latitude, pickupLocation.longitude));
                                riderHistory.setLocationEnd(String.format("%f,%f", Common.currentLat, Common.currentLng));
                                riderHistory.setTripDate(date);
                                historyRider.push().setValue(riderHistory);

                                Intent intent = new Intent(DriverTracking.this, TripDetail.class);
                                intent.putExtra("start_address", legsObject.getString("start_address"));
                                intent.putExtra("end_address", legsObject.getString("end_address"));
                                intent.putExtra("time", String.valueOf(timeValue));
                                intent.putExtra("distance", String.valueOf(distanceValue));
                                intent.putExtra("total", Common.formulaPrice(distanceValue, timeValue));
                                intent.putExtra("location_start", String.format("%f,%f", pickupLocation.latitude, pickupLocation.longitude));
                                intent.putExtra("location_end", String.format("%f,%f", Common.currentLat, Common.currentLng));

                                startActivity(intent);
                                finish();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            Toast.makeText(DriverTracking.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //add rider location
        riderMarker=mMap.addCircle(new CircleOptions()
                .center(new LatLng(riderLat, riderLng))
                .radius(50)
                .strokeColor(Color.BLUE)
                .fillColor(0x220000FF)
                .strokeWidth(5f));

        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.uber_style_map));
        geoFire = new GeoFire(FirebaseDatabase.getInstance().getReference(Common.driver_tbl).child(Common.currentUser.getCarType()));
        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(riderLat, riderLng), 0.05f);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                sendArrivedNotification(riderToken);
                btnStartTrip.setEnabled(true);
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });

    }

    private void sendArrivedNotification(String customerId) {
        Token token = new Token(customerId);
        Notification notification = new Notification( "Arrived",String.format("The driver %s has arrived at your location", Common.currentUser.getName()));
        Sender sender = new Sender(token.getToken(), notification);

        mFCMService.sendMessage(sender).enqueue(new Callback<FCMResponse>() {
            @Override
            public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
                if (response.body().success != 1) {
                    Toast.makeText(DriverTracking.this, "Failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<FCMResponse> call, Throwable t) {

            }
        });

    }

    private void sendDropOffNotification(String customerId) {
        Token token = new Token(customerId);
        Notification notification = new Notification( "DropOff", customerId);
        Sender sender = new Sender(token.getToken(), notification);

        mFCMService.sendMessage(sender).enqueue(new Callback<FCMResponse>() {
            @Override
            public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
                if (response.body().success != 1) {
                    Toast.makeText(DriverTracking.this, "Failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<FCMResponse> call, Throwable t) {

            }
        });

    }
    private String convertToDayOfWeek(int day) {
        switch(day){
            case Calendar.SUNDAY:
                return "SUNDAY";
            case Calendar.MONDAY:
                return "MONDAY";
            case Calendar.TUESDAY:
                return "TUESDAY";
            case Calendar.WEDNESDAY:
                return "WEDNESDAY";
            case Calendar.THURSDAY:
                return "THURSDAY";
            case Calendar.FRIDAY:
                return "FRIDAY";
            case Calendar.SATURDAY:
                return "SATURDAY";
            default:
                return "UNK";
        }
    }
    private void displayLocation(){
        //add driver location
        if(driverMarker!=null)driverMarker.remove();
        driverMarker=mMap.addMarker(new MarkerOptions().position(new LatLng(Common.currentLat, Common.currentLng))
        .title("You").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Common.currentLat, Common.currentLng), 14f));
        geoFire.setLocation(Common.userID,
                new GeoLocation(Common.currentLat, Common.currentLng),
                new GeoFire.CompletionListener() {
                    @Override
                    public void onComplete(String key, DatabaseError error) {

                    }
                });
        //remove route
        if(direction!=null)direction.remove();
          getDirection();

    }

    private void getDirection() {
        LatLng currentPosition = new LatLng(Common.currentLat, Common.currentLng);

        String requestApi = null;
        try {
            requestApi = "https://maps.googleapis.com/maps/api/directions/json?" +
                    "mode=driving&" +
                    "transit_routing_preference=less_driving&" +
                    "origin=" + currentPosition.latitude + "," + currentPosition.longitude + "&" +
                    "destination=" + riderLat + "," + riderLng + "&" +
                    "key=" + getResources().getString(R.string.google_direction_api);
            Log.d("ISAKAY", requestApi);//print url for debug
            mService.getPath(requestApi)
                    .enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            try {

                                new ParserTask().execute(response.body().toString());

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            Toast.makeText(DriverTracking.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void verifyGoogleAccount() {
        GoogleSignInOptions gso=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleApiClient=new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        OptionalPendingResult<GoogleSignInResult> opr=Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()){
            GoogleSignInResult result= opr.get();
            handleSignInResult(result);
        }else {
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(@NonNull GoogleSignInResult googleSignInResult) {
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            account = result.getSignInAccount();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        location.onRequestPermissionResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onResume() {
        super.onResume();
        location.inicializeLocation();
    }

    @Override
    protected void onStop() {
        super.onStop();
        location.stopUpdateLocation();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        ProgressDialog mDialog = new ProgressDialog(DriverTracking.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog.setMessage("Please waiting...");
            mDialog.show();
        }

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... strings) {
            JSONObject jsonObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jsonObject = new JSONObject(strings[0]);
                DirectionJSONParser parser = new DirectionJSONParser();
                routes = parser.parse(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
            mDialog.dismiss();

            ArrayList points = null;
            PolylineOptions polylineOptions = null;

            for (int i = 0; i < lists.size(); i++) {
                points = new ArrayList();
                polylineOptions = new PolylineOptions();

                List<HashMap<String, String>> path = lists.get(i);

                for (int j = 0; j < path.size(); j++) {

                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                polylineOptions.addAll(points);
                polylineOptions.width(10);
                polylineOptions.color(Color.RED);
                polylineOptions.geodesic(true);

            }
            direction = mMap.addPolyline(polylineOptions);
        }
    }
}