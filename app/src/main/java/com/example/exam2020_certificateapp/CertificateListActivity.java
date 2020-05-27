package com.example.exam2020_certificateapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.exam2020_certificateapp.helpers.DownloadImageTask;
import com.example.exam2020_certificateapp.helpers.PhotoHolder;
import com.example.exam2020_certificateapp.model.Certificate;
import com.example.exam2020_certificateapp.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StreamDownloadTask;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class CertificateListActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    User user; //Logged in user
    ArrayList<Certificate> certificates = new ArrayList<>(); //Global list of certificates
    private FirebaseFirestore mDb; //Firebase connection
    private StorageReference mStorageRef; //Firebase storage reference
    private FirebaseAuth mAuth; // Firebase Authtentication connection
    private ImageView profilePic; //Imageview for users profile picture
    ListView lv; //Listview
    private Spinner spinner; //Spinner to select which sorting method is being used
    private EditText mTxtSearch; //EditText to type in search strings
    private String mCurrentSearchString = ""; //String to hold what is currently being searched for
    private int index = -1; //Index for how many certificates there is coming from database
    private ProgressDialog dialog; //Loading icon
    private PhotoHolder mPhotoHolder; //Static class to hold and share large data between activities
    private final static int REQUEST_CODE_USER_SETTINGS = 10;
    private final static int REQUEST_CODE_CERTIFICATE_CE = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_certificate_list);
        mDb = FirebaseFirestore.getInstance();
        mPhotoHolder = PhotoHolder.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mAuth.getCurrentUser();
        user = (User) getIntent().getSerializableExtra("user");
        spinner = findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);
        lv = findViewById(R.id.listCertificates);
        profilePic = findViewById(R.id.imageUser);
        if (user.getmImageUrl() != null) {
            getImageForUser();
        }
        initializeComponents();
        mTxtSearch = findViewById(R.id.certListTXTSearch);
        mTxtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCurrentSearchString = s.toString();
                setupListView();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        TextView textUserName = findViewById(R.id.textUserName);
        textUserName.setText(user.getmUserName());
        getAllCertificatesFromUser();

    }

    /**
     * Initializes all core-components of this activity
     */
    private void initializeComponents() {
        Button buttonNews = findViewById(R.id.certlistBtnNews);
        buttonNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), NewsActivity.class);
                startActivity(intent);
            }
        });

        Button buttonAdd = findViewById(R.id.buttonAdd);
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), CertificateCEActivity.class);
                startActivityForResult(intent, REQUEST_CODE_CERTIFICATE_CE);
            }
        });
        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectToSettings();
            }
        });
    }

    /**
     * Call this method to download all certificates from logged in user
     */
    private void getAllCertificatesFromUser() {
        progressBar();
        mDb.collection("certificates").whereEqualTo("mUserUid", user.getmUId()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    index = 0;
                    final int size = task.getResult().size();
                    //dismisses infinite loop in case of no certificates
                    if (size == 0) {
                        dialog.dismiss();
                    }
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        final Certificate tempCert = documentSnapshot.toObject(Certificate.class);
                        StorageReference riversRef = mStorageRef.child("images/" + user.getmUId() + "/certificates/" + tempCert.getmUId());
                        //Downloads the image from the certificate
                        riversRef.getStream(new StreamDownloadTask.StreamProcessor() {
                            @Override
                            public void doInBackground(@NonNull StreamDownloadTask.TaskSnapshot taskSnapshot, @NonNull InputStream inputStream) {
                                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                                //Puts the downloaded image into photoholder for storage
                                mPhotoHolder.putExtra("bitmap" +tempCert.getmUId(), bitmap);
                                certificates.add(tempCert);
                            }
                        }).addOnCompleteListener(new OnCompleteListener<StreamDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<StreamDownloadTask.TaskSnapshot> task) {
                                index++;
                                //Checks for the last certificate before dismissing dialog and shows certificates
                                if (size == index) {
                                    dialog.dismiss();
                                    setupListView();
                                }
                            }
                        });
                    }

                }
            }
        });

    }

    /**
     * Is responsible for showing the data, depending on the sort and search parameters
     */
    private void setupListView() {
        if (certificates != null || certificates.isEmpty()) {
            final ArrayList<Certificate> sortCertificates = new ArrayList<>();
            //Sort list
            for (Certificate cert : certificates) {
                if (cert.getmName().toLowerCase().contains(mCurrentSearchString.toLowerCase())) {
                    sortCertificates.add(cert);
                    continue;
                }
                if (cert.getmExpirationDate().toLowerCase().contains(mCurrentSearchString.toLowerCase())) {
                    sortCertificates.add(cert);
                    continue;
                }

            }
            // creates the adapter with the sorted list of certificates
            CustomAdapter customAdapter = new CustomAdapter(getApplicationContext(), sortCertificates);
            lv.setAdapter(customAdapter);
            //Sets a OnItemClickListener to select which certificate is pressed
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                //Starts certificateCEActivity with the selected Certificate
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(view.getContext(), CertificateCEActivity.class);
                    Certificate cert = sortCertificates.get(position);
                    intent.putExtra("cert", cert);
                    intent.putExtra("position", position);
                    startActivityForResult(intent, REQUEST_CODE_CERTIFICATE_CE);
                }
            });
        }
    }

    /**
     * Starts the usersettingsactivity
     */
    private void redirectToSettings() {
        Intent intent = new Intent(this, UserSettingsActivity.class); //settings activity
        intent.putExtra("user", user);
        startActivityForResult(intent, REQUEST_CODE_USER_SETTINGS);
    }
    /**
     * Whenever an activity finishes this method is called, then it uses requestcode to determine what view
     * and if resultcode is RESULT_OK, then data can be used to retrieve information
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_USER_SETTINGS) {
            if (resultCode == RESULT_OK) {
                //Checks if the intent returns information to delete user
                if (data.getExtras().get("delete") != null && data.getExtras().get("delete").equals(true)) {
                    finish();
                } else {
                    User updatedUser = (User) data.getExtras().getSerializable("updatedUser");
                    user = updatedUser;
                    getImageForUser();
                }
            }
        }
        if (requestCode == REQUEST_CODE_CERTIFICATE_CE) {
            certificates.clear();
            getAllCertificatesFromUser();
        }
    }

    /**
     * Whenever the spinner for sorting is used, this method is called, where position is used to
     * determine which sorting method is used
     * Parent, view and id is currently not used
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
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
                //Sort by date
                Collections.sort(certificates, new Comparator<Certificate>() {
                    @Override
                    public int compare(Certificate o1, Certificate o2) {
                        SimpleDateFormat formatter1 = new SimpleDateFormat("dd/mm/yyyy");
                        SimpleDateFormat formatter2 = new SimpleDateFormat("dd/mm/yyyy");
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

    /**
     * If nothing is selected in spinner, this method is called,
     * Parent is not used since we currently are not using this method, but is here
     * because of us implementing the parent
     * @param parent
     */
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    /**
     * Starts a dialog with a loading icon spinning until dialog.dismiss() is called
     */
    private void progressBar() {
        dialog = new ProgressDialog(CertificateListActivity.this);
        dialog.show();
        dialog.setContentView(R.layout.progress_loading_certificates);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    /**
     * Starts a new downloadtask that downloads an image to profilepic from users image url
     */
    private void getImageForUser() {
        new DownloadImageTask((ImageView) profilePic).execute(user.getmImageUrl());
    }
}
