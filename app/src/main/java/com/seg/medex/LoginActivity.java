package com.seg.medex;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;


import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.util.Log;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;
import android.text.TextUtils;

import com.google.firebase.firestore.DocumentSnapshot;

import com.google.firebase.firestore.FirebaseFirestore;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Query;

import java.util.List;


/**
 * The activity for the Log in page.
 */

public class LoginActivity extends AppCompatActivity {

    /**
     * Username/Email text field.
     */
    private EditText username;

    /**
     * Password text field.
     */
    private EditText password;


    /**
     * The Firebase Firestore database object.
     */
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        //initializing the variables we are using
        this.db = FirebaseFirestore.getInstance();

        this.username = findViewById(R.id.username);
        this.username.setText("");

        this.password = findViewById(R.id.password);
        this.password.setText("");

    }

    public void onLogInClick(View view){
        final String usernameText = username.getText().toString();
        final String passwordText = password.getText().toString();

        if(Utility.validEmail(usernameText)){
            //checks database to see if user is there
            db.collection("users").whereEqualTo("email", usernameText.toLowerCase())
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {

                    if (TextUtils.isEmpty(usernameText) || TextUtils.isEmpty(passwordText)) {
                        emptyInputs();
                    } else {
                        if (task.isSuccessful()) {
                            QuerySnapshot query = task.getResult();
                            if (!(query.isEmpty())) {
                                //validates username and input password hash with the database's password hash
                                if (Crypto.verifyHash(passwordText, (String) query.getDocuments().get(0).get("password"))) {
                                    successfulLogin();
                                    Log.d("LOGIN", "Document exists");
                                } else {
                                    notSuccessfulLogin();
                                }
                            } else {
                                //email doesn't exist in query
                                Log.d("LOGIN", "No such document");
                                noSuchEmail();
                            }
                        }
                        else {
                            Log.d("LOGIN", "Auth failed.", task.getException());
                        }
                    }
                }
            });
        }else{
            //checks database to see if user is there
            db.collection("users").whereEqualTo("username", usernameText.toLowerCase())
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {

                    if (TextUtils.isEmpty(usernameText) || TextUtils.isEmpty(passwordText)) {
                        emptyInputs();
                    } else {
                        if (task.isSuccessful()) {
                            QuerySnapshot query = task.getResult();


                            if (!(query.isEmpty())) {
                                //validates username and input password hash with the database's password hash
                                if (Crypto.verifyHash(passwordText, (String) query.getDocuments().get(0).get("password"))) {
                                    successfulLogin();
                                    landingPageConnection();
                                    Log.d("LOGIN", "Document exists");
                                } else {
                                    notSuccessfulLogin();
                                }
                            } else {
                                //username doesnt exist in query
                                Log.d("LOGIN", "No such document");
                                noSuchUser();
                            }
                        }
                        else {
                            Log.d("LOGIN", "Auth failed.", task.getException());
                        }
                    }
                }
            });
        }
    }

    //Used for validation of inputs
    private void emptyInputs(){
        Toast.makeText(this, "Inputs are empty!", Toast.LENGTH_SHORT).show();
    }

    //Login is fully authenticated
    private void successfulLogin() {
        Toast.makeText(this, "Success! Logged in.", Toast.LENGTH_SHORT).show();
    }

    //User is not defined in query
    private void noSuchUser(){
        Toast.makeText(this, "No such username is registered. Please register.", Toast.LENGTH_SHORT).show();
    }

    private void noSuchEmail(){
        Toast.makeText(this, "No such email is registered. Please register.", Toast.LENGTH_SHORT).show();
    }

    //Incorrect fields for either user or password
    private void notSuccessfulLogin() {
        Toast.makeText(this, "Failed. Check username and password.", Toast.LENGTH_SHORT).show();
    }

    public void signUpInstead(View view) {
        startActivity(new Intent(this, SignupActivity.class));
        finish();
    }

    public void landingPageConnection() {
        startActivity(new Intent(this, LandingActivity.class));
        finish();
    }

}
