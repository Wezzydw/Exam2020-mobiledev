package com.example.exam2020_certificateapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.exam2020_certificateapp.helpers.PhotoHelper;

import com.example.exam2020_certificateapp.helpers.PhotoHolder;
import com.example.exam2020_certificateapp.helpers.UploadCallBack;
import com.example.exam2020_certificateapp.model.User;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UserSettingsActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_IMAGE_UPLOAD = 2;
    Button mBtnTakePicture;
    Button mBtnGetGalleryPicture;
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
    Uri mCurrentImageUri;

    int MY_PERMISSIONS_REQUEST_CAMERA;
    int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE;
    int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE;
    String mName = "";
    private FirebaseFirestore mDb;
    FirebaseStorage storage;
    StorageReference storageReference;
    private byte[] byteArray;

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
                        deleteUserPrompt();
                        break;
                }
            }
        };

        mBtnTakePicture.setOnClickListener(buttons);
        mBtnGoBack.setOnClickListener(buttons);
        mBtnGetGalleryPicture.setOnClickListener(buttons);
        mBtnDeleteUser.setOnClickListener(buttons);

        mUser = (User) getIntent().getSerializableExtra("user");

        if (mUser != null) {
            initializeDisplayOfData();
        } else {
            finish();
        }

    }

    void initializeDisplayOfData() {
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
        if (uri != null) {
            mCurrentImageUri = uri;
            Bitmap bitmap = mPhotoHelper.getBitmap(uri);
            mImageViewProfilePicture.setImageBitmap(bitmap);

        }
    }

    void returnToActivity() {
        promptForSaveSettings();
        //Save all data changes to firebase


    }

    void deleteUserPrompt() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("DeleteUser");
        builder.setMessage("ARE YOU SURE YOU WANT TO DELETE YOUR ACCOUNT????!!?");
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
        Log.d("XYZ", mUser.getmUId());
        mDb.document("users/" + mUser.getmUId()).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("XYZ", "DocumentSnapshot successfully deleted!");
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        //destroy or make it so you can't get back in with the back button before you log in again.
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("XYZ", "Error deleting document", e);
                    }
                });
        //Delete certificates with functions
    }


    void saveSettings() {

        final User user = new User();
        user.setmEmail(mEditTextEmail.getText().toString());
        user.setmName(mEditTextName.getText().toString());
        user.setmUserName(mEditTextUsername.getText().toString());
        user.setmCertificateList(mUser.getmCertificateList());
        user.setmPhone(mEditTextPhone.getText().toString());
        user.setImage(mUser.getmImage());
        user.setmUId(mUser.getmUId());
        String path = "images/" + mUser.getmUId() + "/profilePicture";

        mPhotoHelper.uploadImageToFirebase(mCurrentImageUri, path, new UploadCallBack() {
            @Override
            public void onCallback(boolean state) {
                if (state == true) {
                    storageReference.child("images/" + mUser.getmUId() + "/profilePicture").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            //user.setImage(uri.);
                            Log.d("XYZAY", uri.toString());
                            user.setImage(uri.toString());
                            mDb.collection("users").document(mUser.getmUId()).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // photo helper breaks the app, I think it is because the toast you put in the activity and it switches activity too fast
                                    Toast succesSaving = Toast.makeText(UserSettingsActivity.this, "Succesfully Saved Changes", Toast.LENGTH_LONG);
                                    succesSaving.show();
                                    Log.d("XYZA", user.getmImage());
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast errorSavingChanges = Toast.makeText(UserSettingsActivity.this, "Error Saving Changes", Toast.LENGTH_LONG);
                                    errorSavingChanges.show();
                                }
                            });
                            Bitmap bitmap = mPhotoHelper.getBitmap(mCurrentImageUri);
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                            byteArray = stream.toByteArray();
                            PhotoHolder extras = PhotoHolder.getInstance();
                            Intent result = new Intent();
                            result.putExtra("updatedUser", user);
                            extras.putExtra("profilePic", byteArray);
                            setResult(RESULT_OK, result);

                            finish();
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        promptForSaveSettings();
    }


    void promptForSaveSettings() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Savesettings");
        builder.setMessage("Save changes?");
        builder.setPositiveButton("YeS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveSettings();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("No sir", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();

            }
        });
        AlertDialog alert = builder.create();
        alert.show();
}

}
