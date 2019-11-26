package com.seg.medex;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserAppointment extends AppCompatActivity {

    private TextView showClinicName;
    private TextView showAddress;
    private TextView showService;
    private TextView showDateandTime;
    private String clinicUserName;
    FirebaseFirestore db;
    private SharedPreferences preferences;
    private String docId;

    //have a variable that stores the date and time from the database


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_appointment);
        this.clinicUserName = (String) getIntent().getSerializableExtra("clinic_username");
        this.showClinicName = findViewById(R.id.showClinicName);
        showClinicName.setText(clinicUserName);
        this.showAddress = findViewById(R.id.showAddress);
        showAddress.setText((String) getIntent().getSerializableExtra("address"));
        this.showService = findViewById(R.id.showService);
        showService.setText((String) getIntent().getSerializableExtra("service"));
        this.showDateandTime = findViewById(R.id.showDateandTime);

        //input date and time here for show date and time

        this.db = FirebaseFirestore.getInstance();
        //retreives the user's id
        this.preferences = getSharedPreferences("ID", 0);


//        db.collection("users").whereEqualTo("username", preferences.getString("username",""))
//                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//            @Override
//            public void onSuccess(QuerySnapshot query) {
//                DocumentSnapshot doc = query.getDocuments().get(0);
//                Map<String, List<String>>  appointments = (Map<String, List<String>>) doc.get("appointments");
//                docId = doc.getId();
//                for(Map.Entry<String, List<String>> entry : appointments.entrySet()){
//                    if(entry.getKey().compareTo(clinicUserName)==0){
//                        List<String> info = entry.getValue();
//                        Integer dandt = Integer.parseInt(info.get(0));
//                        String service = info.get(1);
//                        break;
//                    }
//                }
//
//
//            }});

    }

    public void onCheckInClick(View view){
        // add logic for time when nader finishes his part
        if(true){
            Toast.makeText(this, "It's not your appointment time, wait boi.", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("users").whereEqualTo("username", preferences.getString("username",""))
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot query) {
                DocumentSnapshot doc = query.getDocuments().get(0);
                Map<String, List<String>>  appointments = (Map<String, List<String>>) doc.get("appointments");

                //the map thats going to update ur database
                Map<String, Object> docData = new HashMap<>();
                Map<String, List<String>>  newApps = new HashMap<String, List<String>>();

                for(Map.Entry<String, List<String>> entry : appointments.entrySet()){
                    if(!(entry.getKey().compareTo(clinicUserName)==0)){
                        newApps.put(entry.getKey(),entry.getValue());

                    }
                }

                docData.put("appointements",newApps);


                db.collection("users").document(docId).set(docData);
            }});

        //write the code to remove the User From Clinic'c thing as well

        //startActivity(new Intent(this, ProfileActivity.class));
    }
}

