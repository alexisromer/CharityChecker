package com.example.charitychecker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class charityActivity extends AppCompatActivity implements View.OnClickListener {
    ArrayList<charityList> listviewArray;
    charityAdapter adapter;
    ListView charityListview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charity);

        generateList();
        TextView zipCode = (TextView)findViewById(R.id.zipCode);

        Intent callerIntent = getIntent();
        String zipInfo = callerIntent.getStringExtra("zipcode");

        //listviewArray.addAll(allEventsList);

        adapter = new charityAdapter(this, listviewArray);
        charityListview = findViewById(R.id.charityListView);
        charityListview.setAdapter(adapter);
        Button charityTest = findViewById(R.id.charityTestButton);
        charityTest.setOnClickListener((View.OnClickListener) this);


    }

    private void generateList(){
        listviewArray = new ArrayList<>();

    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.charityTestButton) {
            listviewArray.add(new charityList("Francis Ouimet Scholarship fund", "Scholarships for caddies since 1949", "Scholarship and Financial Support",
                    "300 Arnold Palmer Boulevard, Norton MA 02766"));
            adapter.notifyDataSetChanged();


        }


    }
}
