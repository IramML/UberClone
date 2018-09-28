package com.iramml.uberclone.Activities;


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
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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
import com.iramml.uberclone.Messages.Errors;
import com.iramml.uberclone.Messages.Message;
import com.iramml.uberclone.Model.User;
import com.iramml.uberclone.R;
import com.rengwuxian.materialedittext.MaterialEditText;

import dmax.dialog.SpotsDialog;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class Login extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private GoogleApiClient googleApiClient;
    public static final int SIGN_IN_CODE_GOOGLE=157;
    Button btnSignIn, btnLogIn;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference users;

    ConstraintLayout root;
    GoogleSignInAccount account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder().setDefaultFontPath("fonts/NotoSans.ttf").setFontAttrId(R.attr.fontPath).build());
        setContentView(R.layout.activity_login);
        SignInButton signInButtonGoogle=findViewById(R.id.login_button_Google);
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
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();
        users=firebaseDatabase.getReference(Common.user_driver_tbl);
        if(firebaseAuth.getUid()!=null)loginSuccess();
        btnSignIn=findViewById(R.id.btnSignin);
        btnLogIn=findViewById(R.id.btnLogin);
        root=findViewById(R.id.root);
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRegistrerDialog();
            }
        });
        btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLoginDialog();
            }
        });
    }
    private void showLoginDialog(){
        AlertDialog.Builder alertDialog=new AlertDialog.Builder(this);
        alertDialog.setTitle("LOG IN");
        alertDialog.setMessage("Please fill all fields");

        LayoutInflater inflater=LayoutInflater.from(this);
        View login_layout=inflater.inflate(R.layout.layout_login, null);
        final MaterialEditText etEmail=login_layout.findViewById(R.id.etEmail);
        final MaterialEditText etPassword=login_layout.findViewById(R.id.etPassword);

        alertDialog.setView(login_layout);
        alertDialog.setPositiveButton("LOG IN", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

                btnLogIn.setEnabled(false);
                if (TextUtils.isEmpty(etEmail.getText().toString())){
                    Snackbar.make(root, "Pleace enter email address", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(etPassword.getText().toString())){
                    Snackbar.make(root, "Pleace enter password", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (etPassword.getText().toString().length()<6){
                    Snackbar.make(root, "Password too short", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                final SpotsDialog waitingDialog=new SpotsDialog(Login.this);
                waitingDialog.show();
                firebaseAuth.signInWithEmailAndPassword(etEmail.getText().toString(), etPassword.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        waitingDialog.dismiss();
                        goToMainActivity();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        waitingDialog.dismiss();
                        Snackbar.make(root, "Failed"+e.getMessage(), Snackbar.LENGTH_SHORT).show();
                        btnLogIn.setEnabled(true);
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
    private void verifyGoogleAccount() {
        OptionalPendingResult<GoogleSignInResult> opr=Auth.GoogleSignInApi.silentSignIn(googleApiClient);
        if (opr.isDone()){
            GoogleSignInResult result= opr.get();
            if (result.isSuccess()) loginSuccess();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        verifyGoogleAccount();
    }
    private void showRegistrerDialog(){
        AlertDialog.Builder alertDialog=new AlertDialog.Builder(this);
        alertDialog.setTitle("SIGN IN");
        alertDialog.setMessage("Please fill all fields");

        LayoutInflater inflater=LayoutInflater.from(this);
        View registrer_layout=inflater.inflate(R.layout.layout_register, null);
        final MaterialEditText etEmail=registrer_layout.findViewById(R.id.etEmail);
        final MaterialEditText etPassword=registrer_layout.findViewById(R.id.etPassword);
        final MaterialEditText etName=registrer_layout.findViewById(R.id.etName);
        final MaterialEditText etPhone=registrer_layout.findViewById(R.id.etPhone);

        alertDialog.setView(registrer_layout);
        alertDialog.setPositiveButton("REGISTRER", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

                if (TextUtils.isEmpty(etEmail.getText().toString())){
                    Snackbar.make(root, "Pleace enter email address", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(etPassword.getText().toString())){
                    Snackbar.make(root, "Please enter password", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (etPassword.getText().toString().length()<6){
                    Snackbar.make(root, "Password too short", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(etName.getText().toString())){
                    Snackbar.make(root, "Pleace enter name", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(etPhone.getText().toString())){
                    Snackbar.make(root, "Pleace enter phone number", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                firebaseAuth.createUserWithEmailAndPassword(etEmail.getText().toString(), etPassword.getText().toString())
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                User user=new User();
                                user.setEmail(etEmail.getText().toString());
                                user.setName(etName.getText().toString());
                                user.setPassword(etPassword.getText().toString());
                                user.setPhone(etPhone.getText().toString());

                                users.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .setValue(user)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Snackbar.make(root, "Registered", Snackbar.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Snackbar.make(root, "Failed"+e.getMessage(), Snackbar.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(root, "Failed"+e.getMessage(), Snackbar.LENGTH_SHORT).show();
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
    private void showRegisterPhone(final User user){
        AlertDialog.Builder alertDialog=new AlertDialog.Builder(this);
        alertDialog.setTitle("SIGN IN");
        alertDialog.setMessage("Please fill all fields");

        LayoutInflater inflater=LayoutInflater.from(this);
        View register_phone_layout=inflater.inflate(R.layout.layout_register_phone, null);
        final MaterialEditText etPhone=register_phone_layout.findViewById(R.id.etPhone);

        alertDialog.setView(register_phone_layout);
        alertDialog.setPositiveButton("LOG IN", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                user.setEmail(account.getEmail());
                user.setName(account.getDisplayName());
                user.setPassword(null);
                user.setPhone(etPhone.getText().toString());
                users.child(account.getId())
                        .setValue(user)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Snackbar.make(root, "Registered", Snackbar.LENGTH_SHORT).show();
                                loginSuccess();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(root, "Failed "+e.getMessage(), Snackbar.LENGTH_SHORT).show();

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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==SIGN_IN_CODE_GOOGLE) {//Google
            GoogleSignInResult result=Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }
    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()){
            final User user=new User();
            account = result.getSignInAccount();

            users.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User post = dataSnapshot.child(account.getId()).getValue(User.class);

                    if(post==null) showRegisterPhone(user);
                    else loginSuccess();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });



        }else{
            Message.messageError(this, Errors.ERROR_LOGIN_GOOGLE);
        }
    }
    private void loginSuccess(){
        goToMainActivity();
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
    private void goToMainActivity(){
        startActivity(new Intent(Login.this, Main.class));
        finish();
    }
}
