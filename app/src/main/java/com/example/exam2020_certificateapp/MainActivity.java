package com.example.exam2020_certificateapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;


import com.example.exam2020_certificateapp.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.security.cert.Certificate;


public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore mDb;
    private String TAG = "XYZ";
    private ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Intent intent = new Intent(this, UserSettingsActivity.class);
        //startActivity(intent);

        mDb = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();



    }


    public void btnRouteRegisterActivity(View view) {
        Log.d(TAG, "ny aktivitet");
        startActivity(new Intent(this, RegisterActivity.class));
    }

    public void login(View view) {

        final EditText getEmail = findViewById(R.id.loginEtUsername);
        EditText getPassword = findViewById(R.id.loginEtPassword);

        //AUTO LOGIN//


        final String email = getEmail.getText().toString();
        final String password = getPassword.getText().toString();
        progressBar();

        if((email != null && !email.isEmpty()) && password != null && !password.isEmpty()){

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");

                            FirebaseUser user = mAuth.getCurrentUser();
                            getUser(user.getUid());
                        } else {
                            // If sign in fails, display a message to the user.
                            getEmail.setTextColor(Color.RED);
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Snackbar authError = Snackbar.make(findViewById(R.id.MainActivity),
                                    "Wrong email/username or password.",
                                    Snackbar.LENGTH_SHORT);
                            dialog.dismiss();
                            authError.show();
                        }

                    }
                    // ...
                });}
    }

    private void getUser(String uid) {
        DocumentReference docRef = mDb.collection("users").document(uid);
        docRef.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                User tempUser = document.toObject(User.class);
                                redirectUser(tempUser);

                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
        dialog.dismiss();
    }

    private void redirectUser(User user){
        Log.d(TAG, "redirected??");
        Intent intent = new Intent(this, CertificateListActivity.class);
        intent.putExtra("user",user);
        startActivity(intent);
    }

    private void progressBar(){
        Log.d(TAG,"ProgressBar?");
        dialog = new ProgressDialog(MainActivity.this);
        dialog.show();
        dialog.setContentView(R.layout.progress_dialog);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }


}