package com.seg.medex;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_UP;

public class LandingActivity extends AppCompatActivity {

    /**
     * Logout button.
     */
    private Button logOutButton;

    private Button adminButton;

    private Button clinicButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);
        this.logOutButton = findViewById(R.id.logout_button);
        this.adminButton = findViewById(R.id.admin_button);
        this.adminButton.setVisibility(View.INVISIBLE);
        this.clinicButton = findViewById(R.id.clinicButton);
        this.clinicButton.setVisibility(View.INVISIBLE);
        setOnTouchListener();
        // Get firstname and role
        SharedPreferences sharedPreferences = getSharedPreferences("ID", 0);
        String firstName = sharedPreferences.getString("first_name","");
        //the 0 is a default value that is returned if account type is not found
        int role = sharedPreferences.getInt("account_type",0);

        if(role == 2) {
            this.adminButton.setVisibility(View.VISIBLE);
        }

        if(role == 1) {
            this.clinicButton.setVisibility(View.VISIBLE);
        }
        TextView textView = findViewById(R.id.welcomeMessage);
        textView.setText("Welcome " + firstName +"! You are logged in as a "+ roleConversion(role) + ".");
    }

    public void onLogOutClick(View view){
        SharedPreferences sharedPreferences = getSharedPreferences("ID", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("logged_in", false);
        editor.apply();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    public void onAdminClick(View view) {
        SharedPreferences sharedPreferences = getSharedPreferences("ID", 0);
        int accountType = sharedPreferences.getInt("account_type", 0);
        if(accountType != 2) {
            Toast.makeText(this, "You are not an admin. You shouldn't be getting this button.", Toast.LENGTH_SHORT).show();
            return;
        }
        startActivity(new Intent(this, AdminActivity.class));
    }


    public void onClinicClick(View view) {
        SharedPreferences sharedPreferences = getSharedPreferences("ID", 0);
        int accountType = sharedPreferences.getInt("account_type", 0);
        if(accountType != 1) {
            Toast.makeText(this, "You are not a clinic. You shouldn't be getting this button.", Toast.LENGTH_SHORT).show();
            return;
        }
        startActivity(new Intent(this, ClinicActivity.class));
    }

    private String roleConversion(int role){
        if (role == 0){
            return "client";
        }else if (role == 1){
            return "employee";
        }else if (role == 2){
            return "admin";
        }else{
            return "no account";
        }
    }

    /**
     * AESTHETIC method that changes the sign up button color on press, and on release
     * it executes the onSignupClick() method.
     */
    @SuppressLint("ClickableViewAccessibility")
    private void setOnTouchListener() {
        adminButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case ACTION_DOWN:
                        adminButton.setBackground(getResources().getDrawable(R.drawable.clicked_rectangle));
                        return true; // if you want to handle the touch event
                    case ACTION_UP:
                        adminButton.setBackground(getResources().getDrawable(R.drawable.rectangle));
                        onAdminClick(v);
                        return true; // if you want to handle the touch event
                }
                return false;
            }
        });
        logOutButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case ACTION_DOWN:
                        logOutButton.setBackground(getResources().getDrawable(R.drawable.clicked_rectangle));
                        return true; // if you want to handle the touch event
                    case ACTION_UP:
                        logOutButton.setBackground(getResources().getDrawable(R.drawable.rectangle));
                        onLogOutClick(v);
                        return true; // if you want to handle the touch event
                }
                return false;
            }
        });

        clinicButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case ACTION_DOWN:
                        clinicButton.setBackground(getResources().getDrawable(R.drawable.clicked_rectangle));
                        return true; // if you want to handle the touch event
                    case ACTION_UP:
                        clinicButton.setBackground(getResources().getDrawable(R.drawable.rectangle));
                        onClinicClick(v);
                        return true; // if you want to handle the touch event
                }
                return false;
            }
        });
    }
}
