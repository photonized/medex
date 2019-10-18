package com.seg.medex;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Arrays;


public class MainActivity extends AppCompatActivity {

    /**
     * The Firebase Firestore database object.
     */
    private FirebaseFirestore db;

    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = getSharedPreferences("ID", 0);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        Log.d("VLAD!!!!!!!!!", Arrays.asList(preferences.getAll().values().toArray()).toString());

        if(!preferences.contains("username")) {
            Log.d("VLAD!!!!!!!!!", "username");
            startActivity(new Intent(this, SignupActivity.class));
            finish();
            return;
        }

        if(!preferences.getBoolean("logged_in", false)) {
            Log.d("VLAD!!!!!!!!!", "logged_in");
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }


        if(preferences.contains("username") && preferences.getBoolean("logged_in", false)) {

                Log.d("VLAD!!!!!!!!!", "else");

                this.db = FirebaseFirestore.getInstance();
                db.collection("users").whereEqualTo("username", preferences.getString("username", ""))
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if(task.isSuccessful()) {
                            QuerySnapshot query = task.getResult();
                            if(!(query.isEmpty())) {
                                if(preferences.getString("username", "").equals(query.getDocuments().get(0).get("username"))
                                && preferences.getString("password", "").equals(query.getDocuments().get(0).get("password"))
                                && preferences.getString("email", "").equals(query.getDocuments().get(0).get("email"))) {
                                    if(!preferences.getBoolean("completed_profile", false)) {
                                        sendToProfile();
                                    } else {
                                        sendToLanding();
                                    }
                                }

                            }
                        } else {
                            Log.d("VLAD!!!!!!!!!", "failed");
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.clear();
                            editor.apply();
                            Log.d("LOGIN", "AutoLogin failed", task.getException());
                            sendToLogin();
                        }

                    }
                });

            Log.d("VLAD!!!!!!!!!", "outer");
            return;
        }

        System.out.println(Arrays.asList(preferences.getAll().values().toArray()));
        System.out.println("huh");
        Toast.makeText(this, "You're not supposed to be here. Report this bug to a developer.", Toast.LENGTH_SHORT);
        finish();
    }

    private void sendToLanding() {
        Log.d("VLAD!!!!!!!!!", "landing");
        startActivity(new Intent(this, LandingActivity.class));
        finish();
    }

    private void sendToLogin() {
        Log.d("VLAD!!!!!!!!!", "landing");
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    private void sendToProfile() {
        Log.d("VLAD!!!!!!!!!", "completed_profile");
        startActivity(new Intent(this, ProfileActivity.class));
        finish();
    }
}
