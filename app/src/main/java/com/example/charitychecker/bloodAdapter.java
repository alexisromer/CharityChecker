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

public class bloodAdapter extends ArrayAdapter<bloodList> {
    private bloodAdapter context = this;


    ArrayList<bloodList> listAdapter;
    public bloodAdapter(Activity context, ArrayList<bloodList> list) {
        super(context,0, list);
        listAdapter = list;
    }




    public View getView(final int position, View convertView, ViewGroup parent){
        View listItemView = convertView;

        if(listItemView == null){
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.blood_layout, parent, false);
        }

        bloodList current = getItem(position);
        assert current != null;

        Button theDonate = listItemView.findViewById(R.id.DIRECTIONS);
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


        TextView address = listItemView.findViewById(R.id.address);
        address.setText(current.getAddress());





        return listItemView;
    }


    public void update() {
        this.notifyDataSetChanged();
    }


}
