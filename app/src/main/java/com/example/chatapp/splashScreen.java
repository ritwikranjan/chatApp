package com.example.chatapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class splashScreen extends AppCompatActivity {

    private static final int RC_SIGN_IN = 101;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser currentUser = mAuth.getCurrentUser();


        // Choose authentication providers
        final List<AuthUI.IdpConfig> providers = Collections.singletonList(
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
                                    .setAvailableProviders(providers)
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
                userMap.put("img",mUser.getPhotoUrl().toString());
                userMap.put("thumb_img","default");
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
