package com.example.exam2020_certificateapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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
import android.widget.TextView;

import com.example.exam2020_certificateapp.model.Certificate;
import com.example.exam2020_certificateapp.model.User;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public class CertificateListActivity extends AppCompatActivity {
    User user;
    ArrayList<Certificate> certificates = new ArrayList<Certificate>();
    private FirebaseFirestore mDb;
    private StorageReference mStorageRef;
    private FirebaseAuth mAuth;


    ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_certificate_list);

        mDb = FirebaseFirestore.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mAuth.getCurrentUser();
        Log.d("XYZ", mAuth.getCurrentUser().getUid());
        setUser();
        lv = findViewById(R.id.listCertificates);
        ImageView profilePic = findViewById(R.id.imageUser);
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
                Intent intent = new Intent(v.getContext(), RegisterActivity.class); // detail view instead of register

                startActivity(intent);
            }
        });

    }

    private void setUser() {
        user = (User) getIntent().getSerializableExtra("user");
        if (user.getmImage() != null) {
            //set image
        }

        if (user.getmCertificateList() != null) {
            for (final String uId: user.getmCertificateList()) {
                //mDb.batch().
                mDb.document("certificates/" + uId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        try {
                            final Certificate tempCert = documentSnapshot.toObject(Certificate.class);
                            File localFile = File.createTempFile("images","jpg");
                            StorageReference riversRef = mStorageRef.child("certificates/" + user.getmUId() + "/" + "historyScreen.PNG");
                            Log.d("XYZ",riversRef.getPath());
                            riversRef.getStream(new StreamDownloadTask.StreamProcessor() {
                                @Override
                                public void doInBackground(StreamDownloadTask.TaskSnapshot taskSnapshot, InputStream inputStream) throws IOException {
                                    long totalBytes = taskSnapshot.getTotalByteCount();
                                    long bytesDownloaded = 0;
                                    byte[] buffer = new byte[1024];
                                    int size;


                                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                                    // make bytearray from the inpputstream

                                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                    byte[] byteArray = stream.toByteArray();

                                    tempCert.setmBitmap(byteArray);
                                    certificates.add(tempCert);
                                    Log.d("XYZ", tempCert.getmName() + tempCert.getmExpirationDate());
                                    inputStream.close();
                                }
                            }).addOnSuccessListener(new OnSuccessListener<StreamDownloadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(StreamDownloadTask.TaskSnapshot taskSnapshot) {
                                    Log.d("123","download complete");
                                    Log.d("XYZ","size: " + certificates.get(0).getmBitmap().length);
                                    setupListView();



                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
        TextView textUserName = findViewById(R.id.textUserName);
        textUserName.setText(user.getmUserName());
    }

    private void setupListView() {

        if (certificates != null|| certificates.isEmpty()) {
            CustomAdapter customAdapter = new CustomAdapter(getApplicationContext(), certificates);
            lv.setAdapter(customAdapter);
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(view.getContext(), RegisterActivity.class); // detail view instead of register
                    Certificate cert = certificates.get(position);
                    Log.d("XYZ", cert.getmName() + cert.getmExpirationDate());
                    intent.putExtra("cert", cert);

//                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                    cert.getmBitmap().compress(Bitmap.CompressFormat.PNG, 100, stream);
//                    byte[] byteArray = stream.toByteArray(); // we do this cause it might be able to handle larger files than 1mb
//                    intent.putExtra("image", byteArray);
//                    intent.putExtra("position", position);
//                    Bitmap image = BitmapFactory.decodeByteArray(byteArray, 0,
//                            byteArray.length);

                    startActivity(intent);
                }
            });
        }

    }

    private void redirectToSettings() {
        Log.d("XYZ", "redirected??");
        Intent intent = new Intent(this, RegisterActivity.class); //settings activity
        intent.putExtra("user",user);
        startActivity(intent);
    }


}
