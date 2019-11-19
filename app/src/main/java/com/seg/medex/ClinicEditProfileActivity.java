package com.seg.medex;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.antonious.materialdaypicker.MaterialDayPicker;

public class ClinicEditProfileActivity extends AppCompatActivity {

    private EditText clinic_name;

    private EditText street_number;

    private EditText street_name;

    private EditText insurance_types;

    private EditText payment_method;

    private MaterialDayPicker selectedDays;

    private ImageView imageView;

    private Spinner open_hour;

    private Spinner close_hour;


    /**
     * First name text box.
     */
    private EditText firstName;

    /**
     * Last name text box.
     */
    private EditText lastName;

    /**
     * The shared preferences of the app containing the user info.
     */
    private SharedPreferences preferences;

    /**
     * The editor for the shared preferences.
     */
    private SharedPreferences.Editor editor;

    /**
     * The database object for Firebase Firestore.
     */
    private FirebaseFirestore db;

    /**
     * Continue button.
     */
    private Button continueButton;


    // Omer's getto comment to store days

    private List<String> days;


    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    /**
     * Continue button circle.
     */
    private ProgressBar continueCircle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clinic_edit_profile);
        this.imageView = findViewById(R.id.imageView);

        this.preferences = getSharedPreferences("ID", 0);

        this.clinic_name = findViewById(R.id.clinic_name);
        this.street_number = findViewById(R.id.street_number);
        this.street_name = findViewById(R.id.street_name);
        this.insurance_types = findViewById(R.id.insurance_types);
        this.payment_method = findViewById(R.id.payment_method);
        this.selectedDays = findViewById(R.id.day_picker);
        this.open_hour = findViewById(R.id.spinner);
        this.close_hour = findViewById(R.id.spinner2);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.time_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        open_hour.setAdapter(adapter);
        //open_hour.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);
        close_hour.setAdapter(adapter);
        //close_hour.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);



        this.lastName = findViewById(R.id.last_name);
        this.editor = preferences.edit();
        this.continueButton = findViewById(R.id.continue_button);
        this.continueCircle = findViewById(R.id.continue_circle);
        this.continueCircle.setVisibility(View.INVISIBLE);
        this.db = FirebaseFirestore.getInstance();


        db.collection("users").whereEqualTo("username", preferences.getString("username", " "))
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot query) {
                DocumentSnapshot doc = query.getDocuments().get(0);
                if (doc.get("clinic_name") == null){
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("clinic_name","");
//                                editor.putInt("street_number",0);
//                                editor.putString("street_name","");
//                                editor.putString("postal_code","");
                    //and we call days whenever we want rip
                }else{
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("clinic_name",(String) doc.get("clinic_name"));
//                                editor.putInt("street_number",(Integer) doc.get("street_number"));
//                                editor.putString("street_name",(String)doc.get("street_number"));
//                                editor.putString("postal_code",(String)doc.get("postal_code"));

                    days =(ArrayList) doc.get("days");
                    clinic_name.setText((String) doc.get("clinic_name"));
                    street_number.setText((String) doc.get("street_number"));
                    street_name.setText((String)doc.get("street_name"));
                    insurance_types.setText((String)doc.get("insurance_types"));
                    payment_method.setText((String)doc.get("payment_method"));

                    //days selector gotta dix it later
                    for(int i = 0; i<days.size();i++){
                        if(days.get(i).equals("MONDAY"))
                            selectedDays.selectDay(MaterialDayPicker.Weekday.MONDAY);
                        if(days.get(i).equals("TUESDAY"))
                            selectedDays.selectDay(MaterialDayPicker.Weekday.TUESDAY);
                        if(days.get(i).equals("WEDNESDAY"))
                            selectedDays.selectDay(MaterialDayPicker.Weekday.WEDNESDAY);
                        if(days.get(i).equals("THURSDAY"))
                            selectedDays.selectDay(MaterialDayPicker.Weekday.THURSDAY);
                        if(days.get(i).equals("FRIDAY"))
                            selectedDays.selectDay(MaterialDayPicker.Weekday.FRIDAY);
                        if(days.get(i).equals("SATURDAY"))
                            selectedDays.selectDay(MaterialDayPicker.Weekday.SATURDAY);
                        if(days.get(i).equals("SUNDAY"))
                            selectedDays.selectDay(MaterialDayPicker.Weekday.SUNDAY);
                    }
                }
            }

        });





        setImageListener();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);
        } else if (requestCode == 2 && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
            imageView.setImageBitmap(bitmap);
        }
    }

    private void showPhotoDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this, R.style.DialogTheme);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.photo_dialog, null);
        dialogBuilder.setView(dialogView);

        final Button buttonPhoto = dialogView.findViewById(R.id.buttonTakePhoto);
        final Button buttonGallery = dialogView.findViewById(R.id.buttonSelectFromGallery);
        final Button buttonCancel = dialogView.findViewById(R.id.buttonCancelDialog);

        dialogBuilder.setTitle("Choose method to upload photo");
        final AlertDialog b = dialogBuilder.create();
        b.show();

        buttonPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int permission = ActivityCompat.checkSelfPermission(ClinicEditProfileActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

                if (permission != PackageManager.PERMISSION_GRANTED) {
                    // We don't have permission so prompt the user
                    ActivityCompat.requestPermissions(
                            ClinicEditProfileActivity.this,
                            PERMISSIONS_STORAGE,
                            3
                    );
                } else {
                    Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePicture, 1);
                    b.dismiss();
                }

            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                b.dismiss();
            }
        });

        buttonGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                ActivityCompat.requestPermissions(ClinicEditProfileActivity.this, permissions, 3);
                int permission = ActivityCompat.checkSelfPermission(ClinicEditProfileActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

                if (permission != PackageManager.PERMISSION_GRANTED) {
                    // We don't have permission so prompt the user
                    ActivityCompat.requestPermissions(
                            ClinicEditProfileActivity.this,
                            PERMISSIONS_STORAGE,
                            3
                    );
                } else {
                    Intent i = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, 2);
                    b.dismiss();
                }
            }
        });

    }

    private void setImageListener () {
        imageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showPhotoDialog();
            }
        });
    }

    public void onContinueClick(View view) {
        findViewById(R.id.continue_button).setEnabled(false);

        SharedPreferences sharedPreferences = getSharedPreferences("ID", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        final String clinicName = clinic_name.getText().toString();
        final String streetNumber = street_number.getText().toString();
        final String streetName = street_name.getText().toString();
        final String paymentMethod = payment_method.getText().toString();
        final String insuranceTypes = insurance_types.getText().toString();
        final List<MaterialDayPicker.Weekday> days = selectedDays.getSelectedDays();
        //will uncomment when spinner values are there
        final String openHour = open_hour.getSelectedItem().toString();
        final String closeHour = close_hour.getSelectedItem().toString();

        boolean pass =  validateClinicName(clinicName) && validateStreetNumber(streetNumber)
                && validateStreetName(streetName) && validateSelectedDays(days)
                && validateOpenHour(openHour) && validateCloseHour(closeHour);

        if (!(TextUtils.isEmpty(clinicName) || TextUtils.isEmpty(streetNumber) || TextUtils.isEmpty(streetName) || TextUtils.isEmpty(insuranceTypes) || TextUtils.isEmpty(paymentMethod)
                || TextUtils.isEmpty(openHour)|| TextUtils.isEmpty(closeHour)) ) {
            //have to do it like this or toast doesn't appear
            if (pass){
                editor.putString("clinic_name", clinicName);
                editor.apply();

                //sends off the HashMap to the server
                db.collection("users").whereEqualTo("username", preferences.getString("username", ""))
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                QuerySnapshot query = task.getResult();
                                db.collection("users").whereEqualTo("username", preferences.getString("username", ""))
                                        .get()
                                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                            @Override
                                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                String id = queryDocumentSnapshots.getDocuments().get(0).getId();
                                                Map<String, Object> user = (Map<String, Object>) preferences.getAll();
                                                user.put("days", days);
                                                user.put("clinic_name", clinicName);
                                                user.put("street_number", streetNumber);
                                                user.put("street_name", streetName);
                                                user.put("insurance_types", insuranceTypes);
                                                user.put("payment_method", paymentMethod);
                                                user.put("open_hour", openHour);
                                                user.put("close_hour", closeHour);
                                                db.collection("users").document("/" + id).update(user);
                                            }
                                        });
                            }
                        });

                startActivity(new Intent(this, LandingActivity.class));

            }else{
                findViewById(R.id.continue_button).setEnabled(true);
            }

        }else{
            emptyInputs();
            findViewById(R.id.continue_button).setEnabled(true);
        }
    }
    public boolean validateClinicName(String clinicName){
        if (clinicName.length() > 50 ){
            //if we add check marks or X's, then this will change
            Toast.makeText(this, "Clinic Name is invalid!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    public boolean validateStreetNumber(String streetNumber){
        if (!Utility.isNumeric(streetNumber) ){
            //if we add check marks or X's, then this will change
            Toast.makeText(this, "Street Number is invalid!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    public boolean validateStreetName(String streetName){
        if (streetName.length() > 50 ){
            //if we add check marks or X's, then this will change
            Toast.makeText(this, "Street Name is invalid!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    public boolean validatePostalCode(String postalCode){
        if (!Utility.isAlphanumeric(postalCode) ){
            //if we add check marks or X's, then this will change
            Toast.makeText(this, "Postal Code is invalid!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public boolean validateOpenHour(String openHour){
        if (!Utility.isAlphanumeric(openHour) ){
            //if we add check marks or X's, then this will change
            Toast.makeText(this, "Open hour is invalid!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public boolean validateCloseHour(String closeHour){
        if (!Utility.isAlphanumeric(closeHour) ){
            //if we add check marks or X's, then this will change
            Toast.makeText(this, "Close hour is invalid!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    public boolean validateSelectedDays(List<MaterialDayPicker.Weekday> days){
        if (days.size() == 0){
            //if we add check marks or X's, then this will change
            Toast.makeText(this, "Days are invalid!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void emptyInputs(){
        Toast.makeText(this, "Inputs are empty!", Toast.LENGTH_SHORT).show();
    }
}