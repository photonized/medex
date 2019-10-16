package com.seg.medex;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;
import android.text.TextUtils;

import com.google.firebase.firestore.FirebaseFirestore;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Query;



/**
 * The activity for the Log in page.
 */

public class LoginActivity extends AppCompatActivity {

    /**
     * Username/Email text field.
     */
    private EditText username;

    /**
     * Password text field.
     */
    private EditText password;

    /**
     * The Firebase Firestore database object.
     */
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Screen Size Info
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int screenHeight = displaymetrics.heightPixels;
        int screenWidth = displaymetrics.widthPixels;

        //initializing the variables we are using
        this.db = FirebaseFirestore.getInstance();

        this.username = findViewById(R.id.username);
        this.username.setText("");

        this.password = findViewById(R.id.password);
        this.password.setText("");
    }

    public void onLogInClick(View view){
        final String usernameText = username.getText().toString();
        final String passwordText = password.getText().toString();

        db.collection("users").whereEqualTo("username", usernameText.toLowerCase())
        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (TextUtils.isEmpty(usernameText) || TextUtils.isEmpty(passwordText)) {
                            emptyInputs();
                        } else {
                            if (task.isSuccessful()) {
                                QuerySnapshot query = task.getResult();
                                if (!(query.isEmpty())) {
                                    //username exists
                                    Log.d("LOGIN", "Document exists");
                                    //remove successfullogin and check if the password is the same for the document in the query
                                    successfulLogin();
                                } else {
                                    //username doesnt exist
                                    Log.d("LOGIN", "No such document");
                                    notSuccessfulLogin();
                                }
                            } else {
                                Log.d("LOGIN", "Auth failed.", task.getException());
                                notSuccessfulLogin();
                            }
                        }
                    }
                    //the problem was this tiny meenie bracket
                });


    }

    private void emptyInputs(){
        Toast.makeText(this, "Inputs are empty!", Toast.LENGTH_SHORT).show();
    }

    private void successfulLogin() {
        Toast.makeText(this, "Success!", Toast.LENGTH_SHORT).show();
    }

    private void notSuccessfulLogin() {
        Toast.makeText(this, "Failed. Authentication problem.", Toast.LENGTH_SHORT).show();
    }

    public void signUpInstead(View view) {
        startActivity(new Intent(this, SignupActivity.class));
        finish();
    }

}
