package com.example.exam2020_certificateapp.model;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.Date;

public class Certificate implements Serializable {
    private String mExpirationDate;
    private byte[] mBitmap;
    private String mName;
    private String mUId;
    private String mPhoto;

    public Certificate() {

    }

    public String getmExpirationDate() {
        return mExpirationDate;
    }

    public void setmExpirationDate(String mExpirationDate) {
        this.mExpirationDate = mExpirationDate;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public  byte[] getmBitmap() {
        return mBitmap;
    }

    public void setmBitmap( byte[] mBitmap) {
        this.mBitmap = mBitmap;
    }

    public String getmUId() {
        return mUId;
    }

    public void setmUId(String mUId) {
        this.mUId = mUId;
    }

    public String getmPhoto() {return mPhoto;}

    public void setmPhoto(String photo) {this.mPhoto = photo;}
    public Certificate(String mExpirationDate, byte[] mImageUrl, String mName, String mUId) {
        this.mExpirationDate = mExpirationDate;
        this.mBitmap = mImageUrl;
        this.mName = mName;
        this.mUId = mUId;
    }
    public Certificate(String mExpirationDate, String mName) {
        this.mExpirationDate = mExpirationDate;
        this.mName = mName;
    }
}
