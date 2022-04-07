package com.example.charitychecker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class optionsActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        Button donateButton = findViewById(R.id.donate_button);
        donateButton.setOnClickListener((View.OnClickListener) this);


    }



    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.donate_button) {
            listActivity();
        }
        if (view.getId() == R.id.foodbanks_button){
            foodBankActivitylist();
        }
        if(view.getId() == R.id.dropoff_button){
            boxActivityList();
        }
    }

    public void listActivity(){
        Intent callerIntent = getIntent();
        String zipCode = callerIntent.getStringExtra("zipCode");
        Intent listIntent = new Intent(this, charityActivity.class);
        listIntent.putExtra("zipCode", zipCode);
        startActivity(listIntent);
    }

    public void foodBankActivitylist(){
        Intent callerIntent = getIntent();
        String zipCode = callerIntent.getStringExtra("zipCode");
        Intent foodBankIntent = new Intent(this, foodBankActivity.class);
        foodBankIntent.putExtra("zipCode", zipCode);
        startActivity(foodBankIntent);
    }

    public void boxActivityList(){
        Intent callerIntent = getIntent();
        String zipCode = callerIntent.getStringExtra("zipCode");
        Intent boxIntent = new Intent(this, boxActivity.class);
        boxIntent.putExtra("zipCode", zipCode);
        startActivity(boxIntent);
    }
}