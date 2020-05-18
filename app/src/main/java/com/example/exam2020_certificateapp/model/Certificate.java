package com.example.exam2020_certificateapp.model;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.Date;

public class Certificate implements Serializable {
    private String mExpirationDate;
    private byte[] mBitmap;
    private String mName;
    private String mUId;
    private String mUserUid;

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

    public String getmUserUid() { return mUserUid; }

    public void setmUserUid(String mUserUid) { this.mUserUid = mUserUid; }

    public Certificate(String mExpirationDate, byte[] mImageUrl, String mName, String mUId, String mUserUid) {
        this.mExpirationDate = mExpirationDate;
        this.mBitmap = mImageUrl;
        this.mName = mName;
        this.mUId = mUId;
        this.mUserUid = mUserUid;
    }
    public Certificate(String mExpirationDate, String mName) {
        this.mExpirationDate = mExpirationDate;
        this.mName = mName;
    }
}
