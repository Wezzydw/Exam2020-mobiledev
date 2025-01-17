package com.example.exam2020_certificateapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import com.example.exam2020_certificateapp.helpers.DownloadImageTask;
import com.example.exam2020_certificateapp.helpers.PhotoHelper;
import com.example.exam2020_certificateapp.helpers.UploadCallBack;
import com.example.exam2020_certificateapp.model.User;
import com.example.exam2020_certificateapp.swipe.OnSwipeListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.io.File;
import java.io.FileNotFoundException;

public class UserSettingsActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_IMAGE_UPLOAD = 2;
    ImageView mImageViewProfilePicture; //Image view for profile picture
    EditText mEditTextName; // Edit text for users name
    EditText mEditTextUsername; // Edit text for users username
    EditText mEditTextPhone; // Edit text for users phonenumber
    EditText mEditTextEmail; // Edit text for users email
    User mUser; // Selected user
    Uri mCurrentImageUri; // current image URI
    private FirebaseAuth mAuth; // Firebase authentication
    private FirebaseFirestore mDb; // Firebase firestore
    FirebaseStorage storage; // Firebase storage
    StorageReference storageReference; // storage reference
    private PhotoHelper mPhotoHelper; // class to help with getting images from urls

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);
        mAuth = FirebaseAuth.getInstance();
        mPhotoHelper = new PhotoHelper(this, this, getPackageManager());
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        mDb = FirebaseFirestore.getInstance();
        Button mBtnTakePicture = findViewById(R.id.settingsBtnTakePicture);
        Button mBtnGetGalleryPicture = findViewById(R.id.settingsBtnUploadPicture);
        mImageViewProfilePicture = findViewById(R.id.settingsImageView);
        ImageButton mBtnGoBack = findViewById(R.id.settingsBtnReturn);
        Button mBtnDeleteUser = findViewById(R.id.settingsBtnDeleteUser);
        mEditTextName = findViewById(R.id.settingsInputName);
        mEditTextUsername = findViewById(R.id.settingsInputUsername);
        mEditTextPhone = findViewById(R.id.settingsInputPhone);
        mEditTextEmail = findViewById(R.id.settingsInputEmail);
        View.OnClickListener buttons = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = v.getId();
                switch (id) {
                    case R.id.settingsBtnReturn:
                        promptForSaveSettings();
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

        if (mUser == null) {
            finish();
        }
        initializeDisplayOfData();
        View view = getWindow().getDecorView();
        //Sets on swipelistener for the view, which will prompt for save
        view.setOnTouchListener(new OnSwipeListener(this) {
            @Override
            public void onSwipeLeft() {
                promptForSaveSettings();
            }
        });
    }

    /**
     * takes mUser data and attach it to text fields and profilepicture
     */

    void initializeDisplayOfData() {
        mEditTextEmail.setText(mUser.getmEmail());
        mEditTextName.setText(mUser.getmName());
        mEditTextPhone.setText(mUser.getmPhone());
        mEditTextUsername.setText(mUser.getmUserName());
        if (mUser.getmImageUrl() != null) {
            new DownloadImageTask((ImageView) mImageViewProfilePicture).execute(mUser.getmImageUrl());
        }
    }

    /**
     * Calls mphotohelper to open the camera
     */
    void helperOpenCamera() {
        mPhotoHelper.openCamera();
    }

    /**
     * Calls mphotohelper to open the gallery
     */
    void helperOpenGallery() {
        mPhotoHelper.openGallery();
    }

    /**
     * Whenever an activity finishes this method is called, then it uses requestcode to determine what view
     * and if resultcode is RESULT_OK, then data can be used to retrieve information
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri uri = null;
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            //takes image from camera app
            uri = Uri.fromFile(new File(mPhotoHelper.getmCurrentPhotoPath()));
        } else if (requestCode == REQUEST_IMAGE_UPLOAD && resultCode == RESULT_OK) {
            //takes Image from storage/SD
            uri = data.getData();
        }
        if (uri != null) {
            mCurrentImageUri = uri;
            Bitmap bitmap = null;
            try {
                bitmap = mPhotoHelper.getBitmap(uri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            mImageViewProfilePicture.setImageBitmap(bitmap);
        }
    }

    /**
     * Alerts the user if they are sure that they want to delete their account
     */
    void deleteUserPrompt() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete User");
        builder.setMessage("ARE YOU SURE YOU WANT TO DELETE YOUR ACCOUNT?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                fireBaseDeleteAccount();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * Deletes the logged in user in the firebase database
     * if completed closes the activity
     */
    void fireBaseDeleteAccount() {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        firebaseUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Intent result = new Intent();
                result.putExtra("delete", true);
                setResult(RESULT_OK, result);
                finish();
            }
        });
    }

    /**
     * Saves the settings that has been entered in the different textfields and attaches it to the user
     * Also checks if an image has been chosen by the user
     * then calls upload to firebase
     */
    void saveSettings() {
        final User user = new User();
        user.setmEmail(mEditTextEmail.getText().toString());
        user.setmName(mEditTextName.getText().toString());
        user.setmUserName(mEditTextUsername.getText().toString());
        user.setmPhone(mEditTextPhone.getText().toString());
        user.setmImageUrl(mUser.getmImageUrl());
        user.setmUId(mUser.getmUId());
        String path = "images/" + mUser.getmUId() + "/profilePicture";
        if (mCurrentImageUri != null) {
            mPhotoHelper.uploadImageToFirebase(mCurrentImageUri, path, new UploadCallBack() {
                @Override
                public void onCallback(boolean state) {
                    if (state == true) {
                        storageReference.child("images/" + mUser.getmUId() + "/profilePicture").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                //if image is successfully
                                user.setmImageUrl(uri.toString());
                                saveInFirebase(user);
                            }
                        });
                    }
                }
            });
        } else {
            saveInFirebase(user);
        }

    }

    /**
     * Takes a user and saves it in firebase
     *
     * @param user
     */
    private void saveInFirebase(final User user) {
        mDb.collection("users").document(user.getmUId()).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast succesSaving = Toast.makeText(UserSettingsActivity.this, "Succesfully Saved Changes", Toast.LENGTH_LONG);
                succesSaving.show();
                Intent result = new Intent();
                result.putExtra("updatedUser", user);
                setResult(RESULT_OK, result);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast errorSavingChanges = Toast.makeText(UserSettingsActivity.this, "Error Saving Changes", Toast.LENGTH_LONG);
                errorSavingChanges.show();
            }
        });
    }

    /**
     * Overrides the method for the back button, to prompt the user a change to save changes
     */
    @Override
    public void onBackPressed() {
        promptForSaveSettings();
    }

    /**
     * Makes a popup when trying to leave the activity, which asks for whether
     * or not the user wants to save the data changes
     */
    void promptForSaveSettings() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Save settings");
        builder.setMessage("Save changes?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
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
