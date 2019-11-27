package com.seg.medex;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UserAppointment extends AppCompatActivity {
    private TextView clinicName;
    private TextView address;
    private TextView service;
    private TextView dateAndTime;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_appointment);
        this.clinicName = findViewById(R.id.showClinicName);
        clinicName.setText((String) getIntent().getSerializableExtra("clinic_username"));
        this.address = findViewById(R.id.showAddress);
        address.setText((String) getIntent().getSerializableExtra("addy"));
        this.service = findViewById(R.id.showService);
        service.setText((String) getIntent().getSerializableExtra("service"));
        this.dateAndTime = findViewById(R.id.showDateandTime);
        String da = getIntent().getSerializableExtra("date")+"  "+getIntent().getSerializableExtra("time");
        dateAndTime.setText(da);
    }
}
