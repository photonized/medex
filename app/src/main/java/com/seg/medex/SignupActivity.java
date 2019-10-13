package com.seg.medex;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;


public class SignupActivity extends AppCompatActivity {

    private String item;
    private Spinner spinner;
    private EditText username;
    private EditText password;
    private EditText email;
    private EditText confirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //SPINNER
        this.spinner = findViewById(R.id.spinner);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.spinner, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);


        //Username
        this.username = findViewById(R.id.username);

        //Email
        this.email = findViewById(R.id.email);

        //Passwords
        this.password = findViewById(R.id.password);
        this.confirmPassword = findViewById(R.id.password2);
    }

    public void onSignupClick(View view) {
        System.out.println(password.getText().toString());
        //if username taken blah blah blah check database
        if(username.getText().toString().equals("")) {

        }
        //if passwords arent the same, don't allow
        if(!password.getText().toString().equals(confirmPassword.getText().toString())) {
            Toast.makeText(this, "Passwords do not match.", Toast.LENGTH_SHORT).show();
        }

        //if email already exists

        //if password is too weak (set criteria)

        //if email is invalid

        //else convert password to hash and sent info off to database
        String hash = Crypto.getHash(password.getText().toString());
        item = spinner.getSelectedItem().toString();

        //close
    }

    public void loginInstead(View view) {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

}