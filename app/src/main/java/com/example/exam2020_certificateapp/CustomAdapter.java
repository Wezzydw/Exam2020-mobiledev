package com.example.exam2020_certificateapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.exam2020_certificateapp.model.Certificate;

public class CustomAdapter extends BaseAdapter {
    LayoutInflater inflter;
    Certificate[] certificates;

    public CustomAdapter(Context applicationContext, Certificate[] certificates) {
        this.certificates = certificates;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return 0;
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
        view = inflter.inflate(R.layout.certificate_list_view, null);
        ImageView certImage = (ImageView) view.findViewById(R.id.imageCertificate);
        TextView certExpirationDate = (TextView) view.findViewById(R.id.textExpirationDate);
        TextView certName = (TextView) view.findViewById(R.id.textName);

        // certImage.setImageBitmap();
        certExpirationDate.setText(certificates[position].getmExpirationDate().toString());
        certName.setText(certificates[position].getmName());
        return view;
    }
}
