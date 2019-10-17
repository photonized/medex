package com.seg.medex;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = getSharedPreferences("ID", 0);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        if(!preferences.contains("username")) {
            startActivity(new Intent(this, SignupActivity.class));
            finish();
            return;
        }

        if(!preferences.getBoolean("completed_profile", false)) {
            startActivity(new Intent(this, ProfileActivity.class));
            finish();
            return;
        }

        if(preferences.contains("username") && !preferences.getBoolean("logged_in", false)) {
            startActivity(new Intent(this, LandingActivity.class));
            finish();
            return;
        }

        Toast.makeText(this, "You're not supposed to be here. Report this bug to a developer.", Toast.LENGTH_SHORT);
        finish();
    }
}
