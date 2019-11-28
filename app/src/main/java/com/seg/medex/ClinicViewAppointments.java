package com.seg.medex;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ClinicViewAppointments extends AppCompatActivity {
    private String firstName;
    private ListView list;
    private ClinicViewAppointments.CustomAdapter adapter;
    final ArrayList<String[]> elements = new ArrayList<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    //Shows Services for now
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clinic_view_appointments);
        //Producing a list
        this.list = findViewById(R.id.patient_appointment_list);

        db = FirebaseFirestore.getInstance();

        SharedPreferences preferences = getSharedPreferences("ID", 0);
        this.firstName = preferences.getString("first_name","");

    }

    public void onResume(){
        super.onResume();
        elements.clear();
        setAdapter(elements);
        list.setAdapter(adapter);
        populateList();
    }

    public void editAppointments(String patient, String time, String date, String service){

        Intent intent = new Intent(this, ClinicEditAppoinments.class);
        intent.putExtra("clinic_first_name",firstName);
        intent.putExtra("time",time);
        intent.putExtra("patient", patient);
        intent.putExtra("date", date);
        intent.putExtra("service", service);
        startActivity(intent);

    }
    private void setAdapter(ArrayList<String[]> elements) {
        adapter = new ClinicViewAppointments.CustomAdapter(this, elements);
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
                view = inflater.inflate(R.layout.appointment_clinic_item, null);
            }

            String[] service = list.get(position);

            Log.d("BBBB", service[0]);
            TextView nameText = view.findViewById(R.id.patient_info);
            nameText.setText("Patient: " + service[0]);

            TextView roleText = view.findViewById(R.id.time_info);
            roleText.setText("Date: " + service[2] + " Time: " + service[1]);

            TextView serviceText = view.findViewById(R.id.service_info);
            serviceText.setText("Service: " + service[3]);

            return view;
        }
    }

    public void populateList(){
        db.collection("users").whereEqualTo("first_name", firstName)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot query) {
                DocumentSnapshot doc = query.getDocuments().get(0);
                //gets appointments for specific clinic
                Map<String, ArrayList<Map<String, String>>> appointments = (Map<String, ArrayList<Map<String, String>>>) doc.get("appointments");
                //for each day of appointmets
                for(Map.Entry entry : appointments.entrySet()){
                    //retrieve the appoints in the day
                    List apps = (ArrayList<Map<String, String>>) entry.getValue();
                    // for each appointments
                    for(int i = 0; i<apps.size(); i++){
                        Map<String, String> eachApp = (Map<String, String>) apps.get(i);
                        String patient = eachApp.get("username");
                        String service = eachApp.get("service");
                        String time = eachApp.get("time");
                        String date = (String) entry.getKey();
                        elements.add(new String[]{patient, time, date, service});
                        setAdapter(elements);
                    }

                }

            }});


        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String patient = elements.get(i)[0];
                String time = elements.get(i)[1];
                String date = elements.get(i)[2];
                String service = elements.get(i)[3];
                editAppointments(patient,time, date,service);
            }
        });
    }
}
