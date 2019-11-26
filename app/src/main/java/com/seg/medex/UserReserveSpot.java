package com.seg.medex;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class UserReserveSpot extends AppCompatActivity {

    private CalendarView calender;
    private TextView showSelectedDay;
    private Spinner selectedTime;
    private String clinicUserName;
    FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_reserve_spot);

        this.calender = findViewById(R.id.calendarView);
        this.showSelectedDay = findViewById(R.id.selectedDayText);
        this.selectedTime = findViewById(R.id.time_spinner);
        this.clinicUserName = (String) getIntent().getSerializableExtra("clinic_username");
        this.db = FirebaseFirestore.getInstance();

        calender.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
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
        });
    }
}
