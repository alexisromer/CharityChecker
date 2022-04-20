package com.example.charitychecker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;

public class charityAdapter extends ArrayAdapter<charityList> {
    private charityAdapter context = this;


    ArrayList<charityList> listAdapter;
    public charityAdapter(Activity context, ArrayList<charityList> list) {
        super(context,0, list);
        listAdapter = list;
    }




    public View getView(final int position, View convertView, ViewGroup parent){
        View listItemView = convertView;

        if(listItemView == null){
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.charity_layout, parent, false);
        }

        charityList current = getItem(position);
        assert current != null;

        Button theDonate = listItemView.findViewById(R.id.theDonateButton);
        theDonate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String currentValue = current.getDonateURL();
                Intent donateIntent = new Intent(Intent.ACTION_VIEW);
                donateIntent.setData(Uri.parse(currentValue));
                view.getContext().startActivity(donateIntent);
            }
        });


        //entry charity name
        TextView charityText = listItemView.findViewById(R.id.CharityName);
        charityText.setText(current.getName());

        //entry tagline
        TextView tagLine = listItemView.findViewById(R.id.tagLine);
        tagLine.setText(current.getTagline());

        //entry cause
        TextView cause = listItemView.findViewById(R.id.cause);
        cause.setText(current.getCause());

        TextView address = listItemView.findViewById(R.id.address);
        address.setText(current.getAddress());

        ImageView categoryImage = listItemView.findViewById(R.id.testImageView);
        if(current.getCategory().equals("Animals")){
            categoryImage.setImageResource(R.drawable.animals1);
        }
        if(current.getCategory().equals("Arts") || current.getCategory().equals("Culture") || current.getCategory().equals("Humanities") ){
            categoryImage.setImageResource(R.drawable.humanities1);
        }
        if(current.getCategory().equals("Community Development")){
            categoryImage.setImageResource(R.drawable.community1_);
        }
        if(current.getCategory().equals("Education")){
            categoryImage.setImageResource(R.drawable.education1);
        }
        if(current.getCategory().equals("Environment")){
            categoryImage.setImageResource(R.drawable.environment1);
        }
        if(current.getCategory().equals("Health")){
            categoryImage.setImageResource(R.drawable.health1);
        }
        if(current.getCategory().equals("Human Services")){
            categoryImage.setImageResource(R.drawable.human_services1_);
        }
        if(current.getCategory().equals("Human and Civil Rights")){
            categoryImage.setImageResource(R.drawable.human_and_civil_rights1);
        }
        if(current.getCategory().equals("International")){
            categoryImage.setImageResource(R.drawable.international1);
        }
        if(current.getCategory().equals("Religion")){
            categoryImage.setImageResource(R.drawable.religion1);
        }
        if(current.getCategory().equals("Research and Public Policy")){
            categoryImage.setImageResource(R.drawable.research1);
        }




        return listItemView;
    }


    public void update() {
        this.notifyDataSetChanged();
    }


}
