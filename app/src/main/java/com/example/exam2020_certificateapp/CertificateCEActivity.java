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
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CertificateCEActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    PhotoHelper mPhotoHelper;
    PhotoHolder mPhotoHolder;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_IMAGE_UPLOAD = 2;
    ImageView mImageView;
    private FirebaseFirestore mDb;
    TextView dateText;
    TextView mTextCertName;
    Certificate mCert;
    Bitmap mBitmap;
    Uri mCurrentImageUri;
    private FirebaseAuth mAuth;
    FirebaseStorage storage;
    StorageReference storageReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_certificate_c_e);
        mPhotoHolder = PhotoHolder.getInstance();
        mImageView = findViewById(R.id.cceImageView);
        mDb = FirebaseFirestore.getInstance();
        dateText = (TextView) findViewById(R.id.cceTWDate);
        mCert = (Certificate) getIntent().getSerializableExtra("cert");
        mTextCertName = findViewById(R.id.cceETCertName);
        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

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
        final String path = "images/" + mAuth.getCurrentUser().getUid() + "/certificates/";
        final Certificate certificate;
        if (mCert != null) {
            Log.d("FASTER", "In not null");
            certificate = mCert;
        } else {
            Log.d("FASTER", "In else");
            certificate = new Certificate();
            certificate.setmUId(UUID.randomUUID().toString());
            certificate.setmUserUid(mAuth.getCurrentUser().getUid());
        }
        Log.d("FASTER", certificate.getmUId());
        certificate.setmExpirationDate(dateText.getText().toString());
        certificate.setmExpirationDate(dateText.getText().toString());
        certificate.setmName(mTextCertName.getText().toString());
        if (mCurrentImageUri != null) {
            mPhotoHelper.uploadImageToFirebase(mCurrentImageUri, path + certificate.getmUId(), new UploadCallBack() {
                @Override
                public void onCallback(boolean state) {
                    if (state == true) {
                        storageReference.child("images/" + certificate.getmUserUid() + "/certificates/" + certificate.getmUId()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                certificate.setmPhoto(uri.toString());
                                mPhotoHolder.putExtra("bitmap"+certificate.getmUId(), mBitmap);
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

    void saveInFirebase(final Certificate certificate) {
        mDb.collection("certificates").document(certificate.getmUId()).set(certificate).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mDb.document("certificates/" + certificate.getmUserUid()).set(certificate).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        mPhotoHolder.putExtra(certificate.getmUId(), certificate);
                        Log.d("FASTER", certificate.getmName());
                        finish();
                        Toast succesSaving = Toast.makeText(CertificateCEActivity.this, "Succesfully Saved Changes", Toast.LENGTH_LONG);
                        succesSaving.show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast errorSavingChanges = Toast.makeText(CertificateCEActivity.this, "Error Saving Changes", Toast.LENGTH_LONG);
                errorSavingChanges.show();
            }
        });
    }

    void oldsave() {
        //
        //mPhotoHelper.uploadImageToFirebase(mCurrentImageUri, UUID.randomUUID());
        //
        final String path = "images/" + mAuth.getCurrentUser().getUid() + "/certificates/";
        final Certificate certificate;
        if(mCert != null) {
            certificate = mCert;

            mCert.setmUserUid(mAuth.getCurrentUser().getUid());
            Log.d("XYZ", "User ID ==" + mCert.getmUserUid());
            mPhotoHelper.uploadImageToFirebase(mCurrentImageUri, path + mCert.getmUId(), new UploadCallBack() {
                @Override
                public void onCallback(boolean state) {
                    if (state == true)
                    {
                        storageReference.child("images/" + mCert.getmUserUid() + "/certificates/" + mCert.getmUId()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                mCert.setmPhoto(uri.toString());
                                mDb.document("certificates/" + mCert.getmUId()).set(certificate).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        mPhotoHolder.putExtra(mCert.getmUId(), mCert);
                                        Log.d("XYZ", "nu sker der noget hmmm" + mAuth.getUid() + "==" + mCert.getmUId());
                                        Toast succesSaving = Toast.makeText(CertificateCEActivity.this, "Succesfully Saved Changes", Toast.LENGTH_LONG);
                                        succesSaving.show();
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
                        });
                    }
                }
            });


        } else {
            if(mBitmap!=null){
                certificate = new Certificate( dateText.getText().toString(), mTextCertName.getText().toString());
                // certificate.setmBitmap(mBitmap);
                certificate.setmUId(UUID.randomUUID().toString());
                // mDb.collection("users").document(mAuth.getUid()).update("mCertificateList", certificate);

                certificate.setmUserUid(mAuth.getCurrentUser().getUid());
                Log.d("XYZ", "User ID ==" + mAuth.getCurrentUser().getUid());
                mPhotoHelper.uploadImageToFirebase(mCurrentImageUri, path + certificate.getmUId(), new UploadCallBack() {
                    @Override
                    public void onCallback(boolean state) {
                        if (state == true)
                        {
                            storageReference.child("images/" + certificate.getmUserUid() + "/certificates/" + certificate.getmUId()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    certificate.setmPhoto(uri.toString());
                                    mDb.collection("certificates").document(certificate.getmUId()).set(certificate).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // String path = "images/" + mAuth.getCurrentUser().getUid() + "/certificates/" + UUID.randomUUID();
                                            Log.d("XYZ", "nu sker der noget" + mAuth.getUid() + "==" + certificate.getmUId());
                                            mDb.document("users/" + mAuth.getUid()).update("mCertificateList", FieldValue.arrayUnion(certificate.getmUId())).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    mPhotoHolder.putExtra(certificate.getmUId(), certificate);
                                                    mPhotoHolder.putExtra("bitmap"+certificate.getmUId(), mBitmap);
                                                    finish();
                                                    Toast succesSaving = Toast.makeText(CertificateCEActivity.this, "Succesfully Saved Changes", Toast.LENGTH_LONG);
                                                    succesSaving.show();
                                                }
                                            });
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast errorSavingChanges = Toast.makeText(CertificateCEActivity.this, "Error Saving Changes", Toast.LENGTH_LONG);
                                            errorSavingChanges.show();
                                        }
                                    });
                                }
                            });
                        }
                    }
                });
            }
        }
    }

    void promptForSaveSettings() {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Savesettings");
            builder.setMessage("Save changes?");
            builder.setPositiveButton("YeS", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Log.e("XYZ", "Never finishing");
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

    void initializeDisplayOfData(){
        Calendar c = Calendar.getInstance();
        //use real data here
        c.set(Calendar.YEAR, 2021);
        c.set(Calendar.MONTH, 4);
        c.set(Calendar.DAY_OF_MONTH, 28);
        String expirationDate = DateFormat.getDateInstance().format(c.getTime());

        //ved ikke lige helt hvad der skal gøres med expdate
        dateText.setText(expirationDate);
        mTextCertName.setText(mCert.getmName());
        mImageView.setImageBitmap((Bitmap) mPhotoHolder.getExtra("bitmap"+mCert.getmUId()));
        //new DownloadImageTask((ImageView) mImageView).execute(mCert.getmPhoto());
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String expirationDate = DateFormat.getDateInstance().format(c.getTime());


        dateText.setText(expirationDate);
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
            mCurrentImageUri = uri;
            mBitmap = mPhotoHelper.getBitmap(uri);
            mImageView.setImageBitmap(mBitmap);

        }
    }
    @Override
    public void onBackPressed() {
        promptForSaveSettings();
    }
}
