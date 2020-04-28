package com.example.exam2020_certificateapp.helpers;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.example.exam2020_certificateapp.UserSettingsActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class PhotoHelper {
    Context mCont;
    Activity mActivity;
    PackageManager mPackageManager;
    int MY_PERMISSIONS_REQUEST_CAMERA;
    int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE;
    int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_IMAGE_UPLOAD = 2;
    StorageReference storageReference;
    FirebaseStorage storage;

    String mCurrentPhotoPath = "";
    public PhotoHelper(Context cont, Activity activity, PackageManager packageManager) {
        mCont = cont;
        mActivity = activity;
        mPackageManager = packageManager;
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
    }

    public boolean checkPermissions() {
        if (ContextCompat.checkSelfPermission(mCont, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            //Ask permission
            ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);
            return false;
        }
        if(ContextCompat.checkSelfPermission(mCont, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
            return false;
        }
        if (ContextCompat.checkSelfPermission(mCont, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            //Ask permission
            ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

            return false;
        }
        return true;
    }

    public Bitmap uploadToFirebase(Uri uri) {
        try {
            InputStream imageStream = mActivity.getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
            uploadImageToFirebase(uri);
            return bitmap;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }


    public void openCamera() {
        if(checkPermissions())
        {


            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(mPackageManager) != null) {
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(mCont, "Error saving image", Toast.LENGTH_LONG);
                }

                if(photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(mCont, "com.example.android.fileprovider", photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    mActivity.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        }
        }


    public void openGallery() {

            if(checkPermissions())
            {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                mActivity.startActivityForResult(galleryIntent, REQUEST_IMAGE_UPLOAD);
            }


    }

    public File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyymmdd_HHmmss").format(new Date());
        String imageFileName = "Certificate_" + timeStamp + "_";
        File storageDir = mActivity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        mCurrentPhotoPath = image.getAbsolutePath();

        return image;
    }


    void uploadImageToFirebase(Uri filepath) {
        final ProgressDialog progressDialog = new ProgressDialog(mCont);
        progressDialog.setTitle("Uploading");
        progressDialog.show();
        StorageReference ref = storageReference.child("images/" + UUID.randomUUID().toString());
        ref.putFile(filepath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                progressDialog.dismiss();
                Toast.makeText(mActivity, "Image has been uploaded succesfully ", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast
                        .makeText(mActivity,
                                "Failed " + e.getMessage(),
                                Toast.LENGTH_SHORT)
                        .show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                progressDialog.setMessage("Uploaded " + (int) progress + "%");
            }
        });
    }

    public String getmCurrentPhotoPath() {
        return mCurrentPhotoPath;
    }

}
