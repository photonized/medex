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
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.text.TextUtils;

import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ManageServices extends AppCompatActivity {


    private ListView list;
    private CustomAdapter adapter;
    final ArrayList<String[]> elements = new ArrayList<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_services);

        this.list = findViewById(R.id.services_list);

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

    private void showDeleteEditDialog(final String service, final int pos) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this, R.style.DialogTheme);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.delete_edit_dialog, null);
        dialogBuilder.setView(dialogView);

        final Button buttonCancel = dialogView.findViewById(R.id.buttonCancelChange);
        final Button buttonEdit = dialogView.findViewById(R.id.buttonEditChange);
        final Button buttonDelete = dialogView.findViewById(R.id.buttonDeleteChange);

        dialogBuilder.setTitle(service);
        final AlertDialog b = dialogBuilder.create();
        b.show();

        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showServiceEditDialog(service, pos);
                b.dismiss();
            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteServices(service);
                b.dismiss();
                elements.remove(pos);
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                b.dismiss();
            }
        }); 
    }

    private void showServiceEditDialog(final String service, final int pos){

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this, R.style.DialogTheme);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.service_edit_dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText editTextName =  dialogView.findViewById(R.id.name);
        final EditText editTextRole = dialogView.findViewById(R.id.role);
        final Button buttonCancel = dialogView.findViewById(R.id.buttonCancelChange);
        final Button buttonConfirm = dialogView.findViewById(R.id.buttonEditChange);

        dialogBuilder.setTitle(service);
        final AlertDialog b = dialogBuilder.create();
        b.show();

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                b.dismiss();
            }
        });

        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newName = editTextName.getText().toString().trim();
                String newRole = editTextRole.getText().toString().trim();
                editServices(service, newName, newRole);
                b.dismiss();
                elements.set(pos, new String[]{newName,newRole});
            }
        });

    }

    private void showAddDialog() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this, R.style.DialogTheme);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.add_service_popup, null);
        dialogBuilder.setView(dialogView);

        final EditText editTextName = dialogView.findViewById(R.id.editServiceNameUserInput);
        final EditText editTextRole  = dialogView.findViewById(R.id.editServiceProviderUserInput);
        final Button buttonCancel = dialogView.findViewById(R.id.buttonCancelService);
        final Button buttonAdd = dialogView.findViewById(R.id.buttonConfirmService);

        dialogBuilder.setTitle("Create a service");
        final AlertDialog b = dialogBuilder.create();
        b.show();

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                b.dismiss();
            }
        });

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = editTextName.getText().toString().trim();
                String role = editTextRole.getText().toString().trim();
                addService(name, role );
                b.dismiss();
                String[] newService = new String[]{name,role};
                elements.add(newService);
            }
        });


    }

    public void editServices(final String service, final String newName, final String newRole){

        if (!(TextUtils.isEmpty(newName) || TextUtils.isEmpty(newRole) || newName.length() > 40 || newRole.length() >20 )) {

            db.collection("services").whereEqualTo("name", newName.toLowerCase())
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        QuerySnapshot query = task.getResult();
                        if(query.isEmpty()){
                            db.collection("services").whereEqualTo("name", service)
                                    .get()
                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            String id = queryDocumentSnapshots.getDocuments().get(0).getId();
                                            Map<String, Object> service = new HashMap<>();
                                            service.put("name", newName.toLowerCase());
                                            service.put("role", newRole.toLowerCase());
                                            db.collection("services").document("/" + id).update(service);
                                            list.setAdapter(adapter);
                                            Toast.makeText(ManageServices.this, "Service Updated to" + " name: " +
                                                            newName + " role: " + newRole,
                                                    Toast.LENGTH_LONG).show();
                                        }
                                    });
                        }else {
                            Toast.makeText(ManageServices.this, "Service already exists!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

        }else{
            emptyInputs();
        }
    }

    public void deleteServices (final String name) {

        db.collection("services").whereEqualTo("name", name)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        String id = queryDocumentSnapshots.getDocuments().get(0).getId();
                        db.collection("services").document("/" + id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                list.setAdapter(adapter);
                                Toast.makeText(ManageServices.this, "Service " + name + " deleted!",
                                        Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });
    }

    public void onAddServiceClick(View view) {
        showAddDialog();
    }

    public void addService(final String name, final String role){

            if (!(TextUtils.isEmpty(name) || TextUtils.isEmpty(role) || name.length() > 40 || role.length() >20 )) {
                db.collection("services").whereEqualTo("name", name.toLowerCase())
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot query = task.getResult();
                            if(query.isEmpty()){
                                Map<String, Object> service = new HashMap<>();
                                service.put("name", name.toLowerCase());
                                service.put("role", role.toLowerCase());
                                db.collection("services").add(service)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                list.setAdapter(adapter);
                                                Toast.makeText(ManageServices.this, "Service "+ name +" added!", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }else {
                                Toast.makeText(ManageServices.this, "Service already exists!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

            }else{
                emptyInputs();
            }
    }


    private void emptyInputs(){
        Toast.makeText(this, "Inputs are invalid!", Toast.LENGTH_SHORT).show();
    }

    private void setAdapter(ArrayList<String[]> elements) {
        adapter = new CustomAdapter(this, elements);
        list.setAdapter(adapter);
    }

    private class CustomAdapter extends BaseAdapter implements ListAdapter {

        private Context context;
        private ArrayList<String[]> list;

        CustomAdapter(@NonNull Context context, ArrayList<String[]> list) {
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

            String[] service = list.get(position);

            Log.d("BBBB", service[0]);
            TextView nameText = view.findViewById(R.id.name_info);
            nameText.setText(service[0]);

            TextView roleText = view.findViewById(R.id.role_info);
            roleText.setText(service[1]);

            return view;
        }
    }


}
