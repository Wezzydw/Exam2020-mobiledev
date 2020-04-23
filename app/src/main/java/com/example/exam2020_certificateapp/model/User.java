package com.example.exam2020_certificateapp.model;

import android.media.Image;

import java.io.Serializable;

public class User implements Serializable {
    private String mUserName;
    private String mEmail;
    private String mUId;
    private String mName;
    private String mPhone;
    private Image mImage;

    public User(String name, String email, String uId, String userName) {
        mName = name;
        mEmail = email;
        mUId = uId;
        mUserName = userName;
        mImage = null;
    }

    public void setImage(Image image) {
        mImage = image;
    }

    public void setPhone(String phone) {
        mPhone = phone;
    }

    public String getmUId() {
        return mUId;
    }

    public Image getmImage() {
        return mImage;
    }

    public String getmEmail() {
        return mEmail;
    }

    public String getmName() {
        return mName;
    }

    public String getmPhone() {
        return mPhone;
    }

    public String getmUserName() {
        return mUserName;
    }
}
