package com.example.exam2020_certificateapp.model;

import android.media.Image;

import java.io.Serializable;
import java.util.Date;

public class Certificate implements Serializable {
    private Date mExpirationDate;
    private Image mCertificatePicture;
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

    public Certificate(Date mExpirationDate, Image mCertificatePicture, String mName) {
        this.mExpirationDate = mExpirationDate;
        this.mCertificatePicture = mCertificatePicture;
        this.mName = mName;
    }
}
