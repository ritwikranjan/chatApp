package com.example.chatapp;

import androidx.appcompat.app.AppCompatActivity;
import okhttp3.OkHttpClient;

import android.os.Bundle;

import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

public class chatApp extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Firebase Offline Capabilities
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        //Picasso Offline Capabilities
        Picasso.Builder picassoBuilder = new Picasso.Builder(this);
        picassoBuilder.downloader(new OkHttp3Downloader(this));
        Picasso built = picassoBuilder.build();
        built.setIndicatorsEnabled(true);
        built.setLoggingEnabled(true);
        Picasso.setSingletonInstance(built);
    }
}
