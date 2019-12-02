package com.seg.medex;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ForgotPassword extends AppCompatActivity {
    FirebaseFirestore db;
    private TextView username;
    private TextView password;
    private TextView password2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        this.db = FirebaseFirestore.getInstance();
        this.username = findViewById(R.id.username);
        this.password = findViewById(R.id.Password);
        this.password2 = findViewById(R.id.password);


    }
    public void changePass(View view){
        if(Utility.validPassword(password.toString()) && Utility.validPassword(password2.toString()) && username.getTextSize() != 0){
            db.collection("users").whereEqualTo("username",username)
                    .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot query) {
                    if(query.size() == 0){
                        Toast.makeText(ForgotPassword.this, "Username/Email does not exist, please enter a valid username/email or sign up", Toast.LENGTH_SHORT).show();
                    }else{
                        if(Utility.passwordsMatch(password.toString(),password2.toString())){
                            DocumentSnapshot doc = query.getDocuments().get(0);
                            Map<String, Object> add = new HashMap<>();
                            add.put("password",Crypto.getHash(password.toString()));
                            db.collection("users").document(doc.getId()).set(add);
                            Toast.makeText(ForgotPassword.this, "Password successfully changed", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                }});
        }
    }

    public void clickOnLogIn(View view){
        finish();
    }
}
