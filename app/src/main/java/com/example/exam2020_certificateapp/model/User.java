package com.example.exam2020_certificateapp.model;

import java.io.Serializable;

public class User implements Serializable {
    private String mUserName;
    private String mEmail;
    private String mUId;
    private String mName;
    private String mPhone;
    private String mImageUrl;

    public User() {
    }

    public User(String name, String email, String uId, String userName) {
        mName = name;
        mEmail = email;
        mUId = uId;
        mUserName = userName;
        mImageUrl = null;
    }

    public String getmImageUrl() {
        return mImageUrl;
    }

    public void setmImageUrl(String mImageUrl) {
        this.mImageUrl = mImageUrl;
    }

    public String getmUId() {
        return mUId;
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

    public void setmEmail(String mEmail) {
        this.mEmail = mEmail;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public void setmUserName(String mUserName) {
        this.mUserName = mUserName;
    }

    public void setmUId(String mUId) {
        this.mUId = mUId;
    }

    public void setmPhone(String mPhone) {
        this.mPhone = mPhone;
    }

}
