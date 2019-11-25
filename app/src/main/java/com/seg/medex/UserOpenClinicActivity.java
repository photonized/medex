package com.seg.medex;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

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

    private List<String> startTime;
    private List<String> endTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_open_clinic);

        this.clinicUserName = (String) getIntent().getSerializableExtra("clinic_username");
        this.db = FirebaseFirestore.getInstance();

        this.clinicName = findViewById(R.id.clinic_name);
        this.streetAddress = findViewById(R.id.street_address);
        this.phoneNo = findViewById(R.id.payment_method);
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
            }


        });


    }
}
