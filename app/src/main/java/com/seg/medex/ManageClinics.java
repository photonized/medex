package com.seg.medex;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

public class ManageClinics extends AppCompatActivity {

    private RecyclerView list;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_clinics);

        this.list = findViewById(R.id.clinic_list);

        list.setHasFixedSize(true);
        manager = new LinearLayoutManager(this);
        list.setLayoutManager(manager);
        adapter = new Adapter()
    }

    class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
        private String[] dataset;

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            TextView v = (TextView) LayoutInflater.from(parent.getContext()).inflate(R.id.clinic_list, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.textView.setText(dataset[position]);
        }

        @Override
        public int getItemCount() {
            return dataset.length;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            public TextView textView;
            public ViewHolder(TextView v) {
                super(v);
                textView = v;
            }
        }

        public Adapter(String[] dataset) {
            this.dataset = dataset;
        }
    }
}
