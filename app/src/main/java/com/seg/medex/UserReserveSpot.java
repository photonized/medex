package com.seg.medex;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserReserveSpot extends AppCompatActivity {

    private CalendarView calendar;
    private ArrayList<String> servicesList;
    private TextView showSelectedDay;
    private Spinner selectedTime;
    private String clinicUserName;
    private ArrayList<String> openTimes;
    private ArrayList<String> closingTimes;
    private Spinner selectedService;
    private Button continueButton;
    private ArrayList<String> services = new ArrayList<>();
    FirebaseFirestore db;
    Map<String, ArrayList<Map<String, String>>> appointments;
    String dateString;
    private ArrayList<String> availableTimes = new ArrayList<>();
    private String currentParsedTime;
    private String clinicId;
    private SharedPreferences preferences;
    private boolean hasApt = false;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_reserve_spot);

        this.calendar = findViewById(R.id.calendarView);
        calendar.setMinDate(System.currentTimeMillis() - 1000);
        this.selectedService = findViewById(R.id.service_spinner);
        this.selectedTime = findViewById(R.id.time_spinner);
        this.clinicUserName = (String) getIntent().getSerializableExtra("clinic_username");
        this.db = FirebaseFirestore.getInstance();
        this.continueButton = findViewById(R.id.continue_button);
        final ArrayList<String> servicesList;
        this.preferences = getSharedPreferences("ID", 0);
        dateString = new SimpleDateFormat("yyyy/MM/dd").format(new Date(calendar.getDate()));

        db.collection("users").whereEqualTo("username", clinicUserName).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot querySnapshot){
                    final ArrayList<String> arrList;
                    arrList = new ArrayList<>();
                    final ArrayList ids = (ArrayList) querySnapshot.getDocuments().get(0).get("services");
                    appointments = (Map<String, ArrayList<Map<String, String>>>)querySnapshot.getDocuments().get(0).get("appointments");
                    Log.d("APPOINTMENTS", appointments.toString());
                    openTimes = (ArrayList)querySnapshot.getDocuments().get(0).get("start_times");
                    closingTimes = (ArrayList)querySnapshot.getDocuments().get(0).get("end_times");
                    clinicId = querySnapshot.getDocuments().get(0).getId();
                    ArrayList list = new ArrayList<>();
                    for(ArrayList<Map<String, String>> date : appointments.values()) {
                        for(Map<String, String> appointment : date) {
                            if(appointment.get("username").equals(preferences.getString("username", ""))) {
                                hasApt = true;
                            }
                        }
                    }
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
                                try {
                                    populateServicesSpinner(arrList);
                                    services.addAll(arrList);
                                } catch (ParseException e) {
                                    e.printStackTrace();
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
        });



        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                dateString = Utility.convertDayToString(year, month+1, dayOfMonth);
                db.collection("users").whereEqualTo("username", clinicUserName)
                        .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot query) {
                        // 'doc' here is the DocumentSnapshot of the clinic
                        DocumentSnapshot doc = query.getDocuments().get(0);
                        try {
                            populateServicesSpinner(services);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        //Check validity
                        //Check if day selected is open
                        //Check what time slots are available and display it to the spinner
                        //Take care of it Nader, if u need help hmu (Omer)
                    }


                });
            }
        });

}

    private void populateServicesSpinner(ArrayList<String> list) throws ParseException {

        this.selectedTime = findViewById(R.id.time_spinner);

        String currentTime = Calendar.getInstance().get(Calendar.HOUR_OF_DAY) + ":" + Calendar.getInstance().get(Calendar.MINUTE);
        Log.d("SWAG: ", currentTime);

        currentParsedTime = Utility.convertTimeToFormat(currentTime);

        availableTimes.add("00:00");
        availableTimes.add("00:15");
        availableTimes.add("00:30");
        availableTimes.add("00:45");
        availableTimes.add("01:00");
        availableTimes.add("01:15");
        availableTimes.add("01:30");
        availableTimes.add("01:45");
        availableTimes.add("02:00");
        availableTimes.add("02:15");
        availableTimes.add("02:30");
        availableTimes.add("02:45");
        availableTimes.add("03:00");
        availableTimes.add("03:15");
        availableTimes.add("03:30");
        availableTimes.add("03:45");
        availableTimes.add("04:00");
        availableTimes.add("04:15");
        availableTimes.add("04:30");
        availableTimes.add("04:45");
        availableTimes.add("05:00");
        availableTimes.add("05:15");
        availableTimes.add("05:30");
        availableTimes.add("05:45");
        availableTimes.add("06:00");
        availableTimes.add("06:15");
        availableTimes.add("06:30");
        availableTimes.add("06:45");
        availableTimes.add("07:00");
        availableTimes.add("07:15");
        availableTimes.add("07:30");
        availableTimes.add("07:45");
        availableTimes.add("08:00");
        availableTimes.add("08:15");
        availableTimes.add("08:30");
        availableTimes.add("08:45");
        availableTimes.add("09:00");
        availableTimes.add("09:15");
        availableTimes.add("09:30");
        availableTimes.add("09:45");
        availableTimes.add("10:00");
        availableTimes.add("10:15");
        availableTimes.add("10:30");
        availableTimes.add("10:45");
        availableTimes.add("11:00");
        availableTimes.add("11:15");
        availableTimes.add("11:30");
        availableTimes.add("11:45");
        availableTimes.add("12:00");
        availableTimes.add("12:15");
        availableTimes.add("12:30");
        availableTimes.add("12:45");
        availableTimes.add("13:00");
        availableTimes.add("13:15");
        availableTimes.add("13:30");
        availableTimes.add("13:45");
        availableTimes.add("14:00");
        availableTimes.add("14:15");
        availableTimes.add("14:30");
        availableTimes.add("14:45");
        availableTimes.add("15:00");
        availableTimes.add("15:15");
        availableTimes.add("15:30");
        availableTimes.add("15:45");
        availableTimes.add("16:00");
        availableTimes.add("16:15");
        availableTimes.add("16:30");
        availableTimes.add("16:45");
        availableTimes.add("17:00");
        availableTimes.add("17:15");
        availableTimes.add("17:30");
        availableTimes.add("17:45");
        availableTimes.add("18:00");
        availableTimes.add("18:15");
        availableTimes.add("18:30");
        availableTimes.add("18:45");
        availableTimes.add("19:00");
        availableTimes.add("19:15");
        availableTimes.add("19:30");
        availableTimes.add("19:45");
        availableTimes.add("21:00");
        availableTimes.add("21:15");
        availableTimes.add("21:30");
        availableTimes.add("21:45");
        availableTimes.add("22:00");
        availableTimes.add("22:15");
        availableTimes.add("22:30");
        availableTimes.add("22:45");
        availableTimes.add("23:00");
        availableTimes.add("23:15");
        availableTimes.add("23:30");
        availableTimes.add("23:45");

        if(!(openTimes.get(Utility.convertDaytoWeekday(dateString)).equals(" - ") || openTimes.get(Utility.convertDaytoWeekday(dateString)).equals("-"))) {
            int index = availableTimes.indexOf(openTimes.get(Utility.convertDaytoWeekday(dateString)));
            for (int i = 0; i < index; i++) {
                availableTimes.remove(0);
            }
        }
        if(!(closingTimes.get(Utility.convertDaytoWeekday(dateString)).equals(" - ") || closingTimes.get(Utility.convertDaytoWeekday(dateString)).equals("-"))) {
            int index2 = availableTimes.indexOf(closingTimes.get(Utility.convertDaytoWeekday(dateString)));
            for (int i = availableTimes.size() - 1; i >= index2; i--) {
                availableTimes.remove(i);
            }
        } else {
            availableTimes.clear();
            availableTimes.add("CLOSED");
        }

        if(!availableTimes.get(0).equals("CLOSED") && dateString.equals(new SimpleDateFormat("yyyy/MM/dd").format(new Date(calendar.getDate()) ))) {
            int index3 = availableTimes.indexOf(currentParsedTime);
            for (int i = 0; i < index3; i++) {
                availableTimes.remove(0);
            }
        }

        if(!availableTimes.get(0).equals("CLOSED")) {

            for(Map.Entry entry : appointments.entrySet()) {
                if(entry.getKey().equals(dateString)) {
                    List apps = (ArrayList<Map<String,String>>) entry.getValue();
                    for (int i = 0; i < apps.size(); i++){
                        Map<String,String> eachApp = (Map<String,String>) apps.get(i);
                               if (availableTimes.contains(eachApp.get("time"))) {
                                   availableTimes.remove(availableTimes.indexOf(eachApp.get("time")));
                               }

                    }
//                    for (Map<String, String> time : entry) {
//                        if (availableTimes.contains(time.get("time"))) {
//                            availableTimes.remove(availableTimes.indexOf(time.get("time")));
//                        }
//                    }
                }
            }
        }
        System.currentTimeMillis();

        Log.d("ARRAYLIST: ", availableTimes.toString());
        Log.d("DATENUM: ", String.valueOf(Utility.convertDaytoWeekday(dateString)));




        Log.d("ARRAYLIST: ", availableTimes.toString());


        Log.d("START TIME: ", openTimes.get(Utility.convertDaytoWeekday(dateString)));
        Log.d("END TIME: ", closingTimes.get(Utility.convertDaytoWeekday(dateString)));


        ArrayAdapter<String> timeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, (String[])availableTimes.toArray(new String[availableTimes.size()]));
        timeAdapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
        selectedTime.setAdapter(timeAdapter);


        this.selectedService = findViewById(R.id.service_spinner);
        String[] arr = list.toArray(new String[list.size()]);
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_dropdown_item, arr);
        spinnerArrayAdapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );

        selectedService.setAdapter(spinnerArrayAdapter);

    }

    public void onContinueClick(View view) {
        if(hasApt) {
            Toast.makeText(this, "You already have an appointment booked at this clinic.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        Map<String, String> appointment = new HashMap<>();
        if(selectedTime.getSelectedItem().equals("CLOSED")) {
            Toast.makeText(this, "You can't book for a closed time!", Toast.LENGTH_SHORT).show();
            return;
        }
        appointment.put("time", (String)selectedTime.getSelectedItem());
        appointment.put("username", getSharedPreferences("ID", 0).getString("username", ""));
        appointment.put("service", (String)selectedService.getSelectedItem());
        if(appointments.containsKey(dateString)) {
            appointments.get(dateString).add(appointment);
        } else {
            ArrayList listToAdd = new ArrayList();
            listToAdd.add(appointment);
            appointments.put(dateString, listToAdd);
        }
        Map toAdd = new HashMap();
        toAdd.put("appointments", appointments);
        db.collection("users").document("/" + clinicId).set(toAdd, SetOptions.merge());
        finish();
    }
}
