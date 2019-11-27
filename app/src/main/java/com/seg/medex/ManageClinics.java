package com.seg.medex;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
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

public class ManageClinics extends AppCompatActivity {

    private ListView list;
    private CustomAdapter adapter;
    FirebaseFirestore db;
    final ArrayList<String> elements = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_clinics);

        this.list = findViewById(R.id.clinic_list);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        adapter = new CustomAdapter(this, elements);

        db.collection("users")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult() != null) {
                        Log.d("This", String.valueOf(task.getResult().getDocuments().size()));
                        for (int i = 0; i < task.getResult().getDocuments().size(); i++) {
                            Log.d("AAAA", String.valueOf(task.getResult().getDocuments().get(i).get("account_type").getClass().getName()));
                            if (task.getResult().getDocuments().get(i).get("account_type").equals(Long.valueOf(1))) {
                                Log.d("AAAA", "AAAA");
                                elements.add(task.getResult().getDocuments().get(i).get("username").toString());
                                setAdapter(elements);
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

    private void setAdapter(ArrayList<String> elements) {
        adapter = new CustomAdapter(this, elements);
        list.setAdapter(adapter);
    }

    private void showDeleteDialog(final String username, final int pos) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this, R.style.DialogTheme);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.delete_dialog, null);
        dialogBuilder.setView(dialogView);

        final Button buttonCancel = dialogView.findViewById(R.id.buttonCancelChange);
        final Button buttonDelete = dialogView.findViewById(R.id.buttonDeleteChange);

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
                deleteClinic(username, pos);
                b.dismiss();
                elements.remove(pos);
                setAdapter(elements);
            }
        });
    }

    public void deleteClinic (final String username, final int pos) {

        db = FirebaseFirestore.getInstance();
        db.collection("users").whereEqualTo("username", username)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        String id = queryDocumentSnapshots.getDocuments().get(0).getId();
                        db.collection("users").document("/" + id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(ManageClinics.this, "Clinic " + username + " deleted!",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });


    }

    private class CustomAdapter extends BaseAdapter implements ListAdapter {

        private Context context;
        private ArrayList<String> list;

        CustomAdapter(@NonNull Context context, ArrayList<String> list) {
            this.context = context;
            this.list = list;
        }

        public void remove(Object item){
            list.remove(item);
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View view = convertView;
            if(view == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.service_item, null);
            }

            String service = list.get(position);

            Log.d("BBBB", service);
            TextView nameText = view.findViewById(R.id.name_info);
            nameText.setText(service);

            return view;
        }
    }

}
