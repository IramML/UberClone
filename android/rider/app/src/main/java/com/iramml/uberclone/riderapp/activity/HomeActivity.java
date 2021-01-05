package com.iramml.uberclone.riderapp.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.iramml.uberclone.riderapp.adapter.ClickListener;
import com.iramml.uberclone.riderapp.common.Common;
import com.iramml.uberclone.riderapp.common.ConfigApp;
import com.iramml.uberclone.riderapp.fragment.BottomSheetRiderFragment;
import com.iramml.uberclone.riderapp.helper.CustomInfoWindow;
import com.iramml.uberclone.riderapp.interfaces.HttpResponse;
import com.iramml.uberclone.riderapp.interfaces.IFCMService;
import com.iramml.uberclone.riderapp.interfaces.locationListener;
import com.iramml.uberclone.riderapp.messages.Errors;
import com.iramml.uberclone.riderapp.messages.ShowMessage;
import com.iramml.uberclone.riderapp.model.firebase.User;
import com.iramml.uberclone.riderapp.model.placesapi.PlacesResponse;
import com.iramml.uberclone.riderapp.model.placesapi.Results;
import com.iramml.uberclone.riderapp.R;
import com.iramml.uberclone.riderapp.util.Location;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
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
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.iramml.uberclone.riderapp.util.NetworkUtil;
import com.iramml.uberclone.riderapp.adapter.RecyclerViewPlaces.PlacesAdapter;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnInfoWindowClickListener {
    private ImageView carUberX, carUberBlack;
    private Button btnRequestPickup;
    private Toolbar toolbar;
    private GoogleMap mMap;
    private LinearLayout llPickupInput, llDestinationInput, llPickupPlace, llDestinationPlace;
    private EditText etFinalPickup, etFinalDestination, etPickup, etDestination;
    private RecyclerView rvPickupPlaces, rvDestinationPlaces;
    private GoogleSignInAccount account;
    private SupportMapFragment mapFragment;

    private Marker riderMarket, destinationMarker;
    private ArrayList<Marker> driverMarkers=new ArrayList<>();

    //Gooogle
    private GoogleApiClient mGoogleApiClient;
    AccessToken accessToken = AccessToken.getCurrentAccessToken();
    boolean isLoggedInFacebook = accessToken != null && !accessToken.isExpired();

    private DatabaseReference driversAvailable;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    private IFCMService ifcmService;

    private Location location;
    private NetworkUtil networkUtil;

    private String mPlaceLocation, mPlaceDestination;
    private Double currentLat, currentLng;
    private boolean isUberX=false, pickupPlacesSelected=false;
    private int radius=1, distance=1; // km
    private static final int LIMIT=3;
    private String URL_BASE_API_PLACES="https://maps.googleapis.com/maps/api/place/textsearch/json?";

    public static boolean driverFound=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initViews();
        verifyGoogleAccount();
        storage=FirebaseStorage.getInstance();
        storageReference=storage.getReference();
        ifcmService=Common.getFCMService();
        networkUtil=new NetworkUtil(this);
        location=new Location(this, new locationListener() {
            @Override
            public void locationResponse(LocationResult response) {
                // Add a icon_marker in Sydney and move the camera
                currentLat=response.getLastLocation().getLatitude();
                currentLng=response.getLastLocation().getLongitude();
                Common.currenLocation=new LatLng(response.getLastLocation().getLatitude(), response.getLastLocation().getLongitude());
                displayLocation();
                if(mPlaceLocation==null) {
                    driversAvailable = FirebaseDatabase.getInstance().getReference(Common.driver_tbl);
                    driversAvailable.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            loadAllAvailableDriver(new LatLng(currentLat, currentLng));
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
        });
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        carUberX=findViewById(R.id.selectedUberX);
        carUberBlack=findViewById(R.id.selectedUberBlack);

        carUberX.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isToggle=!isUberX;
                isUberX=true;
                if(isToggle) {
                    carUberX.setImageResource(R.drawable.car_cui_select);
                    carUberBlack.setImageResource(R.drawable.car_vip);
                }
                loadAllAvailableDriver(new LatLng(currentLat, currentLng));
            }
        });

        carUberBlack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isToggle=isUberX;
                isUberX=false;
                if(isToggle) {
                    carUberX.setImageResource(R.drawable.car_cui);
                    carUberBlack.setImageResource(R.drawable.car_vip_select);
                }
                loadAllAvailableDriver(new LatLng(currentLat, currentLng));
            }
        });

        btnRequestPickup=findViewById(R.id.btnPickupRequest);
        btnRequestPickup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentLat!=null && currentLng!=null) {
                    if (!driverFound)
                        requestPickup(Common.userID);
                    else
                        Common.sendRequestToDriver(Common.driverID, ifcmService, getApplicationContext(), Common.currenLocation);
                }
            }
        });
        etFinalPickup.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    llPickupInput.setVisibility(View.VISIBLE);
                    llPickupPlace.setVisibility(View.GONE);
                    llDestinationInput.setVisibility(View.GONE);
                    llDestinationPlace.setVisibility(View.GONE);
                    etPickup.requestFocus();
                }
            }
        });
        etFinalDestination.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    llPickupInput.setVisibility(View.GONE);
                    llPickupPlace.setVisibility(View.GONE);
                    llDestinationInput.setVisibility(View.VISIBLE);
                    llDestinationPlace.setVisibility(View.GONE);
                    etDestination.requestFocus();
                }
            }
        });
        etPickup.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                getPlacesByString(charSequence.toString(), true);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        etDestination.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                getPlacesByString(charSequence.toString(), false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        updateFirebaseToken();
    }

    private void initViews() {
        llPickupInput = findViewById(R.id.ll_pickup_input);
        llPickupPlace = findViewById(R.id.ll_pickup_place);
        llDestinationInput = findViewById(R.id.ll_destination_input);
        llDestinationPlace = findViewById(R.id.ll_destination_place);
        etFinalPickup = findViewById(R.id.et_final_pickup_location);
        etFinalDestination = findViewById(R.id.et_final_destination);
        etDestination = findViewById(R.id.et_destination);
        etPickup = findViewById(R.id.et_pickup);
        rvPickupPlaces = findViewById(R.id.rv_pickup_places);
        rvPickupPlaces.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvDestinationPlaces = findViewById(R.id.rv_destination_places);
        rvDestinationPlaces.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    public void initDrawer(){
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View navigationHeaderView=navigationView.getHeaderView(0);
        TextView tvName=(TextView)navigationHeaderView.findViewById(R.id.tvRiderName);
        TextView tvStars=(TextView)findViewById(R.id.tvStars);
        CircleImageView imageAvatar=(CircleImageView) navigationHeaderView.findViewById(R.id.imgAvatar);

        tvName.setText(Common.currentUser.getName());
        if(Common.currentUser.getRates()!=null &&
                !TextUtils.isEmpty(Common.currentUser.getRates()))
            tvStars.setText(Common.currentUser.getRates());

        if(isLoggedInFacebook)
            Picasso.get().load("https://graph.facebook.com/" + Common.userID + "/picture?width=500&height=500").into(imageAvatar);
        else if(account!=null)
            Picasso.get().load(account.getPhotoUrl()).into(imageAvatar);
        if(Common.currentUser.getAvatarUrl()!=null &&
                !TextUtils.isEmpty(Common.currentUser.getAvatarUrl()))
            Picasso.get().load(Common.currentUser.getAvatarUrl()).into(imageAvatar);
    }

    private void loadUser(){
        FirebaseDatabase.getInstance().getReference(Common.user_rider_tbl)
                .child(Common.userID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Common.currentUser=dataSnapshot.getValue(User.class);
                        initDrawer();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
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

    private void updateFirebaseToken() {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        final DatabaseReference tokens = db.getReference(Common.token_tbl);

        tokens.child(FirebaseAuth.getInstance().getUid()).setValue(FirebaseInstanceId.getInstance().getToken());
    }

    private void requestPickup(String uid) {
        DatabaseReference dbRequest=FirebaseDatabase.getInstance().getReference(Common.pickup_request_tbl);
        GeoFire mGeofire=new GeoFire(dbRequest);
        mGeofire.setLocation(uid, new GeoLocation(Common.currenLocation.latitude, Common.currenLocation.longitude),
                new GeoFire.CompletionListener() {
                    @Override
                    public void onComplete(String key, DatabaseError error) {

                    }
                });
        if (riderMarket.isVisible())riderMarket.remove();
        riderMarket=mMap.addMarker(new MarkerOptions().title(getResources().getString(R.string.pickup_here)).snippet("").position(new LatLng(Common.currenLocation.latitude, Common.currenLocation.longitude))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        riderMarket.showInfoWindow();
        btnRequestPickup.setText(getResources().getString(R.string.getting_uber));
        findDriver();
    }

    private void findDriver() {
        DatabaseReference driverLocation;
        if(isUberX)
            driverLocation=FirebaseDatabase.getInstance().getReference(Common.driver_tbl).child("UberX");
        else
            driverLocation=FirebaseDatabase.getInstance().getReference(Common.driver_tbl).child("Uber Black");
        GeoFire geoFire=new GeoFire(driverLocation);
        GeoQuery geoQuery=geoFire.queryAtLocation(new GeoLocation(Common.currenLocation.latitude, Common.currenLocation.longitude), radius);
        geoQuery.removeAllListeners();
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                if (!driverFound){
                    driverFound = true;
                    Common.driverID = key;
                    btnRequestPickup.setText(getApplicationContext().getResources().getString(R.string.call_driver));
                }
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                if (!driverFound && radius<LIMIT){
                    radius++;
                    findDriver();
                }else{
                    if(!driverFound) {
                        Toast.makeText(HomeActivity.this, "No available any driver near you", Toast.LENGTH_SHORT).show();
                        btnRequestPickup.setText("REQUEST PICKUP");
                    }
                }

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id){
            case R.id.nav_trip_history:
                showTripHistory();
                break;
            case R.id.nav_updateInformation:
                showDialogUpdateInfo();
                break;
            case R.id.nav_signOut:
                signOut();
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showTripHistory() {
        Intent intent=new Intent(HomeActivity.this, TripHistoryActivity.class);
        startActivity(intent);
    }

    private void showDialogUpdateInfo() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(HomeActivity.this);
        alertDialog.setTitle("UPDATE INFORMATION");
        LayoutInflater inflater = this.getLayoutInflater();
        View layout_pwd = inflater.inflate(R.layout.layout_update_information, null);
        final MaterialEditText etName = (MaterialEditText) layout_pwd.findViewById(R.id.etName);
        final MaterialEditText etPhone = (MaterialEditText) layout_pwd.findViewById(R.id.etPhone);
        final ImageView image_upload = (ImageView) layout_pwd.findViewById(R.id.imageUpload);
        image_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });
        alertDialog.setView(layout_pwd);
        alertDialog.setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                final android.app.AlertDialog waitingDialog = new SpotsDialog.Builder().setContext(HomeActivity.this).build();
                waitingDialog.show();
                String name = etName.getText().toString();
                String phone = etPhone.getText().toString();

                Map<String, Object> updateInfo = new HashMap<>();
                if(!TextUtils.isEmpty(name))
                    updateInfo.put("name", name);
                if(!TextUtils.isEmpty(phone))
                    updateInfo.put("phone",phone);
                DatabaseReference driverInformation = FirebaseDatabase.getInstance().getReference(Common.user_rider_tbl);
                driverInformation.child(Common.userID)
                        .updateChildren(updateInfo)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                waitingDialog.dismiss();
                                if(task.isSuccessful())
                                    Toast.makeText(HomeActivity.this,"Information Updated!",Toast.LENGTH_SHORT).show();
                                else
                                    Toast.makeText(HomeActivity.this,"Information Update Failed!",Toast.LENGTH_SHORT).show();

                            }
                        });
            }
        });
        alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialog.show();
    }

    private void chooseImage() {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if(report.areAllPermissionsGranted()){
                            Intent intent=new Intent(Intent.ACTION_PICK);
                            intent.setType("image/*");
                            startActivityForResult(intent, Common.PICK_IMAGE_REQUEST);
                        }else{
                            Toast.makeText(getApplicationContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==Common.PICK_IMAGE_REQUEST && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            Uri saveUri=data.getData();
            if(saveUri!=null){
                final ProgressDialog progressDialog=new ProgressDialog(this);
                progressDialog.setMessage("Uploading...");
                progressDialog.show();

                String imageName=UUID.randomUUID().toString();
                final StorageReference imageFolder=storageReference.child("images/"+imageName);

                imageFolder.putFile(saveUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                progressDialog.dismiss();
                                Toast.makeText(HomeActivity.this, "Uploaded!", Toast.LENGTH_SHORT).show();
                                imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {

                                        Map<String, Object> avatarUpdate=new HashMap<>();
                                        avatarUpdate.put("avatarUrl", uri.toString());


                                        DatabaseReference driverInformations=FirebaseDatabase.getInstance().getReference(Common.user_rider_tbl);
                                        driverInformations.child(Common.userID).updateChildren(avatarUpdate)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if(task.isSuccessful())
                                                            Toast.makeText(HomeActivity.this, "Uploaded!", Toast.LENGTH_SHORT).show();
                                                        else
                                                            Toast.makeText(HomeActivity.this, "Uploaded error!", Toast.LENGTH_SHORT).show();

                                                    }
                                                });
                                    }
                                });
                            }
                        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress=(100.0*taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                        progressDialog.setMessage("Uploaded "+progress+"%");
                    }
                });
            }
        }
    }

    private void signOut() {
        if(account!=null) {
            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(@NonNull Status status) {
                    if (status.isSuccess()) {
                        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(HomeActivity.this, "Could not log out", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else if(isLoggedInFacebook){
            LoginManager.getInstance().logOut();
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }else{
            FirebaseAuth.getInstance().signOut();
            Intent intent=new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            account = result.getSignInAccount();
            Common.userID=account.getId();
            loadUser();
        }else if(isLoggedInFacebook){
            GraphRequest request = GraphRequest.newMeRequest(
                    accessToken,
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject object, GraphResponse response) {
                            String id=object.optString("id");
                            Common.userID=id;
                            loadUser();
                        }
                    });
            request.executeAsync();
        }else{
            Common.userID=FirebaseAuth.getInstance().getCurrentUser().getUid();
            loadUser();
        }
    }

    private void displayLocation(){
        if (currentLat!=null && currentLng!=null){
            //presence system
            driversAvailable = FirebaseDatabase.getInstance().getReference(Common.driver_tbl);
            driversAvailable.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //if have change from drivers table, we will reload all drivers available
                    loadAllAvailableDriver(new LatLng(currentLat, currentLng));
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            loadAllAvailableDriver(new LatLng(currentLat, currentLng));

        }else{
            ShowMessage.messageError(this, Errors.WITHOUT_LOCATION);
        }

    }

    private void loadAllAvailableDriver(final LatLng location) {
        for (Marker driverMarker:driverMarkers) {
            driverMarker.remove();
        }
        driverMarkers.clear();
        if(!pickupPlacesSelected) {
            if (riderMarket != null)
                riderMarket.remove();

            riderMarket = mMap.addMarker(new MarkerOptions().position(location)
                    .title(getResources().getString(R.string.you))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_marker)));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 15.0f));
        }


        DatabaseReference driverLocation;
        if(isUberX)
            driverLocation=FirebaseDatabase.getInstance().getReference(Common.driver_tbl).child("UberX");
        else
            driverLocation=FirebaseDatabase.getInstance().getReference(Common.driver_tbl).child("Uber Black");
        GeoFire geoFire=new GeoFire(driverLocation);

        GeoQuery geoQuery=geoFire.queryAtLocation(new GeoLocation(location.latitude, location.longitude), distance);
        geoQuery.removeAllListeners();
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, final GeoLocation location) {
                FirebaseDatabase.getInstance().getReference(Common.user_driver_tbl).child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        User driver=dataSnapshot.getValue(User.class);
                        String name;
                        String phone;

                        if(driver.getName()!=null) name=driver.getName();
                        else name="not available";

                        if (driver.getPhone()!=null)phone="Phone: "+driver.getPhone();
                        else phone="Phone: none";


                        driverMarkers.add(mMap.addMarker(new MarkerOptions().position(new LatLng(location.latitude, location.longitude)).flat(true)
                                .title(name).snippet("Driver ID: "+dataSnapshot.getKey()).icon(BitmapDescriptorFactory.fromResource(R.drawable.car))));

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                if (distance<=LIMIT){
                    distance++;
                    loadAllAvailableDriver(location);
                }
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.setInfoWindowAdapter(new CustomInfoWindow(this));
        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.uber_style_map));

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if(destinationMarker!=null)
                    destinationMarker.remove();
                destinationMarker=mMap.addMarker(new MarkerOptions().position(latLng)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_destination_marker))
                        .title("Destination"));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0f));

                BottomSheetRiderFragment mBottomSheet=BottomSheetRiderFragment.newInstance(String.format("%f,%f", currentLat, currentLng),
                        String.format("%f,%f",latLng.latitude, latLng.longitude), true);
                mBottomSheet.show(getSupportFragmentManager(), mBottomSheet.getTag());
            }
        });
        mMap.setOnInfoWindowClickListener(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        location.onRequestPermissionResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onStop() {
        super.onStop();
        location.stopUpdateLocation();
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayLocation();
        location.inicializeLocation();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        if(!marker.getTitle().equals("You")){
            Intent intent=new Intent(HomeActivity.this, CallDriverActivity.class);
            String ID= marker.getSnippet().replace("Driver ID: ", "");
            intent.putExtra("driverID", ID);
            intent.putExtra("lat", currentLat);
            intent.putExtra("lng", currentLng);
            startActivity(intent);
        }
    }

    private void getPlacesByString(String s, final boolean isPickup){
        String queryEncode= s.toString();
        try {
            queryEncode = URLEncoder.encode(s.toString(),"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String query = "&query=" + queryEncode;
        String location = "&location=" + Common.currenLocation.latitude + "," + Common.currenLocation.longitude;
        String radius = "radius=1500";
        String key = "&key=" + ConfigApp.GOOGLE_API_KEY;
        String url = (URL_BASE_API_PLACES + radius + location + query + key).replaceAll(" ", "%20");

        Log.d("URL_PLACES", url);
        networkUtil.httpRequest(url, new HttpResponse() {
            @Override
            public void httpResponseSuccess(String response) {
                pickupPlacesSelected=true;
                Gson gson=new Gson();
                PlacesResponse placesResponse=gson.fromJson(response, PlacesResponse.class);
                for(Results result: placesResponse.results){
                    if(result.geometry.location==null){
                        placesResponse.results.remove(result);
                    }else if(result.geometry.location.lat==null || result.geometry.location.lat.equals("") || result.geometry.location.lat.equals("0.0")){
                        placesResponse.results.remove(result);
                    }else if(result.geometry.location.lng==null || result.geometry.location.lng.equals("") || result.geometry.location.lng.equals("0.0")){
                        placesResponse.results.remove(result);
                    }
                }
                if(isPickup)
                    implementPickupRecyclerView(placesResponse.results);
                else
                    implementDestinationRecyclerView(placesResponse.results);
                
            }
        });
    }

    private void implementPickupRecyclerView(final ArrayList<Results> results) {
        PlacesAdapter placesAdapter=new PlacesAdapter(this, results, new ClickListener() {
            @Override
            public void onClick(View view, int index) {
                mPlaceLocation=results.get(index).formatted_address;
                etFinalPickup.setText(mPlaceLocation);

                llPickupInput.setVisibility(View.GONE);
                llPickupPlace.setVisibility(View.VISIBLE);
                llDestinationInput.setVisibility(View.GONE);
                llDestinationPlace.setVisibility(View.VISIBLE);

                Double lat=Double.valueOf(results.get(index).geometry.location.lat);
                Double lng=Double.valueOf(results.get(index).geometry.location.lng);
                LatLng latLng=new LatLng(lat, lng);
                if(riderMarket!=null)
                    riderMarket.remove();
                riderMarket=mMap.addMarker(new MarkerOptions().position(latLng)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_marker))
                        .title("Pickup Here"));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0f));
            }
        });
        rvPickupPlaces.setAdapter(placesAdapter);
    }

    private void implementDestinationRecyclerView(final ArrayList<Results> results) {
        PlacesAdapter placesAdapter=new PlacesAdapter(this, results, new ClickListener() {
            @Override
            public void onClick(View view, int index) {
                mPlaceDestination=results.get(index).formatted_address;
                etFinalDestination.setText(mPlaceDestination);

                llPickupInput.setVisibility(View.GONE);
                llPickupPlace.setVisibility(View.VISIBLE);
                llDestinationInput.setVisibility(View.GONE);
                llDestinationPlace.setVisibility(View.VISIBLE);

                Double lat=Double.valueOf(results.get(index).geometry.location.lat);
                Double lng=Double.valueOf(results.get(index).geometry.location.lng);
                LatLng latLng=new LatLng(lat, lng);
                if(destinationMarker!=null)
                    destinationMarker.remove();
                destinationMarker=mMap.addMarker(new MarkerOptions().position(latLng)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_destination_marker))
                        .title("Destination"));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0f));

                BottomSheetRiderFragment mBottomSheet=BottomSheetRiderFragment.newInstance(mPlaceLocation, mPlaceDestination, false);
                mBottomSheet.show(getSupportFragmentManager(), mBottomSheet.getTag());
            }
        });
        rvDestinationPlaces.setAdapter(placesAdapter);
    }

}