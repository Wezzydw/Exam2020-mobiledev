package com.example.exam2020_certificateapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class UserSettingsActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_IMAGE_UPLOAD = 2;
    FloatingActionButton mBtnTakePicture;
    FloatingActionButton mBtnGetGalleryPicture;
    Button mBtnDeleteUser;
    ImageView mImageViewProfilePicture;
    ImageButton mBtnGoBack;
    EditText mEditTextName;
    int MY_PERMISSIONS_REQUEST_CAMERA;
    int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE;
    String mName = "";
   // FirebaseFirestore mDb = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);

        mBtnTakePicture = findViewById(R.id.settingsBtnTakePicture);
        mBtnGetGalleryPicture = findViewById(R.id.settingsBtnUploadPicture);
        mImageViewProfilePicture = findViewById(R.id.settingsImageView);
        mBtnGoBack = findViewById(R.id.settingsBtnReturn);
        mBtnDeleteUser = findViewById(R.id.settingsBtnDeleteUser);
        mEditTextName = findViewById(R.id.settingsInputName);


        View.OnClickListener buttons = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = v.getId();
                switch (id) {
                    case R.id.settingsBtnReturn:
                        returnToActivity();
                        break;
                    case R.id.settingsBtnTakePicture:
                        openCamera();
                        break;
                    case R.id.settingsBtnUploadPicture:
                        uploadImage();
                        break;
                    case R.id.settingsBtnDeleteUser:
                        deleteUserPromt();
                        break;
                }
            }
        };

        mBtnTakePicture.setOnClickListener(buttons);
        mBtnGoBack.setOnClickListener(buttons);
        mBtnGetGalleryPicture.setOnClickListener(buttons);
        mBtnDeleteUser.setOnClickListener(buttons);

    }

    void openCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            //Ask permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);
        } else {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    void uploadImage() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            //Ask permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        } else {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent, REQUEST_IMAGE_UPLOAD);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            mImageViewProfilePicture.setImageBitmap(imageBitmap);

            //Upload to firebase here
        } else if (requestCode == REQUEST_IMAGE_UPLOAD && resultCode == RESULT_OK) {

            Uri uri = data.getData();
            try {
                InputStream imageStream = getContentResolver().openInputStream(uri);
                Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
                mImageViewProfilePicture.setImageBitmap(bitmap);
                //upload bitmap
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

    }

    void returnToActivity() {
        promtForSaveSettings();
        //Save all data changes to firebase


    }

    void deleteUserPromt() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("DeleteUser");
        builder.setMessage("ARE YUO SURE YOY WAN TTO DELETE YOUR ACOUNT????!!?");
        builder.setPositiveButton("YeS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                fireBaseDeleteAccount();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("No sir", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    void fireBaseDeleteAccount() {
        //editTextName.setText("Virker");
        //Delete user from firebase here
    }


    void saveSettings() {
        //editTextName.setText("Changes Saved");
    }

    void promtForSaveSettings() {
        //editTextName.setText("Checking save");
        if (!mName.equals(mEditTextName.getText().toString())) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Savesettings");
            builder.setMessage("Save changes?");
            builder.setPositiveButton("YeS", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    saveSettings();
                    finish();
                    dialog.dismiss();

                }
            });
            builder.setNegativeButton("No sir", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        } else {
            finish();
        }
    }

}
