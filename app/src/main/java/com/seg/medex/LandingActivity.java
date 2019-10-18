package com.seg.medex;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class LandingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        // Get firstname and role
        SharedPreferences sharedPreferences = getSharedPreferences("ID", 0);
        String firstName = sharedPreferences.getString("firstname","");
        //the 0 is a default value that is returned if account type is not found
        int role = sharedPreferences.getInt("account_type",4);
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
