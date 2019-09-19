package com.example.donationapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MyAccount extends AppCompatActivity {

    Toolbar toolbar;
    TextView nameTv, emailTv, phoneTv, availableFromTv, availableToTv, categoryTv, daysTv, locationTv;
    String name, email, phone, availableFrom, availableTo, category, days, location, uId;
    DatabaseReference getData;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_my_account);
        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        nameTv = findViewById(R.id.nameText);
        emailTv = findViewById(R.id.emailText);
        phoneTv = findViewById(R.id.phoneText);
        availableFromTv = findViewById(R.id.availableFromText);
        availableToTv = findViewById(R.id.availableToText);
        categoryTv = findViewById(R.id.availableCategoryText);
        daysTv = findViewById(R.id.availableDaysText);
        locationTv = findViewById(R.id.availableAddressText);


        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) {
            Intent intent = new Intent(MyAccount.this, RegisterActivity.class);
            startActivity(intent);
        } else {
            uId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            getData = FirebaseDatabase.getInstance().getReference().child("User").child(uId);
            getData.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                    getData.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                name = dataSnapshot.child("Name").getValue().toString();
                                email = dataSnapshot.child("Email").getValue().toString();

                                phone = dataSnapshot.child("Phone").getValue().toString();
                                availableFrom = dataSnapshot.child("FromTime").getValue().toString();

                                availableTo = dataSnapshot.child("ToTime").getValue().toString();
                                category = dataSnapshot.child("Category").getValue().toString();
                                days = dataSnapshot.child("Working Days").getValue().toString();
                                location = dataSnapshot.child("Address").getValue().toString();

                                nameTv.setText(name);
                                emailTv.setText(email);
                                phoneTv.setText(phone);
                                availableFromTv.setText(availableFrom);
                                availableToTv.setText(availableTo);
                                categoryTv.setText(category);
                                daysTv.setText(days);
                                locationTv.setText(location);
                            }
                            else{
                                Intent intent=new Intent(MyAccount.this,CategorySelection.class);
                                startActivity(intent);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        }
    }

    public void editDetails(View view){
      Intent intent=new Intent(MyAccount.this,CategorySelection.class);
      startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.homeMenu:
                Intent intent = new Intent(MyAccount.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                break;

            case R.id.myAccountMenu:

                break;
            case R.id.helpMenu:
                Toast.makeText(this, "Help", Toast.LENGTH_SHORT).show();
                break;
            case R.id.shareMenu:
                Toast.makeText(this, "Share", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
