package com.example.exam2020_certificateapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.example.exam2020_certificateapp.model.User;

public class CertificateListActivity extends AppCompatActivity {
    User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_certificate_list);

        setUser();

        setupListView();


    }

    private void setUser() {
        user = (User) getIntent().getSerializableExtra("user");
        if (user.getmImage() != null) {
            //set image
        }
        if (user.getmCertificateList() != null) {
            // get certificates
        }
        TextView textUserName = findViewById(R.id.textUserName);
        textUserName.setText(user.getmUserName());
    }

    private void setupListView() {
        ListView lv = findViewById(R.id.listCertificates);
        if (user.getmCertificateList() != null) {
            CustomAdapter customAdapter = new CustomAdapter(getApplicationContext(), user.getmCertificateList());
            lv.setAdapter(customAdapter);
        }

    }
}
