package com.seg.medex;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


public class SignupActivity extends AppCompatActivity {

    private String item;
    private Spinner spinner;
    private EditText username;
    private EditText password;
    private EditText email;
    private EditText confirmPassword;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //SPINNER
        this.spinner = findViewById(R.id.spinner);

        this.db = FirebaseFirestore.getInstance();

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

        String emailText = email.getText().toString();
        String usernameText = username.getText().toString();
        String passwordText = password.getText().toString();
        String confirmPasswordText = confirmPassword.getText().toString();

        //empty username text
        if(usernameText.matches("")) {
            return;
        }

        //empty password text
        if(passwordText.matches("")) {
            return;
        }

        //empty password confirm text
        if(confirmPasswordText.matches("")) {
            return;
        }

        //empty email
        if(email.getText().toString().matches("") || email.getText().toString()) {
            return;
        }

        //if username taken blah blah blah check database
        final boolean[] exists = {false};
        db.collection("users")
                .whereEqualTo("username", username.getText().toString())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if(!task.getResult().isEmpty()) {
                                exists[0] = true;
                            }
                        }
                    }
                });
        if(exists[0]) {
            return;
        }
        //if passwords arent the same, don't allow
        if(!password.getText().toString().equals(confirmPassword.getText().toString())) {
            Toast.makeText(this, "Passwords do not match.", Toast.LENGTH_SHORT).show();
            return;
        }

        //if password is too weak (set criteria)
        if(Utility.weakPassword(password.getText().toString())) {
            return;
        }

        //else convert password to hash and sent info off to database
        String hash = Crypto.getHash(password.getText().toString());
        item = spinner.getSelectedItem().toString();


        //close
    }

    private void sendUserInfo(Account account) {

    }

    public void loginInstead(View view) {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

}