package com.example.exam2020_certificateapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.exam2020_certificateapp.model.Certificate;

import java.util.ArrayList;
import java.util.List;

public class CustomAdapter extends BaseAdapter {
    LayoutInflater inflter;
    List<Certificate> certificates;

    public CustomAdapter(Context applicationContext, ArrayList<Certificate> certificates) {
        this.certificates = certificates;
        inflter = (LayoutInflater.from(applicationContext));
        Log.d("XYZ", "customAdapter" + certificates.size());
    }

    @Override
    public int getCount() {
        return certificates.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
        //return certificate;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        Log.d("XYZ", "customAdapter" + position + certificates.get(position).getmName());
        view = inflter.inflate(R.layout.certificate_list_view, null);
        ImageView certImage = (ImageView) view.findViewById(R.id.imageCertificate);
        TextView certExpirationDate = (TextView) view.findViewById(R.id.textExpirationDate);
        TextView certName = (TextView) view.findViewById(R.id.textName);

        byte[] byteArray = certificates.get(position).getmBitmap();
        Bitmap image = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        certImage.setImageBitmap(image);
        certExpirationDate.setText(certificates.get(position).getmExpirationDate().toString());
        certName.setText(certificates.get(position).getmName());
        return view;
    }
}
