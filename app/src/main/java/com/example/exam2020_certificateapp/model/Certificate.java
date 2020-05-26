package com.example.exam2020_certificateapp.model;

import java.io.Serializable;

public class Certificate implements Serializable {
    private String mExpirationDate;
    private String mName;
    private String mUId;
    private String mUserUid;
    private String mPhoto;

    public Certificate() {
    }

    public String getmExpirationDate() {
        return mExpirationDate;
    }
    public String getmPhoto () {
        return mPhoto;
    }
    public String getmUserUid() {
        return mUserUid;
    }
    public String getmName() {
        return mName;
    }
    public String getmUId() {
        return mUId;
    }

    public void setmUId(String mUId) {
        this.mUId = mUId;
    }
    public void setmName(String mName) {
        this.mName = mName;
    }
    public void setmUserUid(String mUserUid) {
        this.mUserUid = mUserUid;
    }
    public void setmExpirationDate(String mExpirationDate) {
        this.mExpirationDate = mExpirationDate;
    }
    public void setmPhoto (String photo){
        this.mPhoto = photo;
    }
}
