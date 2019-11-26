package com.seg.medex;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class UserReserveSpot extends AppCompatActivity {

    private CalendarView calender;
    private TextView showSelectedDay;
    private Spinner selectedTime;
    private String clinicUserName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_reserve_spot);

        this.calender = findViewById(R.id.calendarView);
        this.showSelectedDay = findViewById(R.id.selectedDayText);
        this.selectedTime = findViewById(R.id.time_spinner);
        this.clinicUserName = (String) getIntent().getSerializableExtra("clinic_username");

    }
}
