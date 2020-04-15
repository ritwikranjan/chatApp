package com.example.chatapp;

import android.net.Uri;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

class UserViewHolder extends RecyclerView.ViewHolder {

    RelativeLayout root;
    private CircleImageView userProfilePic;
    private TextView userName, userStatus;

    UserViewHolder(@NonNull View itemView) {
        super(itemView);
        root = itemView.findViewById(R.id.userDetail);
        userProfilePic = itemView.findViewById(R.id.userProfilePic);
        userName = itemView.findViewById(R.id.userName);
        userStatus = itemView.findViewById(R.id.userStatus);
    }

    void setUserProfilePic(String photoUri) {
        if(photoUri.equals("default")){
            userProfilePic.setImageResource(R.drawable.profile_image);
        } else {
            Picasso.get( ).load(Uri.parse(photoUri)).placeholder(R.drawable.profile_image).into(userProfilePic);
        }
    }

    void setUserName(String userName) {
        this.userName.setText(userName);
    }

    void setUserStatus(String userStatus) {
        this.userStatus.setText(userStatus);
    }
}
