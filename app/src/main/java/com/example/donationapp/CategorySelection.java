package com.example.donationapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class CategorySelection extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_selection);

        findViewById(R.id.buttonNgoRegister).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CategorySelection.this, NgoCategoryRegister.class);

                startActivity(intent);
            }
        });
        findViewById(R.id.buttonVolRegister).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CategorySelection.this, VolCategoryRegister.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.buttonBankRegister).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CategorySelection.this, BankRegister.class);
                startActivity(intent);
            }
        });


    }


}

