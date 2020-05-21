package com.example.exam2020_certificateapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.exam2020_certificateapp.helpers.PhotoHolder;
import com.example.exam2020_certificateapp.model.Certificate;
import com.example.exam2020_certificateapp.model.User;
import com.example.exam2020_certificateapp.swipe.CertificateAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StreamDownloadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Observable;

public class CertificateListActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    User user;
    ArrayList<Certificate> certificates = new ArrayList<Certificate>();
    private FirebaseFirestore mDb;
    private StorageReference mStorageRef;
    private FirebaseAuth mAuth;
    private ImageView profilePic;
    private PhotoHolder mPhotoHolder;
    ListView lv;
    private ViewPager viewPager;
    private CertificateAdapter certificateAdapter;
    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_certificate_list);
        viewPager = findViewById(R.id.viewpager);
        certificateAdapter = new CertificateAdapter(getSupportFragmentManager());
        viewPager.setAdapter(certificateAdapter);
        mPhotoHolder = PhotoHolder.getInstance();
        mDb = FirebaseFirestore.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mAuth.getCurrentUser();
        Log.d("XYZ", mAuth.getCurrentUser().getUid());
        user = (User) getIntent().getSerializableExtra("user");
        spinner = findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);
        lv = findViewById(R.id.listCertificates);
        profilePic = findViewById(R.id.imageUser);
        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectToSettings();
            }
        });
        Button buttonAdd = findViewById(R.id.buttonAdd);
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), CertificateCEActivity.class);

                startActivityForResult(intent, 30);
            }
        });
        setUser();
    }

    private void setUser() {

        if (user.getmImage() != null && !user.getmImage().isEmpty()) {
//            byte[] byteArray = user.getmImage();
//            Bitmap bm = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
//            profilePic.setImageBitmap(bm);
        }

        if (user.getmCertificateList() != null) {
            for (final String uId: user.getmCertificateList()) {
                if(mPhotoHolder.hasExtra(uId))
                {
                    certificates.add((Certificate) mPhotoHolder.getExtra(uId));
                    setupListView();
                    continue;
                }
                else {
                //mDb.batch().
                Log.d("XYZCert", uId);
                mDb.document("certificates/" + uId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        try {
                            final Certificate tempCert = documentSnapshot.toObject(Certificate.class);
                            File localFile = File.createTempFile("images","jpg");
                            StorageReference riversRef = mStorageRef.child("images/" + user.getmUId() + "/certificates/" + uId);
                            Log.d("XYZ",riversRef.getPath());
                            Log.d("XYZ",tempCert.getmName() + tempCert.getmUId());
                            riversRef.getStream(new StreamDownloadTask.StreamProcessor() {
                                @Override
                                public void doInBackground(StreamDownloadTask.TaskSnapshot taskSnapshot, InputStream inputStream) throws IOException {
                                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                    byte[] byteArray = stream.toByteArray();
                                    mPhotoHolder.putExtra(tempCert.getmUId(), tempCert);
                                    mPhotoHolder.putExtra("bitmap" +tempCert.getmUId(), bitmap);
                                    certificates.add(tempCert);
                                    Log.d("XYZ", tempCert.getmName() + tempCert.getmExpirationDate());
                                    inputStream.close();
                                }
                            }).addOnSuccessListener(new OnSuccessListener<StreamDownloadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(StreamDownloadTask.TaskSnapshot taskSnapshot) {
                                    Log.d("XYZ","download complete");
                                    setupListView();
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });}
            }
        }
        TextView textUserName = findViewById(R.id.textUserName);
        textUserName.setText(user.getmUserName());
    }

    private void setupListView() {

        if(certificates.size() == user.getmCertificateList().size()) {


        if (certificates != null|| certificates.isEmpty()) {
            //Sort list

            //
            CustomAdapter customAdapter = new CustomAdapter(getApplicationContext(), certificates);
            lv.setAdapter(customAdapter);
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(view.getContext(), CertificateCEActivity.class);
                    Certificate cert = certificates.get(position);
                    //cert.setCurrentBitmap(null);
                    Log.d("XYZ", cert.getmName() + cert.getmExpirationDate());
                    intent.putExtra("cert", cert);
                    intent.putExtra("position", position);

//                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                    cert.getmBitmap().compress(Bitmap.CompressFormat.PNG, 100, stream);
//                    byte[] byteArray = stream.toByteArray(); // we do this cause it might be able to handle larger files than 1mb
//                    intent.putExtra("image", byteArray);
//                    intent.putExtra("position", position);
//                    Bitmap image = BitmapFactory.decodeByteArray(byteArray, 0,
//                            byteArray.length);

                    startActivityForResult(intent, 20);
                }
            });
        }
        }

    }

    private void redirectToSettings() {
        Log.d("XYZ", "redirected??");
        Intent intent = new Intent(this, UserSettingsActivity.class); //settings activity
        intent.putExtra("user",user);
        startActivityForResult(intent, 10);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("XYZ", "onResult resultCode = " + resultCode);
        if (requestCode == 10) {
            if (resultCode == RESULT_OK) {
                User updatedUser = (User) data.getExtras().getSerializable("updatedUser");
                user = updatedUser;
                setUser();

                PhotoHolder photoHolder = PhotoHolder.getInstance();
                byte[] byteArray = (byte[]) photoHolder.getExtra("profilePic");
                if (byteArray != null) {
                    Bitmap bm = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                    profilePic.setImageBitmap(bm);
                }
            }
        }

        if (requestCode == 20) {
            Log.d("XYZ", "detail view");
            certificates.clear();
            setUser();
        }
        if (requestCode == 30) {
            Log.d("XYZ", "new certificate view");
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Log.d("SORT", "Above");
        switch (position) {
            case 0:
                Log.d("SORT", "Aplha");
                //sort alphabetical
                Collections.sort(certificates, new Comparator<Certificate>() {
                    @Override
                    public int compare(Certificate o1, Certificate o2) {
                        return o1.getmName().compareToIgnoreCase(o2.getmName());
                    }
                });
                setupListView();
                break;
            case 1:
                Log.d("SORT", "Exp");
                Collections.sort(certificates, new Comparator<Certificate>() {
                    @Override
                    public int compare(Certificate o1, Certificate o2) {
                        SimpleDateFormat formatter1 = new SimpleDateFormat("dd MMM yyyy");
                        SimpleDateFormat formatter2 = new SimpleDateFormat("dd MMM yyyy");
                        Date date1 = null;
                        Date date2 = null;
                        try {
                            date1 = formatter1.parse(o1.getmExpirationDate());
                            date2 = formatter2.parse(o2.getmExpirationDate());
                            Log.d("SORT", date1.toString());
                            Log.d("SORT", date2.toString());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        return date1.compareTo(date2);
                    }
                });
                setupListView();
                break;
            default:
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Log.d("SORT", "NOTHING");
    }
}
