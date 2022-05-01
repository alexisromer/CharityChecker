package com.example.charitychecker;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public class infoWindowAdapter implements GoogleMap.InfoWindowAdapter {
    private Context context;
    public infoWindowAdapter(Context context) {
        this.context = context.getApplicationContext();
    }

    @Override
    public View getInfoWindow(Marker arg0) {
        return null;
    }

    @Override
    public View getInfoContents(Marker arg0) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v =  inflater.inflate(R.layout.markerwindow, null);


        LatLng latLng = arg0.getPosition();
        TextView name = (TextView) v.findViewById(R.id.Name);
        TextView tvLng = (TextView) v.findViewById(R.id.tv_lng);
        name.setText(arg0.getTitle());
        charityList currCharity = (charityList)arg0.getTag();


        tvLng.setText(currCharity.getAddress());
        return v;
    }
}