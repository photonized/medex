package com.seg.medex;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_UP;

/**
 * The profile activity containing the inputs for the first name and last name.
 */
public class ProfileActivity extends AppCompatActivity {

    /**
     * First name text box.
     */
    private EditText firstName;

    /**
     * Last name text box.
     */
    private EditText lastName;

    /**
     * The shared preferences of the app containing the user info.
     */
    private SharedPreferences preferences;

    /**
     * The editor for the shared preferences.
     */
    private SharedPreferences.Editor editor;

    /**
     * The database object for Firebase Firestore.
     */
    private FirebaseFirestore db;

    /**
     * Continue button.
     */
    private Button continueButton;

    /**
     * Continue button circle.
     */
    private ProgressBar continueCircle;

    /**
     * When the Activity gets loaded up, the database is instantiated as well as all the text boxes.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        this.preferences = getSharedPreferences("ID", 0);

        this.firstName = findViewById(R.id.first_name);
        this.lastName = findViewById(R.id.last_name);
        this.editor = preferences.edit();

        this.continueButton = findViewById(R.id.continue_button);
        this.continueCircle = findViewById(R.id.continue_circle);
        this.continueCircle.setVisibility(View.INVISIBLE);
        this.db = FirebaseFirestore.getInstance();

        setLastNameEnterListener();
        setOnTouchListener();
    }

    /**
     * When the continue button is clicked, then the profile information is sent off to the server and logged in the SharedPreferences.
     * @param view the view in question. In this case, it's the continue button.
     */
    public void onContinueClick(View view) {

        findViewById(R.id.continue_button).setEnabled(false);

        //if the first name isn't valid
        if(!Utility.validName(firstName.getText().toString())) {
            invalidFirstName();
            findViewById(R.id.continue_button).setEnabled(true);
            return;
        }

        //if the last name isn't valid
        if(!Utility.validName(lastName.getText().toString())) {
            invalidLastName();
            findViewById(R.id.continue_button).setEnabled(true);
            return;
        }

        //if you got here without somehow logging in, or the username is empty.
        if(preferences.getString("username", "").equals("")) {
            findViewById(R.id.continue_button).setEnabled(true);
            editor.clear();
            openLogin();
            return;
        }

        //gets the document name that the username is associated to
        continueCircle.setVisibility(View.VISIBLE);
        continueButton.setText("");
        db.collection("users").whereEqualTo("username", preferences.getString("username", ""))
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                editor.putBoolean("created_profile", true);
                editor.apply();
                sendToFirebase(queryDocumentSnapshots.getDocuments().get(0).getReference().getId());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                continueCircle.setVisibility(View.INVISIBLE);
                continueButton.setText(R.string.continue_profile);
                Log.d("Profile: ", "failed. Message a developer.");
            }
        });
    }

    /**
     * Opens the login page and closes this one.
     */
    private void openLogin() {
        startActivity(new Intent(this, LandingActivity.class));
        finish();
    }

    /**
     * Opens the landing page and closes this one.
     */
    private void openLanding() {
        startActivity(new Intent(this, LandingActivity.class));
        finish();
    }

    /**
     * Sends the information to firebase and opens the landing page.
     * @param documentName name of the document to overwrite.
     */
    private void sendToFirebase(String documentName) {
        Map<String, Object> profileInfo = new HashMap<>();
        profileInfo.put("first_name", firstName.getText().toString());
        profileInfo.put("last_name", lastName.getText().toString());
        profileInfo.put("created_profile", true);

        db.collection("users").document(documentName)
                .set(profileInfo, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("PROFILE ACTIVITY: ", "DocumentSnapshot successfully written!");
                        editor.putString("first_name", firstName.getText().toString());
                        editor.putString("last_name", lastName.getText().toString());
                        if(preferences.contains("created_profile")) {
                            editor.remove("created_profile");
                        }
                        editor.putBoolean("created_profile", true);
                        editor.apply();
                        continueCircle.setVisibility(View.INVISIBLE);
                        continueButton.setText(R.string.continue_profile);
                        openLanding();
                        findViewById(R.id.continue_button).setEnabled(true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("PROFILE ACTIVITY: ", "Error writing document", e);
                        continueCircle.setVisibility(View.INVISIBLE);
                        continueButton.setText(R.string.continue_profile);
                        findViewById(R.id.continue_button).setEnabled(true);
                    }
                });
    }

    /**
     * Toast message when user inputs an invalid first name.
     */
    private void invalidFirstName() {
        Toast.makeText(this, "Invalid first name. Try again.", Toast.LENGTH_SHORT);
    }

    /**
     * Toast message when the user inputs an invalid last name.
     */
    private void invalidLastName() {
        Toast.makeText(this, "Invalid last name. Try again.", Toast.LENGTH_SHORT);
    }

    /**
     * Listener for the enter key on the keyboard
     * when on the confirm password text box so that it
     * presses the signup button and clears the focus on press.
     */
    public void setLastNameEnterListener() {
        //hides keyboard and sends text box info as soon as enter is pressed
        lastName.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    lastName.clearFocus();
                    onContinueClick(continueButton);
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
        continueButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case ACTION_DOWN:
                        continueButton.setBackground(getResources().getDrawable(R.drawable.clicked_rectangle));
                        return true; // if you want to handle the touch event
                    case ACTION_UP:
                        continueButton.setBackground(getResources().getDrawable(R.drawable.rectangle));
                        onContinueClick(v);
                        return true; // if you want to handle the touch event
                }
                return false;
            }
        });
    }
}
