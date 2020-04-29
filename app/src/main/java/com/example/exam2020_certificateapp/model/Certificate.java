package com.example.exam2020_certificateapp.model;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.Date;

public class Certificate implements Serializable {
    private Date mExpirationDate;
    private byte[] mBitmap;
    private String mName;

    public Certificate() {

    }

    public Date getmExpirationDate() {
        return mExpirationDate;
    }

    public void setmExpirationDate(Date mExpirationDate) {
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

    public Certificate(Date mExpirationDate,  byte[] mImageUrl, String mName) {
        this.mExpirationDate = mExpirationDate;
        this.mBitmap = mImageUrl;
        this.mName = mName;
    }
    public Certificate(Date mExpirationDate, String mName) {
        this.mExpirationDate = mExpirationDate;
        this.mName = mName;
    }
}
