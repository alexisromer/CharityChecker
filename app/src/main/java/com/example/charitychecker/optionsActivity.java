package com.example.charitychecker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class optionsActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

       // Button donateButton = findViewById(R.id.donate_button);
       // donateButton.setOnClickListener((View.OnClickListener) this);

       // Button foodBankButton = findViewById(R.id.foodbanks_button);
       // foodBankButton.setOnClickListener((View.OnClickListener) this);

        ImageView donatePicture = findViewById(R.id.donateImageView);
        donatePicture.setOnClickListener(this);

        ImageView foodPicture = findViewById(R.id.foodImageView);
        foodPicture.setOnClickListener(this);

        ImageView boxPicture = findViewById(R.id.boxImageView);
        boxPicture.setOnClickListener(this);




    }



    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.donateImageView) {
            listActivity();
        }
        if (view.getId() == R.id.foodImageView){
            FoodBankMapActivity();
        }
        if(view.getId() == R.id.boxImageView){
            boxActivityList();
        }

        if(view.getId() == R.id.bloodImageView){
            bloodList();
        }
    }

    public void listActivity(){
        Intent callerIntent = getIntent();
        String zipCode = callerIntent.getStringExtra("zipCode");
        Intent listIntent = new Intent(this, charityActivity.class);
        listIntent.putExtra("zipCode", zipCode);
        startActivity(listIntent);
    }


    public void FoodBankMapActivity(){
        Intent callerIntent = getIntent();
        String zipCode = callerIntent.getStringExtra("zipCode");
        Intent foodBankMapIntent = new Intent(this, FoodBankMapActivity.class);
        foodBankMapIntent.putExtra("zipCode", zipCode);
        startActivity(foodBankMapIntent);
    }

    public void boxActivityList(){
        Intent callerIntent = getIntent();
        String zipCode = callerIntent.getStringExtra("zipCode");
        Intent boxIntent = new Intent(Intent.ACTION_VIEW);
        boxIntent.setData(Uri.parse("https://bigsri.org/donateri/find-a-bin?near=" + zipCode));
        startActivity(boxIntent);
    }

    public void bloodList(){
        Intent callerIntent = getIntent();
        String zipCode = callerIntent.getStringExtra("zipCode");
        Intent bloodIntent = new Intent(this, bloodActivity.class);
        bloodIntent.putExtra("zipCode", zipCode);
        startActivity(bloodIntent);
    }
}