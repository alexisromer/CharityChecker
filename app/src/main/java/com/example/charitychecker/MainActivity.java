package com.example.charitychecker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        Button zipActivity = findViewById(R.id.zipButton);
        zipActivity.setOnClickListener((View.OnClickListener) this);
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.zipButton) {
            optionsActivity();
        }

    }

    public void optionsActivity(){
        EditText zipCodeEditText = findViewById(R.id.zipCode);
        String zipInfo = zipCodeEditText.getText().toString();
        Intent activityIntent = new Intent(this, optionsActivity.class);
        activityIntent.putExtra("zipCode", zipInfo);
        startActivity(activityIntent);
    }
}