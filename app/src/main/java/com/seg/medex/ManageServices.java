package com.seg.medex;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;

import android.widget.EditText;
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
import java.util.HashMap;
import java.util.Map;

public class ManageServices extends AppCompatActivity {


    private ListView list;
    private CustomAdapter adapter;
    final ArrayList<String[]> elements = new ArrayList<>();
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_services);

        this.list = findViewById(R.id.services_list);


        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("services")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult() != null) {
                        Log.d("This", String.valueOf(task.getResult().getDocuments().size()));
                        for (int i = 0; i < task.getResult().getDocuments().size(); i++) {
                                Log.d("AAAA", task.getResult().getDocuments().get(i).get("name").toString());
                                elements.add(new String[]{task.getResult().getDocuments().get(i).get("name").toString(), task.getResult().getDocuments().get(i).get("role").toString()});
                                setAdapter(elements);
                        }
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
                String username = elements.get(i)[0];
                showDeleteEditDialog(username, i);
            }
        });
    }

    private void setAdapter(ArrayList<String[]> elements) {
        adapter = new CustomAdapter(this, elements);
        list.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private class CustomAdapter extends BaseAdapter implements ListAdapter {

        private Context context;
        private ArrayList<String[]> list;

        CustomAdapter(@NonNull Context context, ArrayList<String[]> list) {
            this.context = context;
            this.list = list;
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

            String[] service = list.get(position);

            Log.d("BBBB", service[0]);
            TextView nameText = view.findViewById(R.id.name_info);
            nameText.setText(service[0]);

            TextView roleText = view.findViewById(R.id.role_info);
            roleText.setText(service[1]);

            return view;
        }
    }
    private void showDeleteEditDialog(final String service, final int pos) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this, R.style.DialogTheme);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.delete_edit_dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText editTextName = (EditText) dialogView.findViewById(R.id.name);
        final EditText editTextRole  = (EditText) dialogView.findViewById(R.id.role);
        final Button buttonEdit = (Button) dialogView.findViewById(R.id.buttonEditProduct);
        final Button buttonDelete = (Button) dialogView.findViewById(R.id.buttonDeleteProduct);

        dialogBuilder.setTitle(service);
        final AlertDialog b = dialogBuilder.create();
        b.show();

        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editServices(service,editTextName.toString());
                b.dismiss();
            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteServices(service, pos);
                b.dismiss();
            }
        });
    }

    public void editServices(final String nam, final String ediName){
        db = FirebaseFirestore.getInstance();
        db.collection("services").whereEqualTo("name", nam)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        String id = queryDocumentSnapshots.getDocuments().get(0).getId();
                        Map<String, Object> us = new HashMap<>();
                        us.put("name",ediName.toLowerCase());
                        db.collection("services").document("/" + id).update(us);
                        list.setAdapter(adapter);
                        Toast.makeText(ManageServices.this, "Service " + ediName + " deleted!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void deleteServices (final String name, final int pos) {

        db = FirebaseFirestore.getInstance();
        db.collection("services").whereEqualTo("name", name)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        String id = queryDocumentSnapshots.getDocuments().get(0).getId();
                        db.collection("services").document("/" + id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                elements.remove(pos);
                                setAdapter(elements);
                                Toast.makeText(ManageServices.this, "Service " + name + " deleted!",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
    }

    public void onAddServiceClick(View view) {
        showAddDialog();
    }

    private void showAddDialog() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.add_service_popup, null);
        dialogBuilder.setView(dialogView);

        final Button buttonCancel = (Button) dialogView.findViewById(R.id.buttonCancelService);
        final Button buttonAdd = (Button) dialogView.findViewById(R.id.buttonConfirmService);

        dialogBuilder.setTitle("Create a service");
        final AlertDialog b = dialogBuilder.create();
        b.show();

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                b.dismiss();
            }
        });


    }
}
