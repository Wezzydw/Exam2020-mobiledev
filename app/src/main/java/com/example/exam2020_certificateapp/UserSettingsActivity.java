package com.example.exam2020_certificateapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.exam2020_certificateapp.helpers.PhotoHelper;

import com.example.exam2020_certificateapp.model.User;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UserSettingsActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_IMAGE_UPLOAD = 2;
    FloatingActionButton mBtnTakePicture;
    FloatingActionButton mBtnGetGalleryPicture;
    Button mBtnDeleteUser;
    ImageView mImageViewProfilePicture;
    ImageButton mBtnGoBack;
    EditText mEditTextName;
    EditText mEditTextUsername;
    EditText mEditTextPassword;
    EditText mEditTextPhone;
    EditText mEditTextEmail;
    User mUser;
    String mCurrentPhotoPath = "";

    int MY_PERMISSIONS_REQUEST_CAMERA;
    int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE;
    int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE;
    String mName = "";
    private FirebaseFirestore mDb;
    FirebaseStorage storage;
    StorageReference storageReference;

    private PhotoHelper mPhotoHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);

        mPhotoHelper = new PhotoHelper(this, this, getPackageManager());
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        mDb = FirebaseFirestore.getInstance();

        mBtnTakePicture = findViewById(R.id.settingsBtnTakePicture);
        mBtnGetGalleryPicture = findViewById(R.id.settingsBtnUploadPicture);
        mImageViewProfilePicture = findViewById(R.id.settingsImageView);
        mBtnGoBack = findViewById(R.id.settingsBtnReturn);
        mBtnDeleteUser = findViewById(R.id.settingsBtnDeleteUser);

        mEditTextName = findViewById(R.id.settingsInputName);
         mEditTextUsername = findViewById(R.id.settingsInputUsername);
         mEditTextPassword = findViewById(R.id.settingsInputPassword);
         mEditTextPhone = findViewById(R.id.settingsInputPhone);
         mEditTextEmail = findViewById(R.id.settingsInputEmail);
        View.OnClickListener buttons = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = v.getId();
                switch (id) {
                    case R.id.settingsBtnReturn:
                        returnToActivity();
                        break;
                    case R.id.settingsBtnTakePicture:
                        helperOpenCamera();
                        break;
                    case R.id.settingsBtnUploadPicture:
                        helperOpenGallery();
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

        mUser = (User) getIntent().getSerializableExtra("usersomethinghere");

        if(mUser != null)
        {
            initializeDisplayOfData();
        }
        else {
            finish();
        }

    }

    void initializeDisplayOfData(){
        mEditTextEmail.setText(mUser.getmEmail());
        mEditTextName.setText(mUser.getmName());
        mEditTextPassword.setText("");
        mEditTextPhone.setText(mUser.getmPhone());
        mEditTextUsername.setText(mUser.getmUserName());
    }


    void helperOpenCamera() {
            mPhotoHelper.openCamera();
    }
    void helperOpenGallery() {
            mPhotoHelper.openGallery();
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
                    mImageViewProfilePicture.setImageBitmap(bitmap);
            }
    }
    void returnToActivity() {
        promptForSaveSettings();
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
    }

    void fireBaseDeleteAccount() {
        mDb.collection("user").document("mUserUID").delete();
        //Delete certificates with functions
    }


    void saveSettings() {
        //save settings
        Map<String, Object> user = new HashMap<>();
        user.put("uid", mUser.getmUId());

        user.put("name", mEditTextName.getText().toString());
        user.put("username", mEditTextUsername.getText().toString());
        user.put("password", mEditTextPassword.getText().toString());
        user.put("email", mEditTextEmail.getText().toString());
        user.put("phone", mEditTextPhone.getText().toString());


        mDb.collection("users").document().set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast succesSaving = Toast.makeText(UserSettingsActivity.this, "Succesfully Saved Changes", Toast.LENGTH_LONG);
                succesSaving.show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast errorSavingChanges = Toast.makeText(UserSettingsActivity.this, "Error Saving Changes", Toast.LENGTH_LONG);
                errorSavingChanges.show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        promptForSaveSettings();
    }


    void promptForSaveSettings() {
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
                    finish();
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
