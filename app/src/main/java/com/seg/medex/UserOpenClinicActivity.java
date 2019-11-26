package com.seg.medex;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
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

    private List<String> startTime;
    private List<String> endTime;

    private TextView displayRating;
    private Double clinicRatingSum;
    private Integer clinicNumberOfRatings;
    private List<String> clinicComments;

    private Double dbUserRating;
    private String dbUserComment;

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
        //testing new branch skrr
<<<<<<< HEAD

=======
        
>>>>>>> schedule-apps
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

        this.displayRating = findViewById(R.id.rating_text);

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


                clinicNumberOfRatings = (Integer) doc.get("total_ratings");
                if (clinicNumberOfRatings != null){
                    clinicRatingSum = (Double) doc.get("ratings");
                    clinicComments = (ArrayList) doc.get("comments");
                    String averageClinicRating = Double.toString(clinicRatingSum/clinicNumberOfRatings);
                    displayRating.setText(averageClinicRating);
                }

            }


        });


    }
    public void onAddRatingClick (View view) {
        showRatingDialog();
    }

    private void showRatingDialog() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this, R.style.DialogTheme);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.add_rating_popup, null);
        dialogBuilder.setView(dialogView);

        final EditText editTextComment = dialogView.findViewById(R.id.addCommentInput);
        final EditText editTextRating  = dialogView.findViewById(R.id.addRating);
        final Button buttonCancel = dialogView.findViewById(R.id.buttonCancelRating);
        final Button buttonAdd = dialogView.findViewById(R.id.buttonConfirmRating);

        dialogBuilder.setTitle("Add a rating");
        final AlertDialog b = dialogBuilder.create();
        b.show();

        //Load rating and comment if already there for user
        SharedPreferences sharedPreferences = getSharedPreferences("ID", 0);
        db.collection("users").whereEqualTo("username", sharedPreferences.getString("username", " "))
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot query) {
                DocumentSnapshot doc = query.getDocuments().get(0);
                if (doc.get("comment") != null){
                    dbUserComment = (String)doc.get("comment");
                    dbUserRating = (Double) doc.get("rating");
                    editTextComment.setText(dbUserComment);
                    editTextRating.setText(Double.toString(dbUserRating));
                }
            }

        });

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
                String rawRating = editTextRating.getText().toString().trim();

                addRating(comment, rawRating );
                b.dismiss();

            }
        });
    }
    public void addRating(final String comment, final String rawRating){
        SharedPreferences sharedPreferences = getSharedPreferences("ID", 0);

        if (!(TextUtils.isEmpty(comment) || TextUtils.isEmpty(rawRating) || comment.length() > 140 || rawRating.length() >3 ) && ManageServices.isAlpha(comment) && Utility.isNumeric(rawRating)) {

            final double rating = Double.parseDouble(rawRating);
            //Add to array of comments and sum of ratings
            db.collection("users").whereEqualTo("username", clinicUserName)
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        QuerySnapshot query = task.getResult();
                        DocumentSnapshot doc = query.getDocuments().get(0);
                        String id = query.getDocuments().get(0).getId();
                        Map<String, Object> updateRating = new HashMap<>();
                        if (doc.get("total_ratings") == null){
                            updateRating.put("ratings", clinicRatingSum + rating);
                            updateRating.put("total_ratings", clinicNumberOfRatings++);
                        }else{
                            updateRating.put("ratings", clinicRatingSum - dbUserRating + rating);
                            clinicComments.remove(dbUserComment);

                        }
                        updateRating.put("comments", clinicComments.add(comment));
                        db.collection("users").document("/" + id).set(updateRating, SetOptions.merge());
                    }
                }
            });

            //Edit rating and comment for individual user
            db.collection("users").whereEqualTo("username", sharedPreferences.getString("username", ""))
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        QuerySnapshot query = task.getResult();
                        String id = query.getDocuments().get(0).getId();
                        Map<String, Object> updateRating = new HashMap<>();
                        updateRating.put("comment", comment);
                        updateRating.put("rating", rating);
                        db.collection("users").document("/" + id).set(updateRating, SetOptions.merge());
                        Toast.makeText(UserOpenClinicActivity.this, "Clinic rating added!!", Toast.LENGTH_SHORT).show();
                    }
                }
            });



        }else{
            emptyInputs();
        }
    }

    private void emptyInputs(){
        Toast.makeText(this, "Inputs are invalid!", Toast.LENGTH_SHORT).show();
    }


}
