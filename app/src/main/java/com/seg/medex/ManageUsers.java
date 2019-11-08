package com.seg.medex;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ManageUsers extends AppCompatActivity {

    private ListView list;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_users);

        this.list = findViewById(R.id.user_list);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);

        final ArrayList<String> elements = new ArrayList<>();
        db.collection("users")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult() != null) {
                        Log.d("This", String.valueOf(task.getResult().getDocuments().size()));
                        for (int i = 0; i < task.getResult().getDocuments().size(); i++) {
                            if (task.getResult().getDocuments().get(i).get("account_type").equals(Long.valueOf(0))) {
                                Log.d("AAAA", "AAAA");
                                elements.add(task.getResult().getDocuments().get(i).get("username").toString());
                                adapter.add(task.getResult().getDocuments().get(i).get("username").toString());
                            }
                        }
                        list.setAdapter(adapter);

                    }
                }
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Manage Clinics: ", "Failed. Contact a developer.");
                    }
                });
    }

    public void deleteUser (String username){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if (db.collection("users").document(username).toString() !=  "0"){
            Toast.makeText(getApplicationContext(), "Not a user", Toast.LENGTH_LONG).show();
            return;
        }
        db.collection("users").document(username).delete();
        Toast.makeText(getApplicationContext(), "User Deleted", Toast.LENGTH_LONG).show();
    }


}
