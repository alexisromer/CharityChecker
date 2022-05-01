package com.example.charitychecker;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.charitychecker.databinding.ActivityFoodBankMapBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Vector;

public class FoodBankMapActivity extends FragmentActivity implements OnMapReadyCallback {

    public GoogleMap mMap;
    private ActivityFoodBankMapBinding binding;
    public LatLng ZipCoords;

    charityList currCharity = new charityList();
    public Vector<charityList> mapMarker = new Vector<charityList>();


    public interface VolleyCallBack {
        void onSuccess();
    }

    public String getAPIKey(String whichKey){
        try {
            ApplicationInfo ai = getPackageManager().getApplicationInfo(
                    getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            String myAPIKey = bundle.getString(whichKey);
            Log.e("Get API Key", "API KEY : " + myAPIKey);
            return myAPIKey;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("Get API Key",
                    "Failed to load meta-data, NameNotFound: " + e.getMessage());
            return "ERROR";
        } catch (NullPointerException e) {
            Log.e("Get API Key",
                    "Failed to load meta-data, NullPointer: " + e.getMessage());
            return "ERROR";
        }
    }

    public void getZipGeo(String zip, final FoodBankMapActivity.VolleyCallBack callBack){
        String tag = "geoCode";
        String APIKEY= getAPIKey("com.google.android.geo.API_KEY");

        // create JSON request URL
        String URL = "https://maps.googleapis.com/maps/api/geocode/json?address=" + zip + "&key=" + APIKEY;

        // create queue object
        RequestQueue queue;
        queue = Volley.newRequestQueue(this);
        final double[] lat = new double[1];
        final double[] lng = new double[1];

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, URL, null,
                // if request is successful
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Get JSON object containing latlong coordinates
                            JSONObject location = response.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location");
                            lat[0] = location.getDouble("lat");
                            lng[0] = location.getDouble("lng");

                            // set global variable to retrieved values
                            ZipCoords = new LatLng(lat[0], lng[0]);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e(tag, "JSONException");
                        }
                        callBack.onSuccess();
                    }
                },
                // if there is an error with request
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("HERE: ", error.getMessage());
                    }
                });
        queue.add(request);
    }

    public void foodbankQuery(String zip, final FoodBankMapActivity.VolleyCallBack callBack){
        // create queue object
        RequestQueue queue;
        queue = Volley.newRequestQueue(this);

        // set URL based on api key
        String auth = "&key=" + getAPIKey("com.google.android.geo.API_KEY");

        // get the geoCoded coordinates for the zipcode provided
        double theLat = ZipCoords.latitude;
        double theLong = ZipCoords.longitude;
        Log.e("makeQuery", "theLat is " + theLat + " the long is " + theLong);
        String myUrl = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?keyword=food+pantry&location=" + theLat +"%2C" + theLong + "&radius=10000" + auth;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, myUrl, null,
                // if request is successful
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            JSONArray json = response.getJSONArray("results");
                            Log.e("Query", "response length is " + response.length());
                            for (int i = 0; i < json.length(); i++) {
                                // Get current json object
                                JSONObject result = json.getJSONObject(i);
                                String name = result.getString("name");

                                Log.e("Query", "Name is " + name);

                                // Get the current name (json object) data
                                currCharity.setName(name);

                                // Get the current address
                                String address = result.getString("vicinity");
                                currCharity.setAddress(address);
                                Log.e("Query", "Address is " + address);

                                // Get the lat and long
                                JSONObject location = result.getJSONObject("geometry").getJSONObject("location");
                                double lat = location.getDouble("lat");
                                double lng = location.getDouble("lng");

                                LatLng geoLoc = new LatLng(lat, lng);
                                currCharity.setGeoLocation(geoLoc);
                                Log.e("Query", "lat is " + lat + " long is " + lng);

                                String addressURL = currCharity.getAddress().replaceAll(" ", "+");
                                String mapsString = "https://www.google.com/maps/place/" + addressURL;
                                currCharity.setDonateURL(mapsString);

                                // add the charity to the list
                                mapMarker.add(new charityList(currCharity.getEIN(),
                                        currCharity.getName(), currCharity.getTagline(),
                                        currCharity.getCause(),currCharity.getAddress(),
                                        currCharity.getDonateURL(),currCharity.getGeoLocation()));


                                Log.e("makeQuery (onResponse)","Charity name is " + currCharity.getName());

                            }
                            callBack.onSuccess();

                        }catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("makeQuery:", "JSONException");
                        }
                    }
                },
                // if there is an error with request
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("HERE: ", error.getMessage());
                    }
                });

        queue.add(request);

    }
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

    public void pre(GoogleMap googleMap, String zip, final FoodBankMapActivity.VolleyCallBack callBack){
        getZipGeo(zip, new FoodBankMapActivity.VolleyCallBack(){
            @Override
            public void onSuccess() {
                foodbankQuery(zip, new FoodBankMapActivity.VolleyCallBack() {
                    @Override
                    public void onSuccess() {
                        callBack.onSuccess();

                    }
                });
            }
            });
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        Intent callerIntent = getIntent();
        String zipCode = callerIntent.getStringExtra("zipCode");
        String tag = "onMapReady";
        mMap = googleMap;
        pre(mMap, zipCode, new FoodBankMapActivity.VolleyCallBack(){
            @Override
            public void onSuccess(){
                mMap.moveCamera(CameraUpdateFactory.zoomTo(10));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(ZipCoords));
                for (int i = 0; i < mapMarker.size(); i++){
                    charityList thisCharity = mapMarker.elementAt(i);
                    Marker currMarker = mMap.addMarker(new MarkerOptions()
                            .position(thisCharity.getGeoLocation())
                            .title(thisCharity.getName())
                            .snippet(thisCharity.getAddress())
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.mark)
                            )
                    );
                    currMarker.setTag(thisCharity);
                }
                infoWindowAdapter markerInfoWindowAdapter = new infoWindowAdapter(getApplicationContext());
                googleMap.setInfoWindowAdapter(markerInfoWindowAdapter);

            }
        });


    }
}