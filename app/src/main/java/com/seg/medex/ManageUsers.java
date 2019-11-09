package com.seg.medex;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ManageUsers extends AppCompatActivity {

    private ListView list;
    private ArrayAdapter<String> adapter;
    FirebaseFirestore db;
    final ArrayList<String> elements = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_users);

        this.list = findViewById(R.id.user_list);

        db = FirebaseFirestore.getInstance();

        adapter = new ArrayAdapter<>(this, R.layout.user_item);

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

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String username = elements.get(i);
                showDeleteDialog(username, i);
            }
        });
    }

    private void showDeleteDialog(final String username, final int pos) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this, R.style.DialogTheme);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.delete_dialog, null);
        dialogBuilder.setView(dialogView);

        final TextView buttonCancel = dialogView.findViewById(R.id.buttonCancelProduct);
        final TextView buttonDelete = dialogView.findViewById(R.id.buttonDeleteProduct);

        dialogBuilder.setTitle(username);
        final AlertDialog b = dialogBuilder.create();
        b.show();

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                b.dismiss();
            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteUser(username, pos);
                b.dismiss();
                elements.remove(pos);
            }
        });
    }

    public void deleteUser (final String username, final int pos) {


        db.collection("users").whereEqualTo("username", username)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        String id = queryDocumentSnapshots.getDocuments().get(0).getId();
                        db.collection("users").document("/" + id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                adapter.remove(adapter.getItem(pos));
                                list.setAdapter(adapter);
                                Toast.makeText(ManageUsers.this, "User " + username + " deleted !",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });


    }
}