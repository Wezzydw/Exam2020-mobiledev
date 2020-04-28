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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;
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
    Object mUser;
    String mCurrentPhotoPath = "";

    int MY_PERMISSIONS_REQUEST_CAMERA;
    int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE;
    int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE;
    String mName = "";
    private FirebaseFirestore mDb;
    FirebaseStorage storage;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);

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

        Object mUser = (Object) getIntent().getSerializableExtra("usersomethinghere");

        if(mUser != null)
        {
            initializeDisplayOfData();
        }

    }

    void initializeDisplayOfData(){
        mEditTextEmail.setText("");
        mEditTextName.setText("");
        mEditTextPassword.setText("");
        mEditTextPhone.setText("");
        mEditTextUsername.setText("NoUserNameYet");
    }

    void openCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            //Ask permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);
        } else {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
            }

            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Error saving image", Toast.LENGTH_LONG);
                }

                if(photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(this, "com.example.android.fileprovider", photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }

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
            //Takes IMAGE from camera
            //Uri uri = data.getData();
            Uri uri = Uri.fromFile(new File(mCurrentPhotoPath));

            try {
                InputStream imageStream = getContentResolver().openInputStream(uri);
                Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
                mImageViewProfilePicture.setImageBitmap(bitmap);
                //addPictureToGallery();
                uploadImageToFirebase(uri);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            /*Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] test = stream.toByteArray();
            StorageReference ref = storageReference.child("images/" + UUID.randomUUID().toString());
            UploadTask uploadTask = ref.putBytes(test);
            Log.e("Original   dimensions", imageBitmap.getWidth()+" "+imageBitmap.getHeight());
            //Log.e("Compressed dimensions", decoded.getWidth()+" "+decoded.getHeight());
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(UserSettingsActivity.this, "IT WORKS", Toast.LENGTH_LONG).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(UserSettingsActivity.this, "Error upload camera image", Toast.LENGTH_LONG).show();
                }
            });
            mImageViewProfilePicture.setImageBitmap(imageBitmap);
            //uploadImageToFirebase(uri);*/

        } else if (requestCode == REQUEST_IMAGE_UPLOAD && resultCode == RESULT_OK) {
            //takes Image from storage/SD
            Uri uri = data.getData();
            try {
                InputStream imageStream = getContentResolver().openInputStream(uri);
                Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
                mImageViewProfilePicture.setImageBitmap(bitmap);
                uploadImageToFirebase(uri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }


    }

    void uploadImageToFirebase(Uri filepath) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading");
        progressDialog.show();
        StorageReference ref = storageReference.child("images/" + UUID.randomUUID().toString());
        ref.putFile(filepath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                progressDialog.dismiss();
                Toast.makeText(UserSettingsActivity.this, "Image has been uploaded succesfully ", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast
                        .makeText(UserSettingsActivity.this,
                                "Failed " + e.getMessage(),
                                Toast.LENGTH_SHORT)
                        .show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                progressDialog.setMessage("Uploaded" + (int) progress + " %");
            }
        });
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
        user.put("name", "mUser.name");
        user.put("username", "mUser.username");
        user.put("password", "mUser.password");
        user.put("email", "mUser.email");
        user.put("phone", "phone");


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

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyymmdd_HHmmss").format(new Date());
        String imageFileName = "Certificate_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        mCurrentPhotoPath = image.getAbsolutePath();

        return image;
    }

    private void addPictureToGallery() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri uri = Uri.fromFile(f);
        mediaScanIntent.setData(uri);
        this.sendBroadcast(mediaScanIntent);
    }

}
