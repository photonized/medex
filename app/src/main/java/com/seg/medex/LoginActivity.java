package com.seg.medex;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;


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

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

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

        this.preferences = getSharedPreferences("ID", 0);
        this.editor = preferences.edit();

    }

    public void onLogInClick(View view){
        final String usernameText = username.getText().toString();
        final String passwordText = password.getText().toString();

        if(Utility.validEmail(usernameText)){
            //checks database to see if email is there
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
                                    db.collection("users").whereEqualTo("username", usernameText)
                                            .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                @Override
                                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                    SharedPreferences.Editor editor = preferences.edit();
                                                    editor.clear();
                                                    editor.putBoolean("created_profile", (boolean)queryDocumentSnapshots.getDocuments().get(0).get("created_profile"));
                                                    editor.putString("email", (String)queryDocumentSnapshots.getDocuments().get(0).get("email"));
                                                    editor.putString("first_name", (String)queryDocumentSnapshots.getDocuments().get(0).get("first_name"));
                                                    editor.putString("last_name", (String)queryDocumentSnapshots.getDocuments().get(0).get("last_name"));
                                                    editor.putString("username", (String)queryDocumentSnapshots.getDocuments().get(0).get("username"));
                                                    editor.putString("password", (String)queryDocumentSnapshots.getDocuments().get(0).get("password"));
                                                    editor.putInt("account_type", ((Long)queryDocumentSnapshots.getDocuments().get(0).get("account_type")).intValue());
                                                    editor.apply();
                                                    if(!preferences.getBoolean("created_profile", false)) {
                                                        profilePageConnection();
                                                        successfulLogin();
                                                    } else {
                                                        landingPageConnection();
                                                        successfulLogin();
                                                    }
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d("Update Preferences: ", "unexpected error.");
                                        }
                                    });
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
                    //sorry Wassim i know this sucks
                    if (TextUtils.isEmpty(usernameText) || TextUtils.isEmpty(passwordText)) {
                        emptyInputs();
                    } else {
                        if (task.isSuccessful()) {
                            QuerySnapshot query = task.getResult();
                            if (!(query.isEmpty())) {
                                //validates username and input password hash with the database's password hash
                                if (Crypto.verifyHash(passwordText, (String) query.getDocuments().get(0).get("password"))) {
                                    db.collection("users").whereEqualTo("username", usernameText)
                                            .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                @Override
                                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                    SharedPreferences.Editor editor = preferences.edit();
                                                    editor.clear();
                                                    editor.putBoolean("created_profile", (boolean)queryDocumentSnapshots.getDocuments().get(0).get("created_profile"));
                                                    editor.putString("email", (String)queryDocumentSnapshots.getDocuments().get(0).get("email"));
                                                    editor.putString("first_name", (String)queryDocumentSnapshots.getDocuments().get(0).get("first_name"));
                                                    editor.putString("last_name", (String)queryDocumentSnapshots.getDocuments().get(0).get("last_name"));
                                                    editor.putString("username", (String)queryDocumentSnapshots.getDocuments().get(0).get("username"));
                                                    editor.putString("password", (String)queryDocumentSnapshots.getDocuments().get(0).get("password"));
                                                    editor.putInt("account_type", ((Long)queryDocumentSnapshots.getDocuments().get(0).get("account_type")).intValue());
                                                    editor.apply();
                                                    if(!preferences.getBoolean("created_profile", false)) {
                                                        profilePageConnection();
                                                        successfulLogin();
                                                    } else {
                                                        landingPageConnection();
                                                        successfulLogin();
                                                    }
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d("Update Preferences: ", "unexpected error.");
                                        }
                                    });
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
        SharedPreferences preferences = getSharedPreferences("ID", 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("logged_in", true);
        editor.apply();

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

    private void landingPageConnection() {
        startActivity(new Intent(this, LandingActivity.class));
        finish();
    }

    private void profilePageConnection() {
        startActivity(new Intent(this, LandingActivity.class));
        finish();
    }

}
