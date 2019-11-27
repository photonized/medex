package com.seg.medex;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_UP;

public class ClinicActivity extends AppCompatActivity {

    Button clinicButton;
    Button serviceButton;
    Button timeButton;
    Button appointmentButton;
    private FirebaseFirestore db;
    private SharedPreferences preferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clinic);

        db = FirebaseFirestore.getInstance();
        preferences = getSharedPreferences("ID", 0);


        this.serviceButton = findViewById(R.id.manage_services_button);
        this.clinicButton = findViewById(R.id.manage_clinic_button);
        this.timeButton = findViewById(R.id.manage_time);
        this.appointmentButton = findViewById(R.id.view_appointments_button);


        View.OnTouchListener touchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case ACTION_DOWN:
                        v.setBackground(getResources().getDrawable(R.drawable.clicked_rectangle));
                        return true; // if you want to handle the touch event
                    case ACTION_UP:
                        v.setBackground(getResources().getDrawable(R.drawable.rectangle));
                        v.performClick();
                        return true; // if you want to handle the touch event
                }
                return false;
            }
        };

        clinicButton.setOnTouchListener(touchListener);
        serviceButton.setOnTouchListener(touchListener);
        timeButton.setOnTouchListener(touchListener);
        appointmentButton.setOnTouchListener(touchListener);

    }

    public void onResume() {

        super.onResume();

        Log.d("CLINIC ACTIVITY:", preferences.getBoolean("cc", false) + " " + preferences.getBoolean("sc", false) + " " + preferences.getBoolean("tc", false));
//        if (!preferences.getBoolean("cc", false)){
//            startClinicInfoEdit();
//            finishCreating();
//        } else if (!preferences.getBoolean("sc", false)){
//            startClinicServiceEdit();
//            finishCreating();
//        } else if (!preferences.getBoolean("tc", false)) {
//                startClinicTimeEdit();
//                finish();
//            }
    }

    private void finishCreating() {
        Toast.makeText(this, "Finish creating your profile!", Toast.LENGTH_SHORT).show();
    }

    public void onServiceClick(View view) {
        SharedPreferences sharedPreferences = getSharedPreferences("ID", 0);
        int accountType = sharedPreferences.getInt("account_type", 0);
        if(accountType != 1) {
            Toast.makeText(this, "You are not a clinic. You shouldn't be getting this button.", Toast.LENGTH_SHORT).show();
            return;
        }
        startActivity(new Intent(this, ClinicServicesActivity.class));
    }

    public void onAppointmentClick(View view) {
        SharedPreferences sharedPreferences = getSharedPreferences("ID", 0);
        int accountType = sharedPreferences.getInt("account_type", 0);
        if(accountType != 1) {
            Toast.makeText(this, "You are not a clinic. You shouldn't be getting this button.", Toast.LENGTH_SHORT).show();
            return;
        }
        startActivity(new Intent(this, ClinicViewAppointments.class));
    }

    private void startClinicInfoEdit() {
        startActivity(new Intent(this, ClinicEditProfileActivity.class));
    }

    private void startClinicTimeEdit() {
        startActivity(new Intent(this, ClinicTimeActivity.class));
    }

    private void startClinicServiceEdit() {
        startActivity(new Intent(this, ClinicServicesActivity.class));

    }

    public void onTimeClick(View view) {
        SharedPreferences sharedPreferences = getSharedPreferences("ID", 0);
        int accountType = sharedPreferences.getInt("account_type", 0);
        if(accountType != 1) {
            Toast.makeText(this, "You are not a clinic. You shouldn't be getting this button.", Toast.LENGTH_SHORT).show();
            return;
        }
        startActivity(new Intent(this, ClinicTimeActivity.class));
    }


    public void onClinicClick(View view) {
        SharedPreferences sharedPreferences = getSharedPreferences("ID", 0);
        int accountType = sharedPreferences.getInt("account_type", 0);
        if(accountType != 1) {
            Toast.makeText(this, "You are not a clinic. You shouldn't be getting this button.", Toast.LENGTH_SHORT).show();
            return;
        }
        startActivity(new Intent(this, ClinicEditProfileActivity.class));
    }
}
