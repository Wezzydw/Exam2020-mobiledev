package com.example.exam2020_certificateapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.exam2020_certificateapp.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth mAuth; // Connection to FirebaseAuthentication.
    private FirebaseFirestore mDb; // Connection to FirebaseFirestore database.
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
                validatingUserInFirebase(email, password, name, userName, confirmPassword);
            }
        });
    }

    /**
     * Takes the users input values for email, password, name and username to create a user account,
     * using FirebaseAuth.
     * If the email is already taken by another user, the user is prompted with an error message.
     * @param email represents string input value.
     * @param password represents string input value.
     * @param name represents string input value.
     * @param userName represents string input value.
     */
    private void createUserAccount(final String email, String password, final String name, final String userName) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            User user = new User(name,email,firebaseUser.getUid(), userName);
                            // firebaseUser.sendEmailVerification();
                            createUserInDb(user);
                            startLoginActivity();
                        } else {
                            // If sign in fails, display a message to the user.
                            Snackbar emailFail = Snackbar.make(findViewById(R.id.RegisterActivity), "The email is already in use", Snackbar.LENGTH_LONG);
                            emailFail.show();
                        }
                    }
                });
    }

    /**
     * Puts the user into the database.
     * @param user Data to put into the database.
     */
    private void createUserInDb(User user) {
        mDb.document("users/" + user.getmUId()).set(user);
    }

    /**
     * Returns false if input values email, name or username are either null or empty. If they fail the
     * checks, the user is prompted.
     * Otherwise returns true.
     * @param email represents string input value.
     * @param name represents string input value.
     * @param userName represents string input value.
     * @return Boolean value.
     */
    private boolean validateUserInput(String email, String name, String userName) {
        if (email == null || email.isEmpty()) {
            Snackbar emailFail = Snackbar.make(findViewById(R.id.RegisterActivity), "Email can not be empty", Snackbar.LENGTH_LONG);
            emailFail.show();
            return false;
        }
        if (name == null || name.isEmpty()) {
            Snackbar nameFail = Snackbar.make(findViewById(R.id.RegisterActivity), "Name can not be empty", Snackbar.LENGTH_LONG);
            nameFail.show();
            return false;
        }
        if (userName == null || userName.isEmpty()) {
            Snackbar userNameFail = Snackbar.make(findViewById(R.id.RegisterActivity), "UserName can not be empty", Snackbar.LENGTH_LONG);
            userNameFail.show();
            return false;
        }
        return true;
    }

    /**
     * Checks the database to see if the username is available.
     * This method calls validateUserInput, using the email, name and username.
     * This method calls passwordCheck using the password and confirm password.
     * this method calls createUserAccount using email, password, name, userName.
     * @param email represents string input value.
     * @param password represents string input value.
     * @param name represents string input value.
     * @param userName represents string input value.
     * @param confirmPassword represents string input value.
     */
    private void validatingUserInFirebase(final String email, final String password, final String name, final String userName, final String confirmPassword) {
        if (!validateUserInput(email, name, userName)) {
            return;
        }
        if (!passwordCheck(password, confirmPassword)) {
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
                    if (userName.equals(un)) {
                        Snackbar userNameFail = Snackbar.make(findViewById(R.id.RegisterActivity), "The username is already in use", Snackbar.LENGTH_LONG);
                        userNameFail.show();
                        EditText registerTextUserName = findViewById(R.id.registerEditUsername);
                        registerTextUserName.setTextColor(Color.RED);
                        exists = true;
                    }
                }
                if(!exists) {
                    createUserAccount(email, password, name, userName);
                }
            }
        });

    }

    /**
     * Returns true if the password meets the requirements and returns false if password doesn't
     * match confirmedPassword and if the password is less that 6 characters.
     * @param password represents string input value.
     * @param confirmPassword represents string input value.
     * @return Boolean value.
     */
    private boolean passwordCheck(String password, String confirmPassword) {
        if (!password.equals(confirmPassword)) {
            Snackbar passwordFail = Snackbar.make(findViewById(R.id.RegisterActivity), "The 2 passwords are not identical", Snackbar.LENGTH_LONG);
            passwordFail.show();
            return false;
        }
        if (password.length()<6) {
            Snackbar passwordFail = Snackbar.make(findViewById(R.id.RegisterActivity), "Password have to be at least 6 characters long", Snackbar.LENGTH_LONG);
            passwordFail.show();
            return false;
        }
        // regex of what a password needs to contain if we want extra security
        return true;
    }

    /**
     * Starts the Login Activity.
     */
    private void startLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}
