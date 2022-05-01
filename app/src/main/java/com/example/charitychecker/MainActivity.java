package com.example.charitychecker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setTitle(Html.fromHtml("<font color =\"black\">" + getString(R.string.app_name) + "</font>"));

        Button zipActivity = findViewById(R.id.zipButton);
        zipActivity.setOnClickListener(this);

        ImageView aboutImage = findViewById(R.id.aboutImageView);
        aboutImage.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.zipButton) {
            optionsActivity();
        }
        if (view.getId() == R.id.aboutImageView){
            aboutActivity();
        }

    }

    public void optionsActivity(){
        EditText zipCodeEditText = findViewById(R.id.zipCode);
        String zipInfo = zipCodeEditText.getText().toString();

        if(zipInfo.equals("")){
            Context context = getApplicationContext();
            CharSequence text = "Please enter a zipcode";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
        else {
            Intent activityIntent = new Intent(this, optionsActivity.class);
            activityIntent.putExtra("zipCode", zipInfo);
            startActivity(activityIntent);
        }
    }

    public void aboutActivity(){
        Intent aboutIntent = new Intent(this, aboutActivity.class);
        startActivity(aboutIntent);
    }
}