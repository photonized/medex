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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;


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
        findViewById(R.id.signup_button).setEnabled(false);
        final String emailText = email.getText().toString();
        final String usernameText = username.getText().toString();
        final String passwordText = password.getText().toString();
        String confirmPasswordText = confirmPassword.getText().toString();

        //check username text
        if(!Utility.validUsername(usernameText)) {
            findViewById(R.id.signup_button).setEnabled(true);
            return;
        }

        //check password
        if(!Utility.passwordsMatch(passwordText, confirmPasswordText)) {
            findViewById(R.id.signup_button).setEnabled(true);
            return;
        }

        if(!Utility.validPassword(passwordText)) {
            findViewById(R.id.signup_button).setEnabled(true);
            return;
        }

        //empty email
        if(!Utility.validEmail(emailText)) {
            findViewById(R.id.signup_button).setEnabled(true);
            return;
        }

        //if username taken blah blah blah check database
        db.collection("users")
                .whereEqualTo("username", username.getText().toString())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if(task.getResult().isEmpty()) {
                                item = spinner.getSelectedItem().toString();
                                int accountType = item.toLowerCase().startsWith("c") ? 0 : 1;
                                Account account = new Account(usernameText, passwordText, accountType, emailText);
                                findViewById(R.id.signup_button).setEnabled(true);
                                sendUserInfo(account);
                            } else {
                                findViewById(R.id.signup_button).setEnabled(true);
                                accountExists();
                            }
                        }
                    }
                });
    }

    private void sendUserInfo(Account account) {
        Map<String, Object> user = new HashMap<>();
        user.put("username", account.getUsername());
        user.put("password", account.getPassword());
        user.put("account_type", account.getAccountType());
        user.put("token", account.getToken());

        db.collection("users")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("SIGNUP: ", "DocumentSnapshot written with ID: " + documentReference.getId());
                        successfulLogin();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("SIGNUP: " , "ERROR: ", e);
                        failedLogin();
                    }
                });
    }

    private void successfulLogin() {
        Toast.makeText(this, "Success! Account sent off.", Toast.LENGTH_SHORT).show();
    }

    private void failedLogin() {
        Toast.makeText(this, "Failed! Something went wrong.", Toast.LENGTH_SHORT).show();
    }

    private void accountExists() {
        Toast.makeText(this, "Account already exists.", Toast.LENGTH_SHORT).show();
    }

    public void loginInstead(View view) {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

}