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
import android.view.MotionEvent;
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

import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_UP;

public class ClinicEditProfileActivity extends AppCompatActivity {

    private EditText clinic_name;

    private EditText phone_number;

    private EditText street_address;

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
        this.phone_number = findViewById(R.id.phone_number);
        this.street_address = findViewById(R.id.street_address);
        this.insurance_types = findViewById(R.id.insurance_types);
        this.payment_method = findViewById(R.id.payment_method);


        //open_hour.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);
        //close_hour.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);



        this.lastName = findViewById(R.id.last_name);
        this.editor = preferences.edit();
        this.continueButton = findViewById(R.id.continue_button);
        this.continueCircle = findViewById(R.id.continue_circle);
        this.continueCircle.setVisibility(View.INVISIBLE);
        this.db = FirebaseFirestore.getInstance();

        View.OnTouchListener touchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case ACTION_DOWN:
                        v.setBackground(getResources().getDrawable(R.drawable.clicked_rectangle));
                        return true; // if you want to handle the touch event
                    case ACTION_UP:
                        v.setBackground(getResources().getDrawable(R.drawable.rectangle));
                        v.performClick();
                        return true; // if you want to handle the touch event
                }
                return false;
            }
        };

        continueButton.setOnTouchListener(touchListener);


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

                    days = (ArrayList) doc.get("days");
                    clinic_name.setText((String) doc.get("clinic_name"));
                    phone_number.setText((String) doc.get("phone_number"));
                    street_address.setText((String)doc.get("street_address"));
                    insurance_types.setText((String)doc.get("insurance_types"));
                    payment_method.setText((String)doc.get("payment_method"));
                    //Warnings come up here but we should be good
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
        final String phoneNumber = phone_number.getText().toString();
        final String streetAddress = street_address.getText().toString();
        final String paymentMethod = payment_method.getText().toString();
        final String insuranceTypes = insurance_types.getText().toString();

        boolean pass =  validateClinicName(clinicName) && validatePhoneNumber(phoneNumber)
                && validateStreetAddress(streetAddress);

        if (!(TextUtils.isEmpty(clinicName) || TextUtils.isEmpty(phoneNumber) || TextUtils.isEmpty(streetAddress) || TextUtils.isEmpty(insuranceTypes) || TextUtils.isEmpty(paymentMethod)) ) {
            //have to do it like this or toast doesn't appear
            if (pass){
                editor.putString("clinic_name", clinicName);
                preferences.edit().remove("cc");
                editor.putBoolean("cc", true);
                editor.commit();

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
                                                user.put("clinic_name", clinicName);
                                                user.put("phone_number", phoneNumber);
                                                user.put("street_address", streetAddress);
                                                user.put("insurance_types", insuranceTypes);
                                                user.put("payment_method", paymentMethod);
                                                db.collection("users").document("/" + id).update(user);
                                            }
                                        });
                            }
                        });
                Toast.makeText(this, "Clinic Profile updated.", Toast.LENGTH_SHORT).show();
                finish();

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
    public boolean validatePhoneNumber(String phoneNumber){
        if (!Utility.isNumeric(phoneNumber) ){
            //if we add check marks or X's, then this will change
            Toast.makeText(this, "Phone Number is invalid!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    public boolean validateStreetAddress(String streetAddress){
        if (streetAddress.length() > 50 && !isAlphanumeric(streetAddress)){
            //if we add check marks or X's, then this will change
            Toast.makeText(this, "Street Name is invalid!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


    private void emptyInputs(){
        Toast.makeText(this, "Inputs are empty!", Toast.LENGTH_SHORT).show();
    }
    //This one has a space
    private static boolean isAlphanumeric(String s) {
        char[] alphaNumeric = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',' '};
        for(int i = 0; i<s.length(); i++) {
            if(!(Utility.includes(alphaNumeric, s.charAt(i)))) {
                return false;
            }
        }
        return true;
    }
    
}