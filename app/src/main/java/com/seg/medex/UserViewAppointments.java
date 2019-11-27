package com.seg.medex;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserViewAppointments extends AppCompatActivity {
    private ListView list;
    private CustomAdapter adapter;
    final ArrayList<String[]> elements = new ArrayList<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String userUserName;
    private String clinic;
    private String date;
    private String service;




    //Shows Services for now
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_view_appointments);
        //Producing a list
        this.list = findViewById(R.id.user_appointment_list);
        this.userUserName = (String) getIntent().getSerializableExtra("userUsername");

        db = FirebaseFirestore.getInstance();

        //queries all clinics
        db.collection("users").whereEqualTo("account_type",1)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    //for every clinic
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        //gets appointments for specific clinic
                        Map<String, ArrayList<Map<String, String>>> appointments = (Map<String, ArrayList<Map<String, String>>>) document.get("appointments");
                        //for each day of appointmets
                        for(Map.Entry entry : appointments.entrySet()){
                            //retrieve the appoints in the day
                            List apps = (ArrayList<Map<String, String>>) entry.getValue();
                            // for each appointments
                            for(int i = 0; i<apps.size(); i++){
                                Map<String, String> eachApp = (Map<String, String>) apps.get(i);
                                if(eachApp.containsValue(userUserName)){
                                    String firstLine = "Clinic: " + (String)document.get("clinic_name")+", Service: "+eachApp.get("service");
                                    String secondLine = "Date: " + entry.getKey()+", Time: "+ eachApp.get("time");
                                    elements.add(new String[]{firstLine, secondLine, (String)document.get("clinic_name"),eachApp.get("service"), (String) entry.getKey(), eachApp.get("time"), (String) document.get("street_address")});
                                    setAdapter(elements);
                                }
                            }

                        }
                    }
                } else {
                    Log.d("", "Error getting documents: ", task.getException());
                }
            }
        });

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                goToUserApp(elements.get(i)[2],elements.get(i)[3],elements.get(i)[4],elements.get(i)[5], elements.get(i)[6]);
            }
        });
    }

    private void goToUserApp(String clinicusername, String service, String date, String time, String add){
        Intent intent = new Intent(this,UserAppointment.class);
        intent.putExtra("clinic_username",clinicusername);
        intent.putExtra("service",service);
        intent.putExtra("date",date);
        intent.putExtra("time",time);
        intent.putExtra("addy", add);
        startActivity(intent);
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
                view = inflater.inflate(R.layout.appointment_item, null);
            }

            String[] service = list.get(position);

            Log.d("BBBB", service[0]);
            TextView nameText = view.findViewById(R.id.clinic_info);
            nameText.setText(service[0]);

            TextView roleText = view.findViewById(R.id.time_info);
            roleText.setText(service[1]);

            return view;
        }
    }
}
