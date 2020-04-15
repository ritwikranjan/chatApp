package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UsersActivity extends AppCompatActivity {

    MaterialToolbar materialToolbar;
    RecyclerView userList;
    DatabaseReference mUsersDatabase;
    ProgressDialog mProgressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        materialToolbar = findViewById(R.id.user_appBar);
        setSupportActionBar(materialToolbar);
        getSupportActionBar().setTitle("Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mProgressBar = new ProgressDialog(this);
        mProgressBar.setTitle("Loading");
        mProgressBar.setMessage("Please wait while profile is loading");
        mProgressBar.setCanceledOnTouchOutside(false);
        mProgressBar.show();
        userList = findViewById(R.id.userList);
        userList.setLayoutManager(new LinearLayoutManager(this));
        userList.setHasFixedSize(true);
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        FirebaseRecyclerOptions<Users> options =
                new FirebaseRecyclerOptions.Builder<Users>( )
                        .setQuery(mUsersDatabase, new SnapshotParser<Users>( ) {
                            @NonNull
                            @Override
                            public Users parseSnapshot(@NonNull DataSnapshot snapshot) {
                                return new Users(snapshot.child("name").getValue( ).toString( ),
                                        snapshot.child("thumb_img").getValue( ).toString( ),
                                        snapshot.child("status").getValue( ).toString( ));
                            }
                        })
                        .build( );
        FirebaseRecyclerAdapter<Users, UserViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Users, UserViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull UserViewHolder holder, int position, @NonNull Users model) {
                holder.setUserName(model.getName());
                holder.setUserProfilePic(model.getImg());
                holder.setUserStatus(model.getStatus());
                final String userId = getRef(position).getKey();
                holder.root.setOnClickListener(new View.OnClickListener( ) {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(getBaseContext(),ProfileActivity.class);
                        i.putExtra("uid",userId);
                        startActivity(i);
                        //Toast.makeText(getBaseContext(),userId,Toast.LENGTH_LONG).show();
                    }
                });
                mProgressBar.dismiss();



            }

            @NonNull
            @Override
            public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_display, parent, false);
                return new UserViewHolder(view);
            }
        };
        firebaseRecyclerAdapter.startListening();

        userList.setAdapter(firebaseRecyclerAdapter);


    }

    @Override
    protected void onStart() {
        super.onStart( );
        //firebaseRecyclerAdapter.startListening();


    }

    @Override
    protected void onPause() {
        super.onPause( );
        //mProgressBar.dismiss();
    }

    @Override
    protected void onStop() {
        super.onStop( );
        //firebaseRecyclerAdapter.stopListening();
    }
}
