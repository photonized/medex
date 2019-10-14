package com.seg.medex;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

    private ImageView emailCheck;
    private ImageView emailX;
    private ProgressBar emailCircle;

    private ImageView usernameCheck;
    private ImageView usernameX;
    private ProgressBar usernameCircle;

    private ImageView passwordCheck;
    private ImageView passwordX;

    private ImageView confirmPasswordCheck;
    private ImageView confirmPasswordX;

    private EditText username;
    private EditText password;
    private EditText email;
    private EditText confirmPassword;

    private FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        initializeVariables();
        invisibleElements();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.spinner, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        setUsernameListener();
        setEmailListener();
        setPasswordListener();
        setConfirmPasswordListener();
    }

    public void onSignupClick(View view) {

        findViewById(R.id.signup_button).setEnabled(true);

        final String emailText = email.getText().toString();
        final String usernameText = username.getText().toString();
        final String passwordText = password.getText().toString();
        final String confirmPasswordText = confirmPassword.getText().toString();

        if(!Utility.validEmail(emailText)) {
            emailCircle.setVisibility(View.INVISIBLE);
            emailCheck.setVisibility(View.INVISIBLE);
            emailX.setVisibility(View.VISIBLE);
            emailExists();
            return;
        }

        if(!Utility.validUsername(usernameText)) {
            usernameCircle.setVisibility(View.INVISIBLE);
            usernameCheck.setVisibility(View.INVISIBLE);
            usernameX.setVisibility(View.VISIBLE);
            findViewById(R.id.signup_button).setEnabled(true);
            usernameExists();
            return;
        }

        if(!Utility.validPassword(passwordText)) {
            passwordCheck.setVisibility(View.INVISIBLE);
            passwordX.setVisibility(View.VISIBLE);
            findViewById(R.id.signup_button).setEnabled(true);
            invalidPassword();
            return;
        }

        if(!Utility.passwordsMatch(passwordText, confirmPasswordText)) {
            confirmPasswordCheck.setVisibility(View.INVISIBLE);
            confirmPasswordX.setVisibility(View.VISIBLE);
            findViewById(R.id.signup_button).setEnabled(true);
            passwordsDontMatch();
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
                                        .whereEqualTo("email", emailText.toLowerCase())
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
                                                        emailCircle.setVisibility(View.INVISIBLE);
                                                        emailCheck.setVisibility(View.INVISIBLE);
                                                        emailX.setVisibility(View.VISIBLE);
                                                        emailExists();
                                                    }
                                                }
                                            }
                                        });
                            } else {
                                findViewById(R.id.signup_button).setEnabled(true);
                                usernameCircle.setVisibility(View.INVISIBLE);
                                usernameCheck.setVisibility(View.INVISIBLE);
                                usernameX.setVisibility(View.VISIBLE);
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

    private void invalidPassword() {
        Toast.makeText(this, "Invalid password: make sure that your password is at least 8 characters.", Toast.LENGTH_SHORT).show();
    }

    private void passwordsDontMatch() {
        Toast.makeText(this, "Passwords don't match. Try again.", Toast.LENGTH_SHORT).show();
    }

    private void setEmailListener() {
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

    private void setUsernameListener() {
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
    }

    private void setPasswordListener() {
        password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    if(Utility.validPassword(password.getText().toString())) {
                        passwordCheck.setVisibility(View.VISIBLE);
                        passwordX.setVisibility(View.INVISIBLE);
                    } else {
                        passwordCheck.setVisibility(View.INVISIBLE);
                        passwordX.setVisibility(View.VISIBLE);
                        invalidPassword();
                    }
                } else {
                    passwordCheck.setVisibility(View.INVISIBLE);
                    passwordX.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private void setConfirmPasswordListener() {
        confirmPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    if(Utility.passwordsMatch(password.getText().toString(), confirmPassword.getText().toString())) {
                        confirmPasswordCheck.setVisibility(View.VISIBLE);
                        confirmPasswordX.setVisibility(View.INVISIBLE);
                    } else {
                        confirmPasswordCheck.setVisibility(View.INVISIBLE);
                        confirmPasswordX.setVisibility(View.VISIBLE);
                        passwordsDontMatch();
                    }
                } else {
                    confirmPasswordCheck.setVisibility(View.INVISIBLE);
                    confirmPasswordX.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private void initializeVariables() {
        //SPINNER
        this.spinner = findViewById(R.id.spinner);

        //Firebase Database
        this.db = FirebaseFirestore.getInstance();

        //email
        this.emailCircle = findViewById(R.id.email_circle);
        this.emailCheck = findViewById(R.id.email_check);
        this.emailX = findViewById(R.id.email_x);
        this.email = findViewById(R.id.email);

        //username
        this.usernameCircle = findViewById(R.id.username_circle);
        this.usernameCheck = findViewById(R.id.username_check);
        this.usernameX = findViewById(R.id.username_x);
        this.username = findViewById(R.id.username);

        //password
        this.password = findViewById(R.id.password);
        this.passwordCheck = findViewById(R.id.password_check);
        this.passwordX = findViewById(R.id.password_x);

        //confirm password
        this.confirmPassword = findViewById(R.id.password2);
        this.confirmPasswordCheck = findViewById(R.id.password2_check);
        this.confirmPasswordX = findViewById(R.id.password2_x);

    }

    private void invisibleElements() {

        //username
        this.usernameCheck.setVisibility(View.INVISIBLE);
        this.usernameX.setVisibility(View.INVISIBLE);
        //username circle recolor and hide
        Drawable usernameDrawable = usernameCircle.getIndeterminateDrawable().mutate();
        usernameDrawable.setColorFilter(0xFF134153, android.graphics.PorterDuff.Mode.SRC_IN);
        emailCircle.setProgressDrawable(usernameDrawable);
        this.usernameCircle.setVisibility(View.INVISIBLE);

        //email
        this.emailCheck.setVisibility(View.INVISIBLE);
        this.emailX.setVisibility(View.INVISIBLE);
        //email circle recolor and hide
        Drawable emailDrawable = emailCircle.getIndeterminateDrawable().mutate();
        emailDrawable.setColorFilter(0xFF134153, android.graphics.PorterDuff.Mode.SRC_IN);
        emailCircle.setProgressDrawable(emailDrawable);
        this.emailCircle.setVisibility(View.INVISIBLE);

        //password
        this.passwordX.setVisibility(View.INVISIBLE);
        this.passwordCheck.setVisibility(View.INVISIBLE);

        //confirm password
        this.confirmPasswordX.setVisibility(View.INVISIBLE);
        this.confirmPasswordCheck.setVisibility(View.INVISIBLE);

    }



    public void loginInstead(View view) {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

}