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

import com.example.exam2020_certificateapp.helpers.DownloadImageTask;
import com.example.exam2020_certificateapp.helpers.PhotoHolder;
import com.example.exam2020_certificateapp.model.Certificate;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class CustomAdapter extends BaseAdapter {
    LayoutInflater inflter; // Gain access to the LayoutInflater.
    List<Certificate> certificates; // List of users certificates.
    private PhotoHolder mPhotoHolder; // Gain access to the PhotoHolder class.


    public CustomAdapter(Context applicationContext, ArrayList<Certificate> certificates) {
        this.certificates = certificates;
        inflter = (LayoutInflater.from(applicationContext));
        mPhotoHolder = PhotoHolder.getInstance();
    }

    /**
     * Gets the number of elements in the list of certificates.
     * @return the size of the list as an Integer.
     */
    @Override
    public int getCount() {
        return certificates.size();
    }

    /**
     * Gets the element of the Certificate list at position.
     * @param position represents the index in the list.
     * @return an Object of the Certificate.
     */
    @Override
    public Object getItem(int position) {
        return certificates.get(position);
    }

    /**
     * This method is not overridden.
     * @param position
     * @return
     */
    @Override
    public long getItemId(int position) {
        return 0;
    }

    /**
     * Gets the view of the selected certificate, at the position in the list of certificates.
     * Every Certificate is a View of the parent ViewGroup.
     * @param position The position is the element in the list.
     * @param view The view selected when pressed.
     * @param parent Is the list of Views.
     * @return The view of selected certificate.
     */
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        view = inflter.inflate(R.layout.certificate_list_view, null);
        ImageView certImage = (ImageView) view.findViewById(R.id.imageCertificate);
        TextView certExpirationDate = (TextView) view.findViewById(R.id.textExpirationDate);
        TextView certName = (TextView) view.findViewById(R.id.textName);

        certImage.setImageBitmap((Bitmap) mPhotoHolder.getExtra("bitmap" +certificates.get(position).getmUId()));
        certExpirationDate.setText(certificates.get(position).getmExpirationDate().toString());
        certName.setText(certificates.get(position).getmName());
        return view;
    }
}
