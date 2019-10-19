package com.seg.medex;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class LandingActivity extends AppCompatActivity {

    /**
    * The function that initalizes the screen with the welcome message.
    * @param savedInstanceState the last saved instance of the app
    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        // Get firstname and role from shared prefernces 
        SharedPreferences sharedPreferences = getSharedPreferences("ID", 0);
        String firstName = sharedPreferences.getString("first_name","");
        // 4 is a default value that is returned if the account type is not found
        int role = sharedPreferences.getInt("account_type", 4);
        TextView textView = findViewById(R.id.welcomeMessage);
        textView.setText("Welcome " + firstName +"! You are logged in as a "+ roleConversion(role) + ".");
    }

    /**
    * Logs out the user. 
    * @param view the associated button xml. 
    */
    public void onLogOutClick(View view){
        SharedPreferences sharedPreferences = getSharedPreferences("ID", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("logged_in", false);
        editor.apply();
        startActivity(new Intent(this, MainActivity.class));
        finish();

    }

    /**
    * Converts the role type to an human understandable form. 
    * @param role the role in integer format
    * @return the role in a readable format. 
    */

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
