package com.example.charitychecker;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.charitychecker.databinding.ActivityFoodBankMapBinding;

public class FoodBankMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityFoodBankMapBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityFoodBankMapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng currLocLatLong = new LatLng(14.0583, 108.2772);
        LatLng otherLatLong = new LatLng(15.1, 103.278);
        this.mMap.moveCamera(CameraUpdateFactory.newLatLng(currLocLatLong));
        mMap.addMarker(new MarkerOptions().position(currLocLatLong).title("Marker in"+ currLocLatLong.latitude+currLocLatLong.longitude));
        mMap.addMarker(new MarkerOptions().position(otherLatLong).title("Marker in"+ otherLatLong.latitude+otherLatLong.longitude));


    }
}