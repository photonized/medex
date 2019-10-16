package com.seg.medex;

import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;

public class LandingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        // Get firstname and role
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        //String firstName = sharedPreferences.getString("firstname","");
        int role = sharedPreferences.getInt("account_type","");
        TextView textView = findViewById(R.id.welcomeMessage);
        //textView.setText("Welcome " +firstName +"! You are logged in as "+ roleConversion(role));
    }

    public static String roleConversion(int role){
        if (role == 0){
            return "client";
        }else if (role == 1){
            return "employee";
        }else{
            return "admin";
        }
    }

}
