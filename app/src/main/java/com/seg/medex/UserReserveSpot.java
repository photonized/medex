package com.seg.medex;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class UserReserveSpot extends AppCompatActivity {

    private CalendarView calender;
    private ArrayList<String> servicesList;
    private TextView showSelectedDay;
    private Spinner selectedTime;
    private String clinicUserName;
    private Spinner selectedService;
    FirebaseFirestore db;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_reserve_spot);

        this.calender = findViewById(R.id.calendarView);
        this.selectedService = findViewById(R.id.service_spinner);
        this.selectedTime = findViewById(R.id.time_spinner);
        this.clinicUserName = (String) getIntent().getSerializableExtra("clinic_username");
        this.db = FirebaseFirestore.getInstance();
        ArrayList<String> servicesList;

        db.collection("users").whereEqualTo("username", clinicUserName).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot querySnapshot){
                    final ArrayList<String> arrList;
                    arrList = new ArrayList<>();
                    final ArrayList ids = (ArrayList) querySnapshot.getDocuments().get(0).get("services");
                    db.collection("services")
                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                if (task.getResult() != null) {
                                    Log.d("This", String.valueOf(task.getResult().getDocuments().size()));
                                    for (int i = 0; i < task.getResult().getDocuments().size(); i++) {
                                        if(ids.contains(task.getResult().getDocuments().get(i).getId())) {
                                            arrList.add((String)task.getResult().getDocuments().get(i).get("name"));
                                        }
                                    }
                                }
                                Log.d("LOLOL", arrList.toString());
                                populateServicesSpinner(arrList);
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
        });



        /*calender.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                db.collection("users").whereEqualTo("username", clinicUserName)
                        .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot query) {
                        // 'doc' here is the DocumentSnapshot of the clinic
                        DocumentSnapshot doc = query.getDocuments().get(0);
                        //Check validity
                        //Check if day selected is open
                        //Check what time slots are available and display it to the spinner
                        //Take care of it Nader, if u need help hmu (Omer)
                    }


                });
            }
        });*/

}

    private void populateServicesSpinner(ArrayList<String> list) {
        this.selectedService = findViewById(R.id.service_spinner);
        String[] arr = list.toArray(new String[list.size()]);
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, arr);
        spinnerArrayAdapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );

        selectedService.setAdapter(spinnerArrayAdapter);

    }
}
