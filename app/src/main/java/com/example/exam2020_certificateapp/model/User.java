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
    private String[] mCertificateList;

    public User() {

    }

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

    public String[] getmCertificateList() {
        return mCertificateList;
    }

    public void setmCertificateList(String[] mCertificateList) {
        this.mCertificateList = mCertificateList;
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
}
