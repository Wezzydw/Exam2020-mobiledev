package com.example.exam2020_certificateapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.exam2020_certificateapp.helpers.PhotoHelper;

import java.io.File;
import java.text.DateFormat;
import java.util.Calendar;

public class CertificateCEActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    PhotoHelper mPhotoHelper;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_IMAGE_UPLOAD = 2;
    ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_certificate_c_e);
        mImageView = findViewById(R.id.cceImageView);

        mPhotoHelper = new PhotoHelper(this, this, getPackageManager());

        Button mBtnTakePicture = findViewById(R.id.cceBtnTakePic);
        mBtnTakePicture.setOnClickListener(new View.OnClickListener() {
                                               @Override
                                               public void onClick(View v) {
                                                   mPhotoHelper.openCamera();
                                               }
                                           }
        );

        Button mBtnPictureFromPhone = findViewById(R.id.cceBtnPicFromLib);
        mBtnPictureFromPhone.setOnClickListener(new View.OnClickListener() {
                                               @Override
                                               public void onClick(View v) {
                                                   mPhotoHelper.openGallery();
                                               }
                                           }
        );


        Button btnPickdate = (Button) findViewById(R.id.cceBtnDatePicker);
        btnPickdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String expirationDate = DateFormat.getDateInstance().format(c.getTime());

        TextView textView = (TextView) findViewById(R.id.cceTWDate);
        textView.setText("Expiration Date: " + expirationDate);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Uri uri = null;
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            uri = Uri.fromFile(new File(mPhotoHelper.getmCurrentPhotoPath()));
        } else if (requestCode == REQUEST_IMAGE_UPLOAD && resultCode == RESULT_OK) {
            //takes Image from storage/SD
            uri = data.getData();
        }
        if(uri != null) {
            Bitmap bitmap = mPhotoHelper.uploadToFirebase(uri);
            mImageView.setImageBitmap(bitmap);
        }
    }
}
