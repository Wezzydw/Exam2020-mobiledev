package com.example.exam2020_certificateapp;

import androidx.annotation.Nullable;
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

import com.example.exam2020_certificateapp.helpers.PhotoHolder;
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
    private ImageView profilePic;

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
        user = (User) getIntent().getSerializableExtra("user");
        setUser();
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

    }

    private void setUser() {

        if (user.getmImage() != null && !user.getmImage().isEmpty()) {
//            byte[] byteArray = user.getmImage();
//            Bitmap bm = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
//            profilePic.setImageBitmap(bm);
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
                    Intent intent = new Intent(view.getContext(), CertificateCEActivity.class);
                    Certificate cert = certificates.get(position);
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

    private void redirectToSettings() {
        Log.d("XYZ", "redirected??");
        Intent intent = new Intent(this, UserSettingsActivity.class); //settings activity
        intent.putExtra("user",user);
        startActivityForResult(intent, 10);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.d("XYZ", "onResult resultCode = " + resultCode);
        if (requestCode==10) {
            if (resultCode == RESULT_OK) {
                User updatedUser = (User) data.getExtras().getSerializable("updatedUser");
                user = updatedUser;
                setUser();

                PhotoHolder photoHolder = PhotoHolder.getInstance();
                byte[] byteArray = (byte[])photoHolder.getExtra("profilePic");
                if (byteArray != null) {
                    Bitmap bm = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                    profilePic.setImageBitmap(bm);
                }
            }
        }

        if (requestCode==20) {
            Log.d("XYZ", "detail view");
        }
        if (requestCode==30) {
            Log.d("XYZ", "new certificate view");
        }
    }
}
