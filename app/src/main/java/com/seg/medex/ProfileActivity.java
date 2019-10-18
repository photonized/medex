package com.seg.medex;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private EditText firstName;
    private EditText lastName;
    private SharedPreferences preferences;
    SharedPreferences.Editor editor;
    FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        this.preferences = getSharedPreferences("ID", 0);

        this.firstName = findViewById(R.id.first_name);
        this.lastName = findViewById(R.id.last_name);
        this.editor = preferences.edit();
        db = FirebaseFirestore.getInstance();
        Log.d("FOR VLAD!!!!!!!!!!!! ", String.valueOf(preferences.getAll().isEmpty()));
        Log.d("FOR VLAD!!!!!!!!!!!! ", preferences.getAll().get("username").toString());
    }

    public void onContinueClick(View view) {
        findViewById(R.id.continue_button).setEnabled(false);
        if(!Utility.validName(firstName.getText().toString())) {
            findViewById(R.id.continue_button).setEnabled(true);
            Log.d("FOR VLAD!!!!!!!!!!!! ", "validname");
            return;
        }
        if(!Utility.validName(lastName.getText().toString())) {
            findViewById(R.id.continue_button).setEnabled(true);
            Log.d("FOR VLAD!!!!!!!!!!!! ", "validlastname");
            return;
        }

        if(preferences.getString("username", "").equals("")) {
            findViewById(R.id.continue_button).setEnabled(true);
            Log.d("FOR VLAD!!!!!!!!!!!! ", "username is empty");
            return;
        }

        db.collection("users").whereEqualTo("username", preferences.getString("username", ""))
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                editor.putBoolean("completed_profile", true);
                editor.apply();
                sendToFirebase(queryDocumentSnapshots.getDocuments().get(0).getReference().getId());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                finish();
            }
        });


    }

    private void openLanding() {
        startActivity(new Intent(this, LandingActivity.class));
        finish();
    }

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
                        openLanding();
                        findViewById(R.id.continue_button).setEnabled(true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("PROFILE ACTIVITY: ", "Error writing document", e);
                        findViewById(R.id.continue_button).setEnabled(true);
                    }
                });
    }
}
