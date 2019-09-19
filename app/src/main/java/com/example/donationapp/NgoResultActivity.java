package com.example.donationapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class NgoResultActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    String city = null, category, donationCategory;
    Button backButton;
    TextView noResultTv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        recyclerView = findViewById(R.id.recyclerView);
        backButton=findViewById(R.id.backButton);
        noResultTv=findViewById(R.id.noResultTv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        final Intent intent = getIntent();
        city = intent.getStringExtra("City");
        category = intent.getStringExtra("Category");
        donationCategory = intent.getStringExtra("DonationCategory");
final View view=findViewById(android.R.id.content);

        if (city != null) {
            final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(city).child(category).child(donationCategory);
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        FirebaseRecyclerOptions<NgoRecyclerViewData> options =
                                new FirebaseRecyclerOptions.Builder<NgoRecyclerViewData>()
                                        .setQuery(databaseReference, NgoRecyclerViewData.class)
                                        .build();
                        FirebaseRecyclerAdapter<NgoRecyclerViewData, DataViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<NgoRecyclerViewData, DataViewHolder>
                                (options) {
                            @Override
                            protected void onBindViewHolder(@NonNull DataViewHolder holder, int i, @NonNull NgoRecyclerViewData model) {
                                holder.name.setText(model.getNgoName());
                                holder.address.setText(model.getAddress());
                                holder.toTime.setText(model.getNgoToTime());
                                holder.fromTime.setText(model.getNgoFromTime());
                                holder.days.setText(model.getWorkingDays());
                                final String phoneNumber = model.getNgoPhone();
                                holder.call.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent1 = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));

                                        startActivity(intent1);
                                    }
                                });

                            }

                            @NonNull
                            @Override
                            public DataViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
                                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.result_cardview, viewGroup, false);
                                DataViewHolder viewHolder = new DataViewHolder(view);
                                return viewHolder;
                            }
                        };
                        recyclerView.setAdapter(firebaseRecyclerAdapter);
                        firebaseRecyclerAdapter.startListening();
                    }
                    else{
                        recyclerView.setVisibility(View.INVISIBLE);
                        noResultTv.setVisibility(View.VISIBLE);
                        noResultTv.setText("No Result Found");
                        backButton.setVisibility(View.VISIBLE);
                        backButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent1=new Intent(NgoResultActivity.this,MainActivity.class);
                                startActivity(intent1);
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        } else {
            recyclerView.setVisibility(View.INVISIBLE);
            noResultTv.setVisibility(View.VISIBLE);

            noResultTv.setText("No Result Found");
            backButton.setVisibility(View.VISIBLE);
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent1=new Intent(NgoResultActivity.this,MainActivity.class);
                    startActivity(intent1);
                }
            });
        }

    }

    public static class DataViewHolder extends RecyclerView.ViewHolder {
        TextView name, address, days, fromTime, toTime;
        Button call;


        public DataViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            address = itemView.findViewById(R.id.address);
            days = itemView.findViewById(R.id.days);
            fromTime = itemView.findViewById(R.id.fromTime);
            toTime = itemView.findViewById(R.id.toTime);
            call = itemView.findViewById(R.id.call);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent=new Intent(NgoResultActivity.this,MainActivity.class);
        startActivity(intent);
    }
}