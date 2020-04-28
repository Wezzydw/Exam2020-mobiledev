package com.example.exam2020_certificateapp.model;

import android.graphics.Bitmap;
import android.media.Image;

import java.io.Serializable;
import java.util.Date;

public class Certificate implements Serializable {
    private Date mExpirationDate;
    private Image mCertificatePicture;
    private Bitmap mBitmap;
    private String mName;

    public Certificate() {

    }

    public Date getmExpirationDate() {
        return mExpirationDate;
    }

    public void setmExpirationDate(Date mExpirationDate) {
        this.mExpirationDate = mExpirationDate;
    }

    public Image getmCertificatePicture() {
        return mCertificatePicture;
    }

    public void setmCertificatePicture(Image mCertificatePicture) {
        this.mCertificatePicture = mCertificatePicture;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public Bitmap getmBitmap() {
        return mBitmap;
    }

    public void setmBitmap(Bitmap mBitmap) {
        this.mBitmap = mBitmap;
    }

    public Certificate(Date mExpirationDate, Bitmap mImageUrl, String mName) {
        this.mExpirationDate = mExpirationDate;
        this.mBitmap = mImageUrl;
        this.mName = mName;
    }
}
