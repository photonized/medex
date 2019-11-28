package com.seg.medex;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class UserOpenClinicActivity extends AppCompatActivity {

    private String clinicUserName;
    FirebaseFirestore db;
    private TextView clinicName;
    private TextView streetAddress;
    private TextView phoneNo;
    private TextView paymentMethod;
    private TextView insuranceType;
    private TextView mondayStart;
    private TextView mondayEnd;
    private TextView tuesdayStart;
    private TextView tuesdayEnd;
    private TextView wednesdayStart;
    private TextView wednesdayEnd;
    private TextView thursdayStart;
    private TextView thursdayEnd;
    private TextView fridayStart;
    private TextView fridayEnd;
    private TextView saturdayStart;
    private TextView saturdayEnd;
    private TextView sundayStart;
    private TextView sundayEnd;

    private Map<String, ArrayList<Map<String, String>>> appointments;

    private TextView waitingTimes;


    private List<String> startTime;
    private List<String> endTime;

    private TextView displayRating;
    private ArrayList<String> closingTimes;
    private ArrayList<String> openingTimes;


    private Map<String, ArrayList<Map<String, String>>> appointments;

    private ArrayList<HashMap<String,Object>> ratings;
    private ArrayList<Long> numericalRatings;
    private ArrayList<String> usersRatings;
    private ArrayList<String[]> elements = new ArrayList<>();
    private boolean hasApt;
    private SharedPreferences preferences;
    private Button book;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_open_clinic);

        this.clinicUserName = (String) getIntent().getSerializableExtra("clinic_username");
        this.db = FirebaseFirestore.getInstance();

        this.clinicName = findViewById(R.id.clinic_name);
        this.streetAddress = findViewById(R.id.street_address);
        this.phoneNo = findViewById(R.id.phone_number);
        this.paymentMethod = findViewById(R.id.payment_method);
        this.insuranceType = findViewById(R.id.insurance_type);

        this.mondayStart = findViewById(R.id.mondayStartTime);
        this.mondayEnd = findViewById(R.id.mondayEndTime);

        this.tuesdayStart = findViewById(R.id.tuesdayStartTime);
        this.tuesdayEnd = findViewById(R.id.tuesdayEndTime);

        this.wednesdayStart = findViewById(R.id.wednesdayStartTime);
        this.wednesdayEnd = findViewById(R.id.wednesdayEndTime);

        this.thursdayStart = findViewById(R.id.thursdayStartTime);
        this.thursdayEnd = findViewById(R.id.thursdayEndTime);

        this.fridayStart = findViewById(R.id.fridayStartTime);
        this.fridayEnd = findViewById(R.id.fridayEndTime);

        this.saturdayStart = findViewById(R.id.saturdayStartTime);
        this.saturdayEnd = findViewById(R.id.saturdayEndTime);

        this.sundayStart = findViewById(R.id.sundayStartTime);
        this.sundayEnd = findViewById(R.id.sundayEndTime);

        this.waitingTimes = findViewById(R.id.wait_time);

        this.displayRating = findViewById(R.id.rating_text);


        this.book = findViewById(R.id.reserve_button);

        this.preferences = getSharedPreferences("ID", 0);




    }

    @Override
    protected void onResume() {
        super.onResume();


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
                            } else {
                                Log.d("", "get failed with ", task.getException());
                            }
                        }
                    });
                }
                appointments = (Map<String, ArrayList<Map<String, String>>>) query.getDocuments().get(0).get("appointments");
                for(ArrayList<Map<String, String>> date : appointments.values()) {
                    for(Map<String, String> appointment : date) {
                        if(appointment.get("username").equals(preferences.getString("username", ""))) {
                            hasApt = true;
                            book.setText("CANCEL APPOINTMENT");
                        }
                    }
                }


            }});

        db.collection("users").whereEqualTo("username", clinicUserName)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot query) {
                DocumentSnapshot doc = query.getDocuments().get(0);

                startTime = (ArrayList) doc.get("start_times");
                endTime = (ArrayList) doc.get("end_times");

                clinicName.setText((String)doc.get("clinic_name"));
                streetAddress.setText((String)doc.get("street_address"));
                phoneNo.setText((String)doc.get("phone_number"));
                paymentMethod.setText((String)doc.get("payment_method"));
                insuranceType.setText((String)doc.get("insurance_types"));
                closingTimes = (ArrayList<String>) doc.get("end_times");
                openingTimes = (ArrayList<String>) doc.get("start_times");
                appointments = ( Map<String, ArrayList<Map<String, String>>>)doc.get("appointments");


                mondayStart.setText(startTime.get(0));
                mondayEnd.setText(endTime.get(0));

                tuesdayStart.setText(startTime.get(1));
                tuesdayEnd.setText(endTime.get(1));

                wednesdayStart.setText(startTime.get(2));
                wednesdayEnd.setText(endTime.get(2));

                thursdayStart.setText(startTime.get(3));
                thursdayEnd.setText(endTime.get(3));

                fridayStart.setText(startTime.get(4));
                fridayEnd.setText(endTime.get(4));

                saturdayStart.setText(startTime.get(5));
                saturdayEnd.setText(endTime.get(5));

                sundayStart.setText(startTime.get(6));
                sundayEnd.setText(endTime.get(6));

                calculateRating();
                try {
                    setWaitTime();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }


        });
    }

    public void onAddRatingClick (View view) {
        SharedPreferences sharedPreferences = getSharedPreferences("ID", 0);
        String clientUserName = sharedPreferences.getString("username", " ");
        if (ratings != null ){
            if (usersRatings.contains(clientUserName)){
                findViewById(R.id.rate_clinic_button).setEnabled(false);
                Toast.makeText(this, "Clinic already rated!", Toast.LENGTH_SHORT).show();
            }else{
                showRatingDialog();
            }
        }else{
            showRatingDialog();
        }

    }

    private void showRatingDialog() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this, R.style.DialogTheme);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.add_rating_popup, null);
        dialogBuilder.setView(dialogView);

        final EditText editTextComment = dialogView.findViewById(R.id.addCommentInput);
        final RatingBar ratingBar = dialogView.findViewById(R.id.ratingBar);
        final Button buttonCancel = dialogView.findViewById(R.id.buttonCancelRating);
        final Button buttonAdd = dialogView.findViewById(R.id.buttonConfirmRating);

        dialogBuilder.setTitle("Add a rating");
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
                String comment = editTextComment.getText().toString().trim();
                addRating(comment, ratingBar.getRating() );
                b.dismiss();
            }
        });
    }
    public void addRating(final String comment, final float numStars){


        if (!(TextUtils.isEmpty(comment) || comment.length() > 140) && ManageServices.isAlpha(comment) && numStars != 0) {

            final long rating = (long)numStars;
            Log.d("STARS", String.valueOf(numStars));
            if ( rating >= 1.0 && rating <= 5.0){
                //Add to array of comments and sum of ratings
                db.collection("users").whereEqualTo("username", clinicUserName)
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            SharedPreferences sharedPreferences = getSharedPreferences("ID", 0);
                            QuerySnapshot query = task.getResult();
                            DocumentSnapshot doc = query.getDocuments().get(0);
                            String id = query.getDocuments().get(0).getId();
                            HashMap<String, Object> updateRating = new HashMap<>();
                            //if total ratings is null that means there are no reviews and the fields are not there yet
                            updateRating.put("rating",  rating);
                            updateRating.put("comment", comment);
                            updateRating.put("username", sharedPreferences.getString("username", ""));

                            Map<String,Object> toFirebaseRatings = new HashMap<>();
                            if (ratings == null){
                                ArrayList<HashMap<String,Object>> ratingsArray = (ArrayList)doc.get("ratings");
                                ArrayList<HashMap<String,Object>> toRemove = new ArrayList<>();
                                for(HashMap<String, Object> map : ratingsArray) {
                                    if(map.get("username").equals(sharedPreferences.getString("username", ""))) {
                                        toRemove.add(map);
                                    }
                                }
                                ratingsArray.removeAll(toRemove);
                                ratingsArray.add(updateRating);
                                toFirebaseRatings.put("ratings",ratingsArray);
                            }else{
                                ratings.add(updateRating);
                                toFirebaseRatings.put("ratings",ratings);
                            }
                            calculateRating();
                            db.collection("users").document("/" + id).set(toFirebaseRatings, SetOptions.merge());
                        }
                    }
                });

            }else{
                Toast.makeText(this, "Rating not  are invalid!", Toast.LENGTH_SHORT).show();
            }

        }else{
            emptyInputs();
        }
    }

    private void emptyInputs(){
        Toast.makeText(this, "Inputs are invalid!", Toast.LENGTH_SHORT).show();
    }

    private void setWaitTime() throws ParseException {

        ArrayList<String> availableTimes = new ArrayList();


        String currentTime = Calendar.getInstance().get(Calendar.HOUR_OF_DAY) + ":" + Calendar.getInstance().get(Calendar.MINUTE);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        LocalDateTime now = LocalDateTime.now();
        String currentParsedDate = dtf.format(now);

        String currentParsedTime = Utility.convertTimeToFormat(currentTime);

        ArrayList<Map<String, String>> todayAppointments = appointments.get(currentParsedDate);
        ArrayList<String> takenTimes = new ArrayList<>();


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

        if((closingTimes.get(Utility.convertDaytoWeekday(currentParsedDate)).equals(" - ") || closingTimes.get(Utility.convertDaytoWeekday(currentParsedDate)).equals("-"))) {
            waitingTimes.setText("CLOSED");
            return;
        }

        int time = availableTimes.indexOf(currentParsedTime);
        int time2 = availableTimes.indexOf(closingTimes.get(Utility.convertDaytoWeekday(currentParsedDate)));
        int time3 = availableTimes.indexOf(openingTimes.get(Utility.convertDaytoWeekday(currentParsedDate)));
        if(availableTimes.indexOf(currentParsedTime) > availableTimes.indexOf(closingTimes.get(Utility.convertDaytoWeekday(currentParsedDate))) ||
                availableTimes.indexOf(currentParsedTime) < availableTimes.indexOf(openingTimes.get(Utility.convertDaytoWeekday(currentParsedDate)))) {
            waitingTimes.setText("CLOSED");
            return;
        }

        if(todayAppointments == null && !waitingTimes.getText().equals("CLOSED")) {
            waitingTimes.setText("0 minutes");
            return;
        }
        for(Map<String, String> appointment : todayAppointments) {
            takenTimes.add(appointment.get("time"));
        }

        Collections.sort(takenTimes);



        if(takenTimes.contains(currentParsedTime) && !waitingTimes.getText().equals("CLOSED")) {
            int counter = 0;
            ArrayList<String> compareTimes = new ArrayList<>();
            for (int i = availableTimes.indexOf(currentParsedTime); i < availableTimes.size(); i++) {
                compareTimes.add(availableTimes.get(i));
            }

            Iterator takenIterator = takenTimes.iterator();
            Iterator compareIterator = compareTimes.iterator();

            while(takenIterator.hasNext() && compareIterator.hasNext()) {
                if(takenIterator.next().equals(compareIterator.next())) {
                    counter++;
                }
            }
            waitingTimes.setText((counter*15) + " minutes");
        }

        if(!takenTimes.contains(currentParsedTime) && !waitingTimes.getText().equals("CLOSED")) {
            waitingTimes.setText("0" + " minutes");
        }
        }

    public void onBookClick(View view) {
        if(hasApt){
            db = FirebaseFirestore.getInstance();
            db.collection("users").whereEqualTo("clinic_name", getIntent().getSerializableExtra("clinic_username"))
                    .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot query) {
                    DocumentSnapshot doc = query.getDocuments().get(0);
                    String id = query.getDocuments().get(0).getId();
                    //gets appointments for specific clinic
                    Map<String, ArrayList<Map<String, String>>> appointments = (Map<String, ArrayList<Map<String, String>>>) doc.get("appointments");
                    //for each day of appointmets
                    for(Map.Entry entry : appointments.entrySet()){
                        //retrieve the appoints in the day
                        List apps = (ArrayList<Map<String, String>>) entry.getValue();
                        // for each appointments
                        for(int i = 0; i<apps.size(); i++){
                            Map<String, String> eachApp = (Map<String, String>) apps.get(i);
                            if (eachApp.get("username").equals(preferences.getString("username",""))){
                                apps.remove(i);
                                appointments.put((String)entry.getKey(),(ArrayList<Map<String, String>>) apps);
                                Map<String, Map<String, ArrayList<Map<String, String>>>> service = new HashMap<>();
                                service.put("appointments", appointments);
                                db.collection("users").document("/" + id).set(service, SetOptions.merge());
                                makeHasAptFalse();
                                book.setText("BOOK APPOINTMENT");
                                Toast.makeText(UserOpenClinicActivity.this, "Canceled appointment", Toast.LENGTH_SHORT).show();
                                return;
                            }

                        }

                    }

                }});


        } else {
            Intent intent = new Intent(this, UserReserveSpot.class);
            Log.d("WAAAAAAAAAAa ", clinicUserName);
            intent.putExtra("clinic_username", clinicUserName);
            startActivity(intent);
        }

    }


    public void showDialog(View view){

        final Dialog dialog = new Dialog(UserOpenClinicActivity.this);
        // dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.service_dialog);

        final Button buttonBack = dialog.findViewById(R.id.back_button);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        ListView listView = dialog.findViewById(R.id.services_list);
        CustomAdapter arrayAdapter = new CustomAdapter(this,elements);
        listView.setAdapter(arrayAdapter);

        dialog.show();

    }

    public void onViewServicesClick(View view) {
        showDialog(view);
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

    public void calculateRating() {
        db.collection("users").whereEqualTo("username", clinicUserName)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot query) {
                DocumentSnapshot doc = query.getDocuments().get(0);

                ArrayList<Long> ratingList = new ArrayList<>();
                for (HashMap<String, Object> map : (ArrayList<HashMap>) doc.get("ratings")) {
                    ratingList.add((Long) map.get("rating"));
                }
                Double rating = 0.0;
                for (int i = 0; i < ratingList.size(); i++) {
                    rating += ratingList.get(i);
                }
                if (ratingList.size() == 0) {
                    displayRating.setText(" - ");
                } else {
                    displayRating.setText(" " + String.valueOf(rating / ratingList.size()).substring(0, 3));
                }


            }


        });

    }

    private void makeHasAptFalse() {
        hasApt = false;
    }

}
