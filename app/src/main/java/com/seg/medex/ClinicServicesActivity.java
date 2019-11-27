package com.seg.medex;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_UP;

public class ClinicServicesActivity extends AppCompatActivity {

    private String documentID;
    private ListView list;
    private CustomAdapter adapter;
    final ArrayList<String[]> elements = new ArrayList<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    final ArrayList<String> selected = new ArrayList<>();
    private Button continueButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clinic_services);

        this.list = findViewById(R.id.user_list);

        final ArrayList<String> ids = new ArrayList<>();

        final SharedPreferences sharedPreferences = getSharedPreferences("ID", 0);

        continueButton = findViewById(R.id.continue_button);

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

        this.continueButton.setOnTouchListener(touchListener);

        db.collection("users").whereEqualTo("username", sharedPreferences.getString("username", ""))
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult() != null) {
                        Log.d("This", String.valueOf(task.getResult().getDocuments().size()));
                        for (int i = 0; i < task.getResult().getDocuments().size(); i++) {
                            Log.d("AAAA", task.getResult().getDocuments().get(i).get("services").toString());
                            for(int j = 0; j<((ArrayList<String>)task.getResult().getDocuments().get(i).get("services")).size(); j++) {
                                ids.add(((ArrayList<String>)task.getResult().getDocuments().get(i).get("services")).get(j));
                                Log.d("IDS", ids.toString());
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
                                            Log.d("AAAA", task.getResult().getDocuments().get(i).get("name").toString());
                                            elements.add(new String[]{task.getResult().getDocuments().get(i).get("name").toString(), task.getResult().getDocuments().get(i).get("role").toString(), task.getResult().getDocuments().get(i).getId()});
                                            setAdapter(elements);
                                        }
                                        list.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                for(int j = 0; j < list.getCount(); j++) {

                                                    if(ids.contains(((String[])list.getAdapter().getItem(j))[2])) {
                                                            Log.d("ADAPTER::", ((String[])list.getAdapter().getItem(j))[2]);
                                                            list.getChildAt(j).setBackgroundColor(Color.YELLOW);
                                                            selected.add(elements.get(j)[2]);
                                                        if(selected.size() > 0) {
                                                            Log.d("SSSSSSSSSSSSSSSS", "aaaaaaaaa");
                                                            sharedPreferences.edit().remove("sc");
                                                            sharedPreferences.edit().putBoolean("sc", true);
                                                            sharedPreferences.edit().commit();
                                                        } else {
                                                            Log.d("SSSSSSSSSSSSSSSS", "bbbbbbbbbbbb");
                                                            sharedPreferences.edit().remove("sc");
                                                            sharedPreferences.edit().putBoolean("sc", false);
                                                            sharedPreferences.edit().commit();
                                                        }
                                                    }
                                                }
                                            }
                                        });
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
                }
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Manage Clinics: ", "Failed. Contact a developer.");
                    }
                });





        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ColorDrawable viewColour = (ColorDrawable) view.getBackground();

                if (viewColour == null){
                    view.setBackgroundColor(Color.YELLOW);
                    addToSelectedServices(elements.get(i)[2]);
                }else if (viewColour.getColor() == Color.TRANSPARENT){
                    view.setBackgroundColor(Color.YELLOW);
                    addToSelectedServices(elements.get(i)[2]);
                }
                else{
                    view.setBackgroundColor(Color.TRANSPARENT);
                    view.setSelected(false);
                    selected.remove(elements.get(i)[2]);
                }

                if(selected.size() > 0) {
                    Log.d("SSSSSSSSSSSSSSSS", "aaaaaaaaa");
                    sharedPreferences.edit().remove("sc");
                    sharedPreferences.edit().putBoolean("sc", true);
                    sharedPreferences.edit().commit();
                    Log.d("SERVICES ACTIVITY:", sharedPreferences.getBoolean("cc", false) + " " + sharedPreferences.getBoolean("sc", false) + " " + sharedPreferences.getBoolean("tc", false));

                } else {
                    Log.d("SSSSSSSSSSSSSSSS", "bbbbbbbbbbbb");
                    sharedPreferences.edit().remove("sc");
                    sharedPreferences.edit().putBoolean("sc", false);
                    sharedPreferences.edit().commit();
                }
            }
        });
    }

    public void addToSelectedServices(String id){
        selected.add(id);
    }

    public void onContinueClick(View view){
        SharedPreferences sharedPreferences = getSharedPreferences("ID", 0);
        final String clinic_name = sharedPreferences.getString("clinic_name","");

        db.collection("users").whereEqualTo("username", sharedPreferences.getString("username", ""))
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot query = task.getResult();
                    String id = query.getDocuments().get(0).getId();
                    Map<String, Object> services = new HashMap<>();
                    services.put("services", selected);
                    db.collection("users").document("/" + id).set(services, SetOptions.merge());
                    Toast.makeText(ClinicServicesActivity.this, "Services updated!!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if(selected.size() > 0) {
            Log.d("SSSSSSSSSSSSSSSS", "aaaaaaaaa");
            sharedPreferences.edit().remove("sc");
            sharedPreferences.edit().putBoolean("sc", true);
            sharedPreferences.edit().commit();
        } else {
            Log.d("SSSSSSSSSSSSSSSS", "bbbbbbbbbbbb");
            sharedPreferences.edit().remove("sc");
            sharedPreferences.edit().putBoolean("sc", false);
            sharedPreferences.edit().commit();
        }

        finish();

    }



    private void emptyInputs(){
        Toast.makeText(this, "Inputs are invalid!", Toast.LENGTH_SHORT).show();
    }

    private void setAdapter(ArrayList<String[]> elements) {
        adapter = new CustomAdapter(this, elements);
        list.setAdapter(adapter);
    }

    public class CustomAdapter extends BaseAdapter implements ListAdapter {

        private Context context;
        public ArrayList<String[]> list;
        private String id;

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

            id = service[2];

            return view;
        }
    }

    /*
        Helper methods
     */
    private static boolean isAlpha(String s) {
        char[] alpha = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', ' '};
        s = s.toLowerCase();
        for(int i = 0; i<s.length(); i++) {
            if(!(Utility.includes(alpha, s.charAt(i)))) {
                return false;
            }
        }
        return true;
    }
}
