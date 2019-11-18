package com.seg.medex;

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
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.firebase.firestore.FirebaseFirestore;

public class ClinicProfileActivity extends AppCompatActivity {

    private ImageView imageView;

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
        setContentView(R.layout.activity_clinic_profile);
        this.imageView = findViewById(R.id.imageView);

        this.preferences = getSharedPreferences("ID", 0);

        this.firstName = findViewById(R.id.clinic_name);
        this.lastName = findViewById(R.id.last_name);
        this.editor = preferences.edit();

        this.continueButton = findViewById(R.id.continue_button);
        this.continueCircle = findViewById(R.id.continue_circle);
        this.continueCircle.setVisibility(View.INVISIBLE);
        this.db = FirebaseFirestore.getInstance();

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
                int permission = ActivityCompat.checkSelfPermission(ClinicProfileActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

                if (permission != PackageManager.PERMISSION_GRANTED) {
                    // We don't have permission so prompt the user
                    ActivityCompat.requestPermissions(
                            ClinicProfileActivity.this,
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
                ActivityCompat.requestPermissions(ClinicProfileActivity.this, permissions, 3);
                int permission = ActivityCompat.checkSelfPermission(ClinicProfileActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

                if (permission != PackageManager.PERMISSION_GRANTED) {
                    // We don't have permission so prompt the user
                    ActivityCompat.requestPermissions(
                            ClinicProfileActivity.this,
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
        startActivity(new Intent(this, ClinicServicesActivity.class));
    }
}
