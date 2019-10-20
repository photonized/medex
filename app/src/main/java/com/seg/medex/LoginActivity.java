package com.seg.medex;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_UP;


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

    private Button loginButton;

    private ProgressBar loginCircle;

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

        this.loginButton = findViewById(R.id.login_button);
        this.loginCircle = findViewById(R.id.login_circle);
        this.loginCircle.setVisibility(View.INVISIBLE);

        setConfirmPasswordEnterListener();
        setOnTouchListener();

    }

    public void onLogInClick(View view){
        loginButton.setEnabled(false);
        final String usernameText = username.getText().toString();
        final String passwordText = password.getText().toString();

        if(Utility.validEmail(usernameText)){
            this.loginCircle.setVisibility(View.VISIBLE);
            this.loginButton.setText("");
             //Checks database to see if email is there
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
                                    db.collection("users").whereEqualTo("email", usernameText)
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
                                                        loginCircle.setVisibility(View.INVISIBLE);
                                                        loginButton.setText(R.string.log_in);
                                                        loginButton.setEnabled(true);
                                                        profilePageConnection();
                                                        successfulLogin();
                                                    } else {
                                                        loginCircle.setVisibility(View.INVISIBLE);
                                                        loginButton.setText(R.string.log_in);
                                                        loginButton.setEnabled(true);
                                                        landingPageConnection();
                                                        successfulLogin();
                                                    }
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            loginCircle.setVisibility(View.INVISIBLE);
                                            loginButton.setText(R.string.log_in);
                                            loginButton.setEnabled(true);
                                            Log.d("Update Preferences: ", "unexpected error.");
                                        }
                                    });
                                    Log.d("LOGIN", "Document exists");
                                } else {
                                    loginCircle.setVisibility(View.INVISIBLE);
                                    loginButton.setText(R.string.log_in);
                                    loginButton.setEnabled(true);
                                    notSuccessfulLogin();
                                }
                            } else {
                                loginCircle.setVisibility(View.INVISIBLE);
                                loginButton.setText(R.string.log_in);
                                loginButton.setEnabled(true);
                                //email doesn't exist in query
                                Log.d("LOGIN", "No such document");
                                noSuchEmail();
                            }
                        }
                        else {
                            loginCircle.setVisibility(View.INVISIBLE);
                            loginButton.setText(R.string.log_in);
                            loginButton.setEnabled(true);
                            Log.d("LOGIN", "Auth failed.", task.getException());
                        }
                    }
                }
            });
        }else{
            /**
             * Checks database to see if user is there
             */
            this.loginCircle.setVisibility(View.VISIBLE);
            this.loginButton.setText("");
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
                                                        loginCircle.setVisibility(View.INVISIBLE);
                                                        loginButton.setText(R.string.log_in);
                                                        loginButton.setEnabled(true);
                                                        profilePageConnection();
                                                        successfulLogin();
                                                    } else {
                                                        loginCircle.setVisibility(View.INVISIBLE);
                                                        loginButton.setText(R.string.log_in);
                                                        loginButton.setEnabled(true);
                                                        landingPageConnection();
                                                        successfulLogin();
                                                    }
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            loginCircle.setVisibility(View.INVISIBLE);
                                            loginButton.setText(R.string.log_in);
                                            loginButton.setEnabled(true);
                                            Log.d("Update Preferences: ", "unexpected error.");
                                        }
                                    });
                                    Log.d("LOGIN", "Document exists");
                                } else {
                                    loginCircle.setVisibility(View.INVISIBLE);
                                    loginButton.setText(R.string.log_in);
                                    loginButton.setEnabled(true);
                                    notSuccessfulLogin();
                                }
                            } else {
                                loginCircle.setVisibility(View.INVISIBLE);
                                loginButton.setText(R.string.log_in);
                                loginButton.setEnabled(true);
                                //username doesnt exist in query
                                Log.d("LOGIN", "No such document");
                                noSuchUser();
                            }
                        }
                        else {
                            loginCircle.setVisibility(View.INVISIBLE);
                            loginButton.setText(R.string.log_in);
                            loginButton.setEnabled(true);
                            Log.d("LOGIN", "Auth failed.", task.getException());
                        }
                    }
                }
            });
        }
    }


    /**
     *Validation of inputs
     */
    private void emptyInputs(){
        Toast.makeText(this, "Inputs are empty!", Toast.LENGTH_SHORT).show();
    }

    /**
     *Login is fully authenticated
     */
    private void successfulLogin() {
        SharedPreferences preferences = getSharedPreferences("ID", 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("logged_in", true);
        editor.apply();

        Toast.makeText(this, "Success! Logged in.", Toast.LENGTH_SHORT).show();
    }

    /**
     *User is not defined in query
     */
    private void noSuchUser(){
        Toast.makeText(this, "No such username is registered. Please register.", Toast.LENGTH_SHORT).show();
    }

    /**
      *Email is not defined in query
     */
    private void noSuchEmail(){
        Toast.makeText(this, "No such email is registered. Please register.", Toast.LENGTH_SHORT).show();
    }

    /**
     *Incorrect fields for either user or password
     */
    private void notSuccessfulLogin() {
        Toast.makeText(this, "Failed. Check username and password.", Toast.LENGTH_SHORT).show();
    }

    /**
     *Incorrect fields for either user or password
     */
    public void signUpInstead(View view) {
        startActivity(new Intent(this, SignupActivity.class));
        finish();
    }

    /**
     *Goes to landing page after successful authentication
     */
    private void landingPageConnection() {
        startActivity(new Intent(this, LandingActivity.class));
        finish();
    }

    /**
     *Goes to profile page after sign up complete in database
     */
    private void profilePageConnection() {
        startActivity(new Intent(this, LandingActivity.class));
        finish();
    }

    /**
     * Listener for the enter key on the keyboard
     * when on the confirm password text box so that it
     * presses the signup button and clears the focus on press.
     */
    public void setConfirmPasswordEnterListener() {
        //hides keyboard and sends text box info as soon as enter is pressed
        password.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    password.clearFocus();
                    onLogInClick(loginButton);
                    View view = findViewById(R.id.light_login);
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });
    }

    /**
     * AESTHETIC method that changes the sign up button color on press, and on release
     * it executes the onSignupClick() method.
     */
    @SuppressLint("ClickableViewAccessibility")
    private void setOnTouchListener() {
        loginButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case ACTION_DOWN:
                        loginButton.setBackground(getResources().getDrawable(R.drawable.clicked_rectangle));
                        return true; // if you want to handle the touch event
                    case ACTION_UP:
                        loginButton.setBackground(getResources().getDrawable(R.drawable.rectangle));
                        onLogInClick(v);
                        return true; // if you want to handle the touch event
                }
                return false;
            }
        });
    }
}
