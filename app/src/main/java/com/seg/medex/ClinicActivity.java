package com.seg.medex;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_UP;

public class ClinicActivity extends AppCompatActivity {

    Button clinicButton;
    Button serviceButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clinic);

        this.serviceButton = findViewById(R.id.manage_services_button);
        this.clinicButton = findViewById(R.id.manage_clinic_button);


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
