package com.example.exam2020_certificateapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.exam2020_certificateapp.helpers.DownloadImageTask;
import com.example.exam2020_certificateapp.helpers.PhotoHelper;
import com.example.exam2020_certificateapp.helpers.PhotoHolder;
import com.example.exam2020_certificateapp.helpers.UploadCallBack;
import com.example.exam2020_certificateapp.model.Certificate;
import com.example.exam2020_certificateapp.swipe.OnSwipeListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class CertificateCEActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_IMAGE_UPLOAD = 2;

    ImageView mImageView; //Imageview for certificate image
    private FirebaseFirestore mDb; //Firestore database connection
    TextView dateText; //Textview for date
    TextView mTextCertName; //Textview for certificatename
    Certificate mCert; //Placeholder for current selected certificate if updating certificate
    Bitmap mBitmap; //Placeholder for bitmap of selected image from phone
    Uri mCurrentImageUri; //Placeholder URI for currently selected image
    private FirebaseAuth mAuth; //Firebase authentication connection
    FirebaseStorage storage; //Firebase storage connection
    StorageReference storageReference; //Reference for Firebase storage
    PhotoHelper mPhotoHelper; //Reference to Photohelper class

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_certificate_c_e);
        mImageView = findViewById(R.id.cceImageView);
        mDb = FirebaseFirestore.getInstance();
        dateText = findViewById(R.id.cceTWDate);
        mCert = (Certificate) getIntent().getSerializableExtra("cert");
        mTextCertName = findViewById(R.id.cceETCertName);
        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        mPhotoHelper = new PhotoHelper(this, this, getPackageManager());

        initializeButtons();

        //Initializes the onSwipeListener and on swipeleft asks the user to save settings before leaving
        View view = getWindow().getDecorView();
        view.setOnTouchListener(new OnSwipeListener(this) {
            @Override
            public void onSwipeLeft() {
                promptForSaveSettings();
            }
        });

    }

    /**
     * Initializes buttons and their onClicklisteners
     */
    private void initializeButtons() {
        Button mBtnDelete = findViewById(R.id.cceBtnDelete);
        mBtnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteCertificate();
            }
        });
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
        Button btnPickdate = findViewById(R.id.cceBtnDatePicker);
        btnPickdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });
        if (mCert != null) {
            initializeDisplayOfData();
            mBtnSave.setText("Save Changes");
        } else {
            mBtnSave.setText("Create new Certificate");
        }
    }

    /**
     * When called, creates a fresh certificate and fills it with data.
     * Specific data depends on what "state" the activity is in either Create or Update
     */
    private void save() {
        final String path = "images/" + mAuth.getCurrentUser().getUid() + "/certificates/";
        final Certificate certificate;
        //If the activity has entered as update
        if (mCert != null) {
            certificate = mCert;
        }
        // The Activity has entered as Create New
        else {
            certificate = new Certificate();
            certificate.setmUId(UUID.randomUUID().toString());
            certificate.setmUserUid(mAuth.getCurrentUser().getUid());
        }
        certificate.setmExpirationDate(dateText.getText().toString());
        certificate.setmExpirationDate(dateText.getText().toString());
        certificate.setmName(mTextCertName.getText().toString());

        //If an image has been chosen, then it uploads that to the database
        if (mCurrentImageUri != null) {
            mPhotoHelper.uploadImageToFirebase(mCurrentImageUri, path + certificate.getmUId(), new UploadCallBack() {
                //Uses an onCallback class to ensure that the image is uploaded before the code proceeds
                @Override
                public void onCallback(boolean state) {
                    if (state == true) {
                        //Then gets the url for the newly uploaded image
                        storageReference.child("images/" + certificate.getmUserUid() + "/certificates/" + certificate.getmUId()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                certificate.setmPhoto(uri.toString());
                                saveInFirebase(certificate);
                            }
                        });
                    }
                }
            });
        } else {
            saveInFirebase(certificate);
        }
    }

    /**
     * Takes certificate and saves that in firebase
     * @param certificate
     */
    void saveInFirebase(final Certificate certificate) {
        mDb.collection("certificates").document(certificate.getmUId()).set(certificate).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast successSaving = Toast.makeText(CertificateCEActivity.this, "Successfully Saved Changes", Toast.LENGTH_LONG);
                successSaving.show();
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast errorSavingChanges = Toast.makeText(CertificateCEActivity.this, "Error Saving Changes", Toast.LENGTH_LONG);
                errorSavingChanges.show();
            }
        });
    }

    /**
     * Makes a popup when trying to leave the activity, which asks for whether
     * or not the user wants to save the data changes
     */
    void promptForSaveSettings() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Savesettings");
        builder.setMessage("Save changes?");
        builder.setPositiveButton("YeS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                save();
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

    /**
     * Sets up the display of data for the textfields and imageview
     */
    void initializeDisplayOfData() {
        dateText.setText(mCert.getmExpirationDate());
        mTextCertName.setText(mCert.getmName());
        //checks whether the certificate has an image or not, before trying to download the image
        if (mCert.getmPhoto() != null) {
            new DownloadImageTask((ImageView) mImageView).execute(mCert.getmPhoto());
        }

    }

    /**
     * When using the datepicker view, upon choosing a date, this method is revoked
     * Then creates a string with the specified year, month and dayofmonth, which is attached to dateText
     * @param view
     * @param year
     * @param month
     * @param dayOfMonth
     */
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String expirationDate = DateFormat.getDateInstance(DateFormat.SHORT, Locale.UK).format(c.getTime());
        dateText.setText(expirationDate);
    }

    /**
     * Whenever an activity finishes this method is called, then it uses requestcode to determine what view
     * and if resultcode is RESULT_OK, then data can be used to retrieve information
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri uri = null;
        // if the image comes from the camera directly
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            uri = Uri.fromFile(new File(mPhotoHelper.getmCurrentPhotoPath()));
        } else if (requestCode == REQUEST_IMAGE_UPLOAD && resultCode == RESULT_OK) {
            //takes Image from storage/SD
            uri = data.getData();
        }
        // Checks uri and sets the bitmap for displaying the new image
        if (uri != null) {
            mCurrentImageUri = uri;
            try {
                mBitmap = mPhotoHelper.getBitmap(uri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            mImageView.setImageBitmap(mBitmap);
        }
    }

    /**
     * Overrides the method for the back button, to prompt the user a change to save changes
     */
    @Override
    public void onBackPressed() {
        promptForSaveSettings();
    }

    /**
     * Deletes the currently selected certificate: mCert if applicable
     * once completed finishes this activity
     */
    private void deleteCertificate() {
        mDb.collection("certificates").document(mCert.getmUId()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                finish();
            }
        });
    }
}
