package com.example.exam2020_certificateapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.exam2020_certificateapp.helpers.PhotoHelper;
import com.example.exam2020_certificateapp.model.Certificate;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class CertificateCEActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    PhotoHelper mPhotoHelper;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_IMAGE_UPLOAD = 2;
    ImageView mImageView;
    private FirebaseFirestore mDb;
    TextView dateText;
    TextView mTextCertName;
    Certificate mCert;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_certificate_c_e);
        mImageView = findViewById(R.id.cceImageView);
        mDb = FirebaseFirestore.getInstance();
        dateText = (TextView) findViewById(R.id.cceTWDate);
        mCert = (Certificate) getIntent().getSerializableExtra("usersomethinghere");
        mTextCertName = findViewById(R.id.cceETCertName);


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
        Button mBtnSave = findViewById(R.id.cceBtnSave);
        mBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });

        Button btnPickdate = (Button) findViewById(R.id.cceBtnDatePicker);
        btnPickdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });


        if(mCert != null)
        {
            initializeDisplayOfData();
            mBtnSave.setText("Save Changes");
        }
        else {
            mBtnSave.setText("Create new Certificate");
        }


    }

    private void save() {
        Map<String, Object> certificate = new HashMap<>();
        certificate.put("name", mTextCertName.getText());
        certificate.put("expDate", "certificate.expDate");
        mDb.collection("certificates").document().set(certificate).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast succesSaving = Toast.makeText(CertificateCEActivity.this, "Succesfully Saved Changes", Toast.LENGTH_LONG);
                succesSaving.show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast errorSavingChanges = Toast.makeText(CertificateCEActivity.this, "Error Saving Changes", Toast.LENGTH_LONG);
                errorSavingChanges.show();
            }
        });
    }

    void promptForSaveSettings() {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Savesettings");
            builder.setMessage("Save changes?");
            builder.setPositiveButton("YeS", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    save();
                    finish();
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton("No sir", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                    dialog.dismiss();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
    }

    void initializeDisplayOfData(){
        Calendar c = Calendar.getInstance();
        //use real data here
        c.set(Calendar.YEAR, 2021);
        c.set(Calendar.MONTH, 4);
        c.set(Calendar.DAY_OF_MONTH, 28);
        String expirationDate = DateFormat.getDateInstance().format(c.getTime());

        //ved ikke lige helt hvad der skal gøres med expdate
        dateText.setText("Expiration Date: " + expirationDate);
        mTextCertName.setText(mCert.getmName());

    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String expirationDate = DateFormat.getDateInstance().format(c.getTime());


        dateText.setText("Expiration Date: " + expirationDate);
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
    @Override
    public void onBackPressed() {
        promptForSaveSettings();
    }
}
