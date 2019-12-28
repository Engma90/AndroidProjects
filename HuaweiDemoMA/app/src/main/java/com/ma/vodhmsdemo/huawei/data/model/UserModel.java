package com.ma.vodhmsdemo.huawei.data.model;

import android.graphics.Bitmap;

public class UserModel {
    private String id;
    private String name;
    private String photoUrl;
    private boolean loggedInBefore;
    private Bitmap photo;

    public UserModel(String id, String name, String photoUrl , Bitmap photo) {
        this.id = id;
        this.name = name;
        this.photoUrl = photoUrl;
        this.photo = photo;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Bitmap getPhoto() {
        return photo;
    }

    public void setPhoto(Bitmap photo) {
        this.photo = photo;
    }

    public boolean isLoggedInBefore() {
        return loggedInBefore;
    }

    public void setLoggedInBefore(boolean loggedInBefore) {
        this.loggedInBefore = loggedInBefore;
    }

}
