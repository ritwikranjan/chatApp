package com.example.chatapp;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
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
    private DatabaseReference friendReqDb,friendDb,notificationDB;
    //MaterialToolbar profileToolbar;
    private Button reqButton1, reqButton2;
    private ProgressDialog mProgressBar;
    private String user_id;
    private String reqStatus;
    private int friendState;
    private int notifClick;
    SimpleDateFormat sdf;
    Calendar c;
    String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        initialize( );

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
                            //Toast.makeText(getBaseContext(),"recieved",Toast.LENGTH_LONG).show();
                            reqButton2.setText(R.string.accept_request);
                            reqButton1.setVisibility(View.VISIBLE);


                            break;
                        case "sent":
                            friendState = REQ_FRIENDS;
                            reqButton2.setText(R.string.req_sent);
                            reqButton1.setVisibility(View.VISIBLE);
                            reqButton1.setText(R.string.cancel_request);
                            reqButton2.setEnabled(false);
                            //Toast.makeText(getBaseContext(),"sent",Toast.LENGTH_LONG).show();
                            break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //Fetching Data from Friend Database and updating UI accordingly
        friendDb.addValueEventListener(new ValueEventListener( ) {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Object ob = dataSnapshot.child(userMain.getUid( )).child(user_id).getValue();
                if (ob != null) {
                    friendState = FRIENDS;
                    reqButton2.setText(R.string.unfriend);
                    reqButton1.setVisibility(View.INVISIBLE);
                    //reqStatus = ob.toString( );
                } else{
                    /*friendState = NOT_FRIENDS;
                    reqButton1.setVisibility(View.INVISIBLE);
                    reqButton2.setText(R.string.send_request);
                    reqButton2.setEnabled(true);*/
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //Functioning of Buttons
        reqButton2.setOnClickListener(new View.OnClickListener( ) {
            @Override
            public void onClick(View v) {

                switch (friendState) {
                    //Sending Friend Request
                    case NOT_FRIENDS:
                        friendReqDb.child(userMain.getUid( )).child(user_id).child("reqStatus").setValue("sent");
                        friendReqDb.child(user_id).child(userMain.getUid( )).child("reqStatus").setValue("received");
                        HashMap<String,String> notificationData = new HashMap<>();
                        notificationData.put("type","request");
                        notificationData.put("from",userMain.getUid());
                        notificationDB.child(user_id).push().setValue(notificationData);
                        reqButton1.setVisibility(View.VISIBLE);
                        reqButton1.setText(R.string.cancel_request);
                        reqButton2.setText(R.string.req_sent);
                        reqButton2.setEnabled(false);
                        friendState = REQ_FRIENDS;
                        break;
                    //Accepting Friend Request
                    case REQ_FRIENDS:
                        friendReqDb.child(userMain.getUid( )).child(user_id).child("reqStatus").removeValue().addOnSuccessListener(new OnSuccessListener<Void>( ) {
                            @Override
                            public void onSuccess(Void aVoid) {
                                friendReqDb.child(user_id).child(userMain.getUid( )).child("reqStatus").removeValue().addOnSuccessListener(new OnSuccessListener<Void>( ) {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        friendDb.child(userMain.getUid( )).child(user_id).setValue(date);
                                        friendDb.child(user_id).child(userMain.getUid( )).setValue(date);
                                        reqButton2.setText(R.string.unfriend);
                                        reqButton2.setEnabled(true);
                                        reqButton1.setVisibility(View.INVISIBLE);
                                        friendState = FRIENDS;
                                    }
                                });
                            }
                        });
                        break;
                    //UnFriend
                    case FRIENDS:
                        friendDb.child(userMain.getUid( )).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>( ) {
                            @Override
                            public void onSuccess(Void aVoid) {
                                friendDb.child(user_id).child(userMain.getUid( )).removeValue().addOnSuccessListener(new OnSuccessListener<Void>( ) {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        friendState = NOT_FRIENDS;
                                        reqButton2.setText(R.string.send_request);
                                    }
                                });
                            }
                        });
                        //reqButton2.setEnabled(true);
                        break;
                }
            }
        });
        reqButton1.setOnClickListener(new View.OnClickListener( ) {
            @Override
            public void onClick(View v) {
                //Declining or Cancelling Friend Request
                if (friendState == REQ_FRIENDS) {
                    friendReqDb.child(userMain.getUid( )).child(user_id).child("reqStatus").removeValue().addOnSuccessListener(new OnSuccessListener<Void>( ) {
                        @Override
                        public void onSuccess(Void aVoid) {
                            friendReqDb.child(user_id).child(userMain.getUid( )).child("reqStatus").removeValue().addOnSuccessListener(new OnSuccessListener<Void>( ) {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    friendState = NOT_FRIENDS;
                                    reqButton1.setVisibility(View.INVISIBLE);
                                    reqButton2.setText(R.string.send_request);
                                    reqButton2.setEnabled(true);
                                }
                            });
                        }
                    });
                }
            }
        });

        /*profileToolbar = findViewById(R.id.profileToolBar);
        setSupportActionBar(profileToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/

        /*
        mProgressBar = new ProgressDialog(this);
        mProgressBar.setTitle("Loading");
        mProgressBar.setMessage("Please wait while profile is loading");
        mProgressBar.setCanceledOnTouchOutside(false);
        mProgressBar.show();*/
        assert user_id != null;
        //Profile Loading
        DatabaseReference mRef = FirebaseDatabase.getInstance( ).getReference( ).child("Users").child(user_id);
        mRef.keepSynced(true);
        mRef.addValueEventListener(new ValueEventListener( ) {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = Objects.requireNonNull(dataSnapshot.child("name").getValue( )).toString( );
                final String photoUri = Objects.requireNonNull(dataSnapshot.child("img").getValue( )).toString( );
                String status = Objects.requireNonNull(dataSnapshot.child("status").getValue( )).toString( );
                profileName.setText(name);
                profileStatus.setText(status);
                if (!photoUri.equals("default")) {
                    Picasso.get( )
                            .load(Uri.parse(photoUri))
                            .networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.profile_image)
                            .into(profilePic, new Callback( ) {
                                @Override
                                public void onSuccess() { }
                                @Override
                                public void onError(Exception e) {
                                    Picasso.get( ).load(Uri.parse(photoUri)).placeholder(R.drawable.profile_image).into(profilePic);
                                }
                            });
                } else {
                    profilePic.setImageResource(R.drawable.profile_image);
                }
                //mProgressBar.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        if(notifClick==1){
            reqButton2.performClick();
            Toast.makeText(getBaseContext(),"working",Toast.LENGTH_LONG).show();
        }
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
        //friendState = NOT_FRIENDS;
        userMain = FirebaseAuth.getInstance( ).getCurrentUser( );
        user_id = getIntent( ).getStringExtra("uid");
        profilePic = findViewById(R.id.profilePic);
        profileName = findViewById(R.id.profileName);
        profileStatus = findViewById(R.id.profileStatus);
        friendReqDb = FirebaseDatabase.getInstance( ).getReference( ).child("FriendReqData");
        friendReqDb.keepSynced(true);
        friendDb = FirebaseDatabase.getInstance( ).getReference( ).child("FriendData");
        friendDb.keepSynced(true);
        notificationDB = FirebaseDatabase.getInstance().getReference().child("notifications");
        reqButton1 = findViewById(R.id.reqButton1);
        reqButton2 = findViewById(R.id.reqButton2);
        sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        c = Calendar.getInstance();
        date = sdf.format(c.getTime());
        notifClick = getIntent().getIntExtra("acceptVal",2);

            Toast.makeText(getBaseContext(),String.valueOf(notifClick),Toast.LENGTH_LONG).show();


    }
}
