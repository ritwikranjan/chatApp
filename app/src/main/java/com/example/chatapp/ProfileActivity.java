package com.example.chatapp;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {


    private static final int NOT_FRIENDS = 0;
    private static final int FRIENDS = 1;
    private static final int REQ_FRIENDS = 2;
    //private static final int NOT_FRIENDS = 0;
    private ImageView profilePic;
    private TextView profileName, profileStatus;
    private FirebaseUser userMain;
    private DatabaseReference friendReqDb;
    //MaterialToolbar profileToolbar;
    private Button reqButton1, reqButton2;
    private ProgressDialog mProgressBar;
    private String user_id;
    private String reqStatus;
    private int friendState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        initialize( );
        //Functioning of Buttons
        reqButton2.setOnClickListener(new View.OnClickListener( ) {
            @Override
            public void onClick(View v) {
                switch (friendState) {
                    //Sending Friend Request
                    case NOT_FRIENDS:
                        friendReqDb.child(userMain.getUid( )).child(user_id).child("reqStatus").setValue("sent");
                        friendReqDb.child(user_id).child(userMain.getUid( )).child("reqStatus").setValue("received");
                        reqButton1.setVisibility(View.VISIBLE);
                        reqButton1.setText(R.string.cancel_request);
                        reqButton2.setText(R.string.req_sent);
                        reqButton2.setEnabled(false);
                        friendState = REQ_FRIENDS;
                        break;
                    //Accepting Friend Request
                    case REQ_FRIENDS:
                        friendReqDb.child(userMain.getUid( )).child(user_id).child("reqStatus").setValue("accepted");
                        friendReqDb.child(user_id).child(userMain.getUid( )).child("reqStatus").setValue("accepted");
                        reqButton2.setText(R.string.unfriend);
                        reqButton1.setVisibility(View.INVISIBLE);
                        friendState = FRIENDS;
                        break;
                    //UnFriend
                    case FRIENDS:
                        friendReqDb.child(userMain.getUid( )).child(user_id).child("reqStatus").setValue("cancelled");
                        friendReqDb.child(user_id).child(userMain.getUid( )).child("reqStatus").setValue("cancelled");
                        friendState = NOT_FRIENDS;
                        reqButton2.setText(R.string.send_request);
                        //reqButton2.setEnabled(true);
                        break;
                }
            }
        });
        reqButton1.setOnClickListener(new View.OnClickListener( ) {
            @Override
            public void onClick(View v) {
                //Declining Friend Request
                if (friendState == REQ_FRIENDS) {
                    friendReqDb.child(userMain.getUid( )).child(user_id).child("reqStatus").setValue("cancelled");
                    friendReqDb.child(user_id).child(userMain.getUid( )).child("reqStatus").setValue("cancelled");
                    reqButton1.setVisibility(View.INVISIBLE);
                    reqButton2.setText(R.string.send_request);
                    //reqButton2.setEnabled(true);
                }
            }
        });

        /*profileToolbar = findViewById(R.id.profileToolBar);
        setSupportActionBar(profileToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/
        //Fetching Data from Friend Req Database and Updating UI accordingly
        friendReqDb.addValueEventListener(new ValueEventListener( ) {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Changing UI according to reqStatus
                Object ob = dataSnapshot.child(userMain.getUid( )).child(user_id).child("reqStatus").getValue( );
                if (ob != null) {
                    reqStatus = ob.toString( );
                }
                if (reqStatus != null) {
                    switch (reqStatus) {
                        case "received":
                            friendState = REQ_FRIENDS;
                            reqButton2.setText(R.string.accept_request);
                            reqButton1.setVisibility(View.VISIBLE);
                            break;
                        case "sent":
                            friendState = REQ_FRIENDS;
                            reqButton2.setText(R.string.req_sent);
                            reqButton2.setEnabled(false);
                            break;
                        case "accepted":
                            friendState = FRIENDS;
                            reqButton2.setText(R.string.unfriend);
                            reqButton1.setVisibility(View.INVISIBLE);
                            //reqButton2.setEnabled(true);
                            break;
                        case "cancelled":
                            friendState = NOT_FRIENDS;
                            reqButton1.setVisibility(View.INVISIBLE);
                            reqButton2.setText(R.string.send_request);
                            reqButton2.setEnabled(true);
                            break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        /*
        mProgressBar = new ProgressDialog(this);
        mProgressBar.setTitle("Loading");
        mProgressBar.setMessage("Please wait while profile is loading");
        mProgressBar.setCanceledOnTouchOutside(false);
        mProgressBar.show();*/
        assert user_id != null;
        //Profile Loading
        DatabaseReference mRef = FirebaseDatabase.getInstance( ).getReference( ).child("Users").child(user_id);
        mRef.addValueEventListener(new ValueEventListener( ) {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = Objects.requireNonNull(dataSnapshot.child("name").getValue( )).toString( );
                String photoUri = Objects.requireNonNull(dataSnapshot.child("img").getValue( )).toString( );
                String status = Objects.requireNonNull(dataSnapshot.child("status").getValue( )).toString( );
                profileName.setText(name);
                profileStatus.setText(status);
                if (!photoUri.equals("default")) {
                    Picasso.get( ).load(Uri.parse(photoUri)).placeholder(R.drawable.profile_image).into(profilePic);
                } else {
                    profilePic.setImageResource(R.drawable.profile_image);
                }
                //mProgressBar.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause( );
        //mProgressBar.hide();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy( );
        //mProgressBar.dismiss();
    }

    void initialize() {
        friendState = NOT_FRIENDS;
        userMain = FirebaseAuth.getInstance( ).getCurrentUser( );
        user_id = getIntent( ).getStringExtra("uid");
        profilePic = findViewById(R.id.profilePic);
        profileName = findViewById(R.id.profileName);
        profileStatus = findViewById(R.id.profileStatus);
        friendReqDb = FirebaseDatabase.getInstance( ).getReference( ).child("FriendReqData");
        reqButton1 = findViewById(R.id.reqButton1);
        reqButton2 = findViewById(R.id.reqButton2);
    }
}
