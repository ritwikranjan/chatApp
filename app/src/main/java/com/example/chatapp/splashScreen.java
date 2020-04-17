package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class splashScreen extends AppCompatActivity {

    private static final int RC_SIGN_IN = 101;
    private FirebaseAuth mAuth;
    String deviceToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser currentUser = mAuth.getCurrentUser();
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>( ) {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                deviceToken = instanceIdResult.getToken();
            }
        }).addOnFailureListener(new OnFailureListener( ) {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });

        // Choose authentication providers
        final List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder( ).build( ),
                new AuthUI.IdpConfig.GoogleBuilder( ).build( ));

        new Handler( ).postDelayed(new Runnable( ) {


            @Override
            public void run() {
                // This method will be executed once the timer is over
                if(currentUser==null){
                    // Create and launch sign-in intent
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setTheme(R.style.AppTheme)
                                    .setAvailableProviders(providers)
                                    .setAlwaysShowSignInMethodScreen(true)
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        }, 1000);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference().child("Users").child(mUser.getUid());
                String displayName = mUser.getDisplayName();
                String status = "Hi There I am using chatApp";
                HashMap<String,String> userMap = new HashMap<>();
                userMap.put("name",displayName);
                userMap.put("status",status);
                userMap.put("tokenId",deviceToken);
                Log.d("deviceToken: ", deviceToken);
                if(mUser.getPhotoUrl()!=null) {
                    userMap.put("img", mUser.getPhotoUrl( ).toString( ));
                } else{
                    userMap.put("img", "default");
                }
                if(mUser.getPhotoUrl()!=null) {
                    userMap.put("thumb_img", mUser.getPhotoUrl( ).toString( ));
                } else {
                    userMap.put("thumb_img", "default");
                }
                myRef.setValue(userMap);
                // ...
            }

        }
    }

    @Override
    protected void onResume() {
        super.onResume( );
        FirebaseUser user = mAuth.getCurrentUser();
        if(user!=null){
            Intent i = new Intent(splashScreen.this,MainActivity.class);
            startActivity(i);
            finish();
        }
    }
}
