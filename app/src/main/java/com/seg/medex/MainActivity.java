package com.seg.medex;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Arrays;

/**
 * activity that runs when the app is launched
 */
public class MainActivity extends AppCompatActivity {

    /**
     * The Firebase Firestore database object.
     */
    private FirebaseFirestore db;

    /**
     * reference to the SharedPreferences
     */
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = getSharedPreferences("ID", 0); //gets a reference to SharedPreferences
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        //if there is no local data saved for this app (new user) it will send the user to the sign up page
        if(!preferences.contains("username")) {
            startActivity(new Intent(this, SignupActivity.class));
            finish();
            return;
        }

        //if the user was not logged in the last time they exited the app they are sent to the login page
        if(!preferences.getBoolean("logged_in", false)) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }


        if(preferences.contains("username") && preferences.getBoolean("logged_in", false)) {

                this.db = FirebaseFirestore.getInstance();
                db.collection("users").whereEqualTo("username", preferences.getString("username", ""))
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            QuerySnapshot query = task.getResult();
                            if(!(query.isEmpty())) {
                                //checks if the local data matches that in the database
                                if(preferences.getString("username", "").equals(query.getDocuments().get(0).get("username"))
                                && preferences.getString("password", "").equals(query.getDocuments().get(0).get("password"))
                                && preferences.getString("email", "").equals(query.getDocuments().get(0).get("email"))) {
                                    //if the user has not yet completed the profile creation process
                                    if(!(boolean)query.getDocuments().get(0).get("created_profile")) {
                                        //updates the local information with the information on the database
                                        SharedPreferences.Editor editor = preferences.edit();
                                        editor.clear();
                                        editor.putBoolean("created_profile", (boolean)query.getDocuments().get(0).get("created_profile"));
                                        editor.putString("email", (String)query.getDocuments().get(0).get("email"));
                                        editor.putString("first_name", (String)query.getDocuments().get(0).get("first_name"));
                                        editor.putString("last_name", (String)query.getDocuments().get(0).get("last_name"));
                                        editor.putString("username", (String)query.getDocuments().get(0).get("username"));
                                        editor.putString("password", (String)query.getDocuments().get(0).get("password"));
                                        editor.putInt("account_type", ((Long)query.getDocuments().get(0).get("account_type")).intValue());
                                        editor.putBoolean("logged_in", true);
                                        editor.apply();
                                        //the user is sent to the page to complete their profile
                                        sendToProfile();
                                    } else {
                                        //updates the local information with the information on the database
                                        SharedPreferences.Editor editor = preferences.edit();
                                        editor.clear();
                                        editor.putBoolean("created_profile", (boolean)query.getDocuments().get(0).get("created_profile"));
                                        editor.putString("email", (String)query.getDocuments().get(0).get("email"));
                                        editor.putString("first_name", (String)query.getDocuments().get(0).get("first_name"));
                                        editor.putString("last_name", (String)query.getDocuments().get(0).get("last_name"));
                                        editor.putString("username", (String)query.getDocuments().get(0).get("username"));
                                        editor.putString("password", (String)query.getDocuments().get(0).get("password"));
                                        editor.putInt("account_type", ((Long)query.getDocuments().get(0).get("account_type")).intValue());
                                        editor.putBoolean("logged_in", true);
                                        editor.apply();
                                        //send the user to the landing page
                                        sendToLanding();
                                    }
                                }
                            }
                        } else {
                            //if the local information does not match the information on the database
                            //resets the local information
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.clear();
                            editor.apply();
                            Log.d("LOGIN", "AutoLogin failed", task.getException());
                            //sends the user to the login page
                            sendToLogin();
                        }

                    }
                });

            return;
        }

        Toast.makeText(this, "You're not supposed to be here. Report this bug to a developer.", Toast.LENGTH_SHORT);
        finish();
    }

    /**
     * Sends the user to the landing page
     */
    private void sendToLanding() {
        startActivity(new Intent(this, LandingActivity.class));
        finish();
    }

    /**
     * Sends the user to the login page
     */
    private void sendToLogin() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    /**
     * Sends the user to the profile page
     */
    private void sendToProfile() {
        startActivity(new Intent(this, ProfileActivity.class));
        finish();
    }
}
