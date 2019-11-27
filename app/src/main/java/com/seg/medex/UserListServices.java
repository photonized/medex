package com.seg.medex;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UserListServices extends AppCompatActivity {
    private ListView list;
    private UserListServices.CustomAdapter adapter;
    final ArrayList<String[]> elements = new ArrayList<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String clinicUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list_services);
        this.clinicUserName = (String) getIntent().getSerializableExtra("clinic_username");
        this.list = findViewById(R.id.view_services_list);


        db.collection("users").whereEqualTo("username", clinicUserName)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot query) {
                DocumentSnapshot doc = query.getDocuments().get(0);
                List<String> services = (ArrayList<String>) doc.get("services");
                for(int i = 0; i<services.size(); i++){
                    db.collection("services").document(services.get(i))
                            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                elements.add(new String[]{document.get("name").toString(), document.get("role").toString()});
                                setAdapter(elements);
                            } else {
                                Log.d("", "get failed with ", task.getException());
                            }
                        }
                    });
                }


        }});

        db.collection("users").whereEqualTo("clinic_name", clinicUserName)
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


    }



    private void setAdapter(ArrayList<String[]> elements) {
        adapter = new UserListServices.CustomAdapter(this, elements);
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
    public static boolean isAlpha(String s) {
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
