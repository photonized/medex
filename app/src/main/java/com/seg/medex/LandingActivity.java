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
        String firstName = sharedPreferences.getString("firstname","");
        //the 0 is a default value that is returned if account type is not found
        int role = sharedPreferences.getInt("account_type",4);
        TextView textView = findViewById(R.id.welcomeMessage);
        textView.setText("Welcome " +firstName +"! You are logged in as a"+ roleConversion(role));
    }

    public static String roleConversion(int role){
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

}
