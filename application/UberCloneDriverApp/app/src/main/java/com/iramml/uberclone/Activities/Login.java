package com.iramml.uberclone.Activities;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.iramml.uberclone.Common.Common;
import com.iramml.uberclone.Helpers.FirebaseHelper;
import com.iramml.uberclone.Messages.Errors;
import com.iramml.uberclone.Messages.Message;
import com.iramml.uberclone.Model.User;
import com.iramml.uberclone.R;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.Arrays;

import dmax.dialog.SpotsDialog;
import mehdi.sakout.fancybuttons.FancyButton;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class Login extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private GoogleApiClient googleApiClient;
    public static final int SIGN_IN_CODE_GOOGLE=157;
    Button btnSignIn, btnLogIn;

    FirebaseHelper firebaseHelper;
    GoogleSignInAccount account;

    //facebook
    CallbackManager mFacebookCallbackManager;
    LoginManager mLoginManager;
    AccessToken accessToken = AccessToken.getCurrentAccessToken();
    boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder().setDefaultFontPath("fonts/NotoSans.ttf").setFontAttrId(R.attr.fontPath).build());
        setContentView(R.layout.activity_login);
        firebaseHelper=new FirebaseHelper(this);
        FancyButton signInButtonGoogle=findViewById(R.id.login_button_Google);
        FancyButton signInButtonFacebook=findViewById(R.id.facebookLogin);
        GoogleSignInOptions gso=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        googleApiClient=new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        signInButtonGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(intent, SIGN_IN_CODE_GOOGLE);
            }
        });
        setupFacebookStuff();
        signInButtonFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AccessToken.getCurrentAccessToken() != null){
                    mLoginManager.logOut();
                } else {
                    mLoginManager.logInWithReadPermissions(Login.this, Arrays.asList("email", "user_birthday", "public_profile"));
                }
            }
        });
        btnSignIn=findViewById(R.id.btnSignin);
        btnLogIn=findViewById(R.id.btnLogin);
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseHelper.showRegistrerDialog();
            }
        });
        btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseHelper.showLoginDialog();
            }
        });
    }

    private void verifyGoogleAccount() {
        OptionalPendingResult<GoogleSignInResult> opr= Auth.GoogleSignInApi.silentSignIn(googleApiClient);
        if (opr.isDone()){
            GoogleSignInResult result= opr.get();
            if (result.isSuccess())
                firebaseHelper.loginSuccess();

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(isLoggedIn){
            startActivity(new Intent(Login.this, DriverHome.class));
            finish();
        }
        verifyGoogleAccount();
    }
    private void setupFacebookStuff() {

        // This should normally be on your application class
        FacebookSdk.sdkInitialize(getApplicationContext());

        mLoginManager = LoginManager.getInstance();
        mFacebookCallbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(mFacebookCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                //login
                firebaseHelper.registerByFacebookAccount();
            }

            @Override
            public void onCancel() {
                Toast.makeText(Login.this,"The login was canceled",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(Login.this,"There was an error in the login",Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==SIGN_IN_CODE_GOOGLE) {//Google
            GoogleSignInResult result=Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
        mFacebookCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()){
            account = result.getSignInAccount();
            firebaseHelper.registerByGoogleAccount(account);
        }else{
            Message.messageError(this, Errors.ERROR_LOGIN_GOOGLE);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Message.messageError(this, Errors.ERROR_LOGIN_GOOGLE);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

}