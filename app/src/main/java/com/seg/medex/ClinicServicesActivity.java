package com.seg.medex;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
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
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ClinicServicesActivity extends AppCompatActivity {

    private String documentID;
    private ListView list;
    private CustomAdapter adapter;
    final ArrayList<String[]> elements = new ArrayList<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    final ArrayList<String> selected = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clinic_services);

        this.list = findViewById(R.id.user_list);

        db.collection("services")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult() != null) {
                        Log.d("This", String.valueOf(task.getResult().getDocuments().size()));
                        for (int i = 0; i < task.getResult().getDocuments().size(); i++) {
                            Log.d("AAAA", task.getResult().getDocuments().get(i).get("name").toString());
                            elements.add(new String[]{task.getResult().getDocuments().get(i).get("name").toString(), task.getResult().getDocuments().get(i).get("role").toString(), task.getResult().getDocuments().get(i).getId()});
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
                ColorDrawable viewColour = (ColorDrawable) view.getBackground();

                if (viewColour == null){
                    view.setBackgroundColor(Color.LTGRAY);
                    addToSelectedServices(elements.get(i)[2]);
                }else if (viewColour.getColor() == Color.TRANSPARENT){
                    view.setBackgroundColor(Color.LTGRAY);
                    addToSelectedServices(elements.get(i)[2]);
                }
                else{
                    view.setBackgroundColor(Color.TRANSPARENT);
                    selected.remove(elements.get(i)[2]);
                }

            }
        });
    }

    public void addToSelectedServices(String id){
        selected.add(id);
    }

    public void onContinueClick(View view){
        SharedPreferences sharedPreferences = getSharedPreferences("ID", 0);
        final String clinic_name = sharedPreferences.getString("clinic_name","");

        db.collection("clinics").whereEqualTo("clinic_name", clinic_name)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot query = task.getResult();
                        db.collection("clinics").whereEqualTo("clinic_name", clinic_name)
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        String id = queryDocumentSnapshots.getDocuments().get(0).getId();
                                        Map<String, Object> services = new HashMap<>();
                                        services.put("services", selected);
                                        db.collection("clinics").document("/" + id).set(services, SetOptions.merge());
                                        Toast.makeText(ClinicServicesActivity.this, "Services added!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                }
            }
        });

    }



    private void emptyInputs(){
        Toast.makeText(this, "Inputs are invalid!", Toast.LENGTH_SHORT).show();
    }

    private void setAdapter(ArrayList<String[]> elements) {
        adapter = new ClinicServicesActivity.CustomAdapter(this, elements);
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

    /*
        Helper methods
     */
    private static boolean isAlpha(String s) {
        char[] alpha = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', ' '};
        s = s.toLowerCase();
        for(int i = 0; i<s.length(); i++) {
            if(!(Utility.includes(alpha, s.charAt(i)))) {
                return false;
            }
        }
        return true;
    }
}
