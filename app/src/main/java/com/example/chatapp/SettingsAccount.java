package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.ContentLoadingProgressBar;
import de.hdodenhof.circleimageview.CircleImageView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class SettingsAccount extends AppCompatActivity {

    static String name,photoUri,thumbPhotoUri,status;
    TextView displayName,displayStatus;
    CircleImageView profilePic;
    Button changeStatus, changePic;
    private StorageReference mStorageRef;
    ProgressDialog mProgressBar;
    private FirebaseUser user;
    private DatabaseReference mRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_account);

        displayName = findViewById(R.id.displayName);
        displayStatus = findViewById(R.id.displayStatus);
        profilePic = findViewById(R.id.profile_image);
        changePic = findViewById(R.id.changePic);
        changeStatus = findViewById(R.id.changeStatus);
        mProgressBar = new ProgressDialog(this);
        mProgressBar.setTitle("Loading");
        mProgressBar.setMessage("Please Wait While contents are loading");
        mProgressBar.setCanceledOnTouchOutside(false);
        mProgressBar.show();

        user = FirebaseAuth.getInstance().getCurrentUser();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mRef = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid());
        mRef.addValueEventListener(new ValueEventListener( ) {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                name = dataSnapshot.child("name").getValue().toString();
                photoUri = dataSnapshot.child("img").getValue().toString();
                thumbPhotoUri = dataSnapshot.child("thumb_img").getValue().toString();
                status = dataSnapshot.child("status").getValue().toString();
                displayName.setText(name);
                displayStatus.setText(status);
                mProgressBar.hide();
                Picasso.get().load(Uri.parse(photoUri)).into(profilePic);
               // profilePic.setImageBitmap(Bitmap.createBitmap(new Bitmap()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        changeStatus.setOnClickListener(new View.OnClickListener( ) {
            @Override
            public void onClick(View v) {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(SettingsAccount.this);
                LayoutInflater inflater = getLayoutInflater( );
                View dialogView = inflater.inflate(R.layout.alertdialog_custom_view, null);
                // Specify alert dialog is not cancelable/not ignorable
                builder.setCancelable(false);
                // Set the custom layout as alert dialog view
                builder.setView(dialogView);
                // Get the custom alert dialog view widgets reference
                final TextInputLayout et_name = dialogView.findViewById(R.id.statusText);
                et_name.getEditText().setText(displayStatus.getText());
                builder.setPositiveButton("Submit", new DialogInterface.OnClickListener( ) {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String status = et_name.getEditText( ).getText( ).toString( );
                        mRef.child("status").setValue(status);
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener( ) {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel( );
                    }
                });
                // Create the alert dialog
                final AlertDialog dialog = builder.create( );
                // Display the custom alert dialog on interface
                dialog.show( );
            }
        });

        changePic.setOnClickListener(new View.OnClickListener( ) {
            @Override
            public void onClick(View v) {

                //Intent galleryIntent = new Intent();
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1,1)
                        .start(SettingsAccount.this);


            }
        });




    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mProgressBar = new ProgressDialog(SettingsAccount.this);
                mProgressBar.setTitle("Uploading");
                mProgressBar.setMessage("Please wait while your image is uploaded");
                mProgressBar.setCanceledOnTouchOutside(false);
                mProgressBar.show();
                Uri resultUri = result.getUri( );
                final StorageReference filePath = mStorageRef.child("profile_pic/"+user.getUid()+".jpg");
                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>( ) {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){
                            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>( ) {
                                @Override
                                public void onSuccess(Uri uri) {
                                    mRef.child("img").setValue(uri.toString()).addOnCompleteListener(new OnCompleteListener<Void>( ) {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            mProgressBar.hide();
                                        }
                                    });
                                }
                            });
                        }
                    }
                }).addOnFailureListener(new OnFailureListener( ) {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError( );
            }
        }
    }
}
