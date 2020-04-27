package com.example.exam2020_certificateapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.exam2020_certificateapp.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class RegisterActivity extends AppCompatActivity {

    public static final String TAG = "CertificateReg";
    private FirebaseAuth mAuth;
    private FirebaseFirestore mDb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        mDb = FirebaseFirestore.getInstance();

        Button signUp = findViewById(R.id.registerBtn);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText registerTextName = findViewById(R.id.registerEditName);
                EditText registerTextUserName = findViewById(R.id.registerEditUsername);
                EditText registerTextEmail = findViewById(R.id.registerEditEmail);
                EditText registerTextPassword = findViewById(R.id.registerEditPassword);
                EditText registerConfirmTextPassword = findViewById(R.id.registerEditConfirmPassword);
                String confirmPassword = registerConfirmTextPassword.getText().toString();
                String email = registerTextEmail.getText().toString();
                String password = registerTextPassword.getText().toString();
                String userName = registerTextUserName.getText().toString();
                String name = registerTextName.getText().toString();
                validatingUser(email, password, name, userName, confirmPassword);
            }
        });
    }

    private void createAccount(final String email, String password, final String name, final String userName) {
        Log.d(TAG, "createAccount:" + email);


        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            User user = new User(name,email,firebaseUser.getUid(), userName);
                            Log.d(TAG, firebaseUser.getUid());
                            // firebaseUser.sendEmailVerification();
                            createUserInDb(user);
                            redirectToLogin();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void createUserInDb(User user) {
        mDb.document("users/" + user.getmUId()).set(user);
    }

    private boolean validateUser(String email, String name, String userName) {
        if (email == null || email.isEmpty()) {
            // throw new error or exeption or make a message
            Log.d(TAG, "must be a valid email");
            return false;
        }
        if (name == null || name.isEmpty()) {
            // throw new error or exeption or make a message
            Log.d(TAG, "name can not be null or empty");
            return false;
        }
        if (userName == null || userName.isEmpty()) {
            // throw new error or exeption or make a message
            Log.d(TAG, "user name can not be null or empty");
            return false;
        }
        return true;
    }

    private void validatingUser(final String email, final String password, final String name, final String userName, final String confirmPassword) {
        if (!validateUser(email, name, userName)) {
            return;
        }
        if (!passwordCheck(password, confirmPassword)) {
            Log.d(TAG, "passwords does not match each other");
            return;
        }
        final List<String> allUserNames = new ArrayList<String>();
        mDb.collection("users").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                boolean exists = false;
                for(QueryDocumentSnapshot docSnap : queryDocumentSnapshots) {
                    User tempUser = docSnap.toObject(User.class);
                    String tempUserName = tempUser.getmUserName();
                    allUserNames.add(tempUserName);
                }
                for (String un: allUserNames) {
                    Log.d(TAG, "username exist " + un);
                    if (userName.equals(un)) {
                        Log.d(TAG, "found username match");
                        Toast.makeText(RegisterActivity.this, "Username is already in use",
                                Toast.LENGTH_SHORT).show();
                        EditText registerTextUserName = findViewById(R.id.registerEditUsername);
                        registerTextUserName.setTextColor(Color.RED);
                        exists = true;
                    }
                }
                if(!exists) {
                    createAccount(email, password, name, userName);
                }
            }
        });

    }

    private boolean passwordCheck(String password, String confirmPassword) {
        if (!password.equals(confirmPassword)) {
            Log.d(TAG, "the 2 passwords are not hte same");
            return false;
        }
        if (password.length()<6) {
            Log.d(TAG, "password too short");
            return false;
        }
        // regex of what a password needs to contain if we want extra security

        return true;
    }

    private void redirectToLogin() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
