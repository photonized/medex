package com.seg.medex;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
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

    private String spinnerSelection;
    private Spinner spinner;

    private boolean emailValid;
    private boolean usernameValid;

    private ProgressBar emailCircle;
    private ProgressBar usernameCircle;

    private ImageView usernameCheck;
    private ImageView usernameX;

    private ImageView emailCheck;
    private ImageView emailX;

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

        //email circle recolor and hide
        this.emailCircle = findViewById(R.id.email_circle);
        Drawable emailDrawable = emailCircle.getIndeterminateDrawable().mutate();
        emailDrawable.setColorFilter(0xFF134153, android.graphics.PorterDuff.Mode.SRC_IN);
        emailCircle.setProgressDrawable(emailDrawable);
        this.emailCircle.setVisibility(View.INVISIBLE);

        //username circle recolor and hide
        this.usernameCircle = findViewById(R.id.username_circle);
        Drawable usernameDrawable = usernameCircle.getIndeterminateDrawable().mutate();
        usernameDrawable.setColorFilter(0xFF134153, android.graphics.PorterDuff.Mode.SRC_IN);
        emailCircle.setProgressDrawable(usernameDrawable);
        this.usernameCircle.setVisibility(View.INVISIBLE);

        //init username checkmark and hide
        this.usernameCheck = findViewById(R.id.username_check);
        this.usernameCheck.setVisibility(View.INVISIBLE);

        //init username x and hide
        this.usernameX = findViewById(R.id.username_x);
        this.usernameX.setVisibility(View.INVISIBLE);

        //init email checkmark and hide
        this.emailCheck = findViewById(R.id.email_check);
        this.emailCheck.setVisibility(View.INVISIBLE);

        //init email x and hide
        this.emailX = findViewById(R.id.email_x);
        this.emailX.setVisibility(View.INVISIBLE);


        //Username text box init
        this.username = findViewById(R.id.username);

        //Email text box init
        this.email = findViewById(R.id.email);

        //Password text boxes init
        this.password = findViewById(R.id.password);
        this.confirmPassword = findViewById(R.id.password2);

        username.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if(!hasFocus) {
                    usernameCircle.setVisibility(View.VISIBLE);
                    usernameCheck.setVisibility(View.INVISIBLE);
                    usernameX.setVisibility(View.INVISIBLE);
                    db.collection("users")
                            .whereEqualTo("username", username.getText().toString())
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        if(task.getResult().isEmpty()) {
                                            usernameCircle.setVisibility(View.INVISIBLE);
                                            usernameX.setVisibility(View.INVISIBLE);
                                            usernameCheck.setVisibility(View.VISIBLE);
                                        } else {
                                            usernameCircle.setVisibility(View.INVISIBLE);
                                            usernameCheck.setVisibility(View.INVISIBLE);
                                            usernameX.setVisibility(View.VISIBLE);
                                            usernameValid = true;
                                            usernameExists();
                                        }
                                    }
                                }
                            });
                } else {
                    usernameCircle.setVisibility(View.INVISIBLE);
                    usernameCheck.setVisibility(View.INVISIBLE);
                    usernameX.setVisibility(View.INVISIBLE);
                }
            }
        });

        email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    emailCircle.setVisibility(View.VISIBLE);
                    emailCheck.setVisibility(View.INVISIBLE);
                    emailX.setVisibility(View.INVISIBLE);
                    db.collection("users")
                            .whereEqualTo("email", email.getText().toString())
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        if(task.getResult().isEmpty()) {
                                            emailCircle.setVisibility(View.INVISIBLE);
                                            emailX.setVisibility(View.INVISIBLE);
                                            emailCheck.setVisibility(View.VISIBLE);
                                        } else {
                                            emailCircle.setVisibility(View.INVISIBLE);
                                            emailCheck.setVisibility(View.INVISIBLE);
                                            emailX.setVisibility(View.VISIBLE);
                                            emailValid = true;
                                            emailExists();
                                        }
                                    }
                                }
                            });
                } else {
                    emailCircle.setVisibility(View.INVISIBLE);
                    emailCheck.setVisibility(View.INVISIBLE);
                    emailX.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    public void onSignupClick(View view) {

        if(emailValid) {
            emailCircle.setVisibility(View.INVISIBLE);
            emailCheck.setVisibility(View.INVISIBLE);
            emailX.setVisibility(View.VISIBLE);
            emailExists();
            return;
        }

        if(usernameValid) {
            usernameCircle.setVisibility(View.INVISIBLE);
            usernameCheck.setVisibility(View.INVISIBLE);
            usernameX.setVisibility(View.VISIBLE);
            usernameExists();
            return;
        }

        findViewById(R.id.signup_button).setEnabled(false);
        final String emailText = email.getText().toString();
        final String usernameText = username.getText().toString();
        final String passwordText = password.getText().toString();
        final String confirmPasswordText = confirmPassword.getText().toString();

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

        db.collection("users")
                .whereEqualTo("username", username.getText().toString().toLowerCase())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if(task.getResult().isEmpty()) {
                                db.collection("users")
                                        .whereEqualTo("email", email.getText().toString().toLowerCase())
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    if(task.getResult().isEmpty()) {
                                                        spinnerSelection = spinner.getSelectedItem().toString();
                                                        int accountType = spinnerSelection.toLowerCase().startsWith("c") ? 0 : 1;
                                                        findViewById(R.id.signup_button).setEnabled(true);
                                                        Account account = new Account(usernameText, passwordText, accountType, emailText);
                                                        sendUserInfo(account);
                                                    } else {
                                                        findViewById(R.id.signup_button).setEnabled(true);
                                                        email.setTextColor(Color.RED);
                                                        emailExists();
                                                    }
                                                }
                                            }
                                        });
                            } else {
                                username.setTextColor(Color.RED);
                                usernameExists();
                            }
                        }
                    }
                });

    }

    private void sendUserInfo(Account account) {
        Map<String, Object> user = new HashMap<>();
        user.put("username", account.getUsername().toLowerCase());
        user.put("password", account.getPassword().toLowerCase());
        user.put("account_type", account.getAccountType());
        user.put("email", account.getEmail().toLowerCase());

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

    private void usernameExists() {
        Toast.makeText(this, "Username already exists.", Toast.LENGTH_SHORT).show();
    }

    private void emailExists() {
        Toast.makeText(this, "Email already exists.", Toast.LENGTH_SHORT).show();

    }

    public void loginInstead(View view) {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

}