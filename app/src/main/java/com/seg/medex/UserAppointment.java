package com.seg.medex;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collection;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_appointment);
        this.showClinicName = findViewById(R.id.showClinicName);
        this.showAddress = findViewById(R.id.showAddress);
        this.showService = findViewById(R.id.showService);
        this.showDateandTime = findViewById(R.id.showDateandTime);
        this.clinicUserName = (String) getIntent().getSerializableExtra("clinic_username");
        this.db = FirebaseFirestore.getInstance();
        //retreives the user's id
        this.preferences = getSharedPreferences("ID", 0);

        //retrieves users appointments
        db.collection("users").whereEqualTo("username", preferences.getString("username",""))
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot query) {
                DocumentSnapshot doc = query.getDocuments().get(0);
                Map<String, List<String>>  appointments = (Map<String, List<String>>) doc.get("appointments");
                for(Map.Entry<String, List<String>> entry : appointments.entrySet()){
                    if(entry.getKey().compareTo(clinicUserName)==0){
                        //complete implementation for receiving the appointment
                        break;
                    }
                }

            }});

    }
}

