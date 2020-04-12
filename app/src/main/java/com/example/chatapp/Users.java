package com.example.chatapp;

public class Users {
    private String name,img,status;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Users() {
    }

    public Users(String name, String img, String status) {
        this.name = name;
        this.img = img;
        this.status = status;
    }
}
