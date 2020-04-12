package com.example.chatapp;

import android.net.Uri;
import android.view.View;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

class UserViewHolder extends RecyclerView.ViewHolder {

    CircleImageView userProfilePic;
    TextView userName, userStatus;

    UserViewHolder(@NonNull View itemView) {
        super(itemView);
        userProfilePic = itemView.findViewById(R.id.userProfilePic);
        userName = itemView.findViewById(R.id.userName);
        userStatus = itemView.findViewById(R.id.userStatus);
    }

    public void setUserProfilePic(String photoUri) {
        Picasso.get().load(Uri.parse(photoUri)).into(userProfilePic);
    }

    public void setUserName(String userName) {
        this.userName.setText(userName);
    }

    public void setUserStatus(String userStatus) {
        this.userStatus.setText(userStatus);
    }
}
