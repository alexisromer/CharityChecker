package com.example.charitychecker;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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

        return listItemView;
    }


}
