package com.example.charitychecker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class bloodActivity extends AppCompatActivity {
    ArrayList<bloodList> listviewArray;

    bloodAdapter adapter;
    ListView bloodListview;
    Button theDonate;
    public bloodList currCharity = new bloodList();
    LatLng ZipCoords;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blood);


        Intent callerIntent = getIntent();
        String zipCode = callerIntent.getStringExtra("zipCode");


        Log.e("onCreate:", "Before generatelist");




        getZipGeo(zipCode, new bloodActivity.VolleyCallBack(){
            @Override
            public void onSuccess() {
                Log.e("GEOCODE SUCESS", "geoCode recieved:" + ZipCoords.latitude);
                generateList(zipCode);
                adapter = new bloodAdapter(bloodActivity.this, listviewArray);
                bloodListview = findViewById(R.id.bloodListView);
                bloodListview.setAdapter(adapter);
                bloodListview.setDivider(null);

                adapter.notifyDataSetChanged();
                adapter.clear();

            }
        });



    }


    public interface VolleyCallBack {
        void onSuccess();
    }

    public String getAPIKey(String whichKey){
        try {
            ApplicationInfo ai = getPackageManager().getApplicationInfo(
                    getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            String myAPIKey = bundle.getString(whichKey);
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


    public String getDonateURL(String EIN) {
        // create url for donation
        String stringurl = "https://www.charitynavigator.org/index.cfm?bay=my.donations.makedonation&ein=" + EIN;
        return stringurl;
    }

    public void getZipGeo(String zip, final bloodActivity.VolleyCallBack callBack){
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

    public void bloodQuery(String zip, final VolleyCallBack callBack){
        // create queue object
        RequestQueue queue;
        queue = Volley.newRequestQueue(this);

        Log.e("makeQuery:", "in make query");

        // set URL based on api key
        String auth = "&key=" + getAPIKey("com.google.android.geo.API_KEY");

        // get the geoCoded coordinates for the zipcode provided
        double theLat = ZipCoords.latitude;
        double theLong = ZipCoords.longitude;

        String myUrl = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?keyword=blood+drive&location=" + theLat +"%2C" + theLong + "&radius=20000" + auth;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, myUrl, null,
                // if request is successful
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            JSONArray json = response.getJSONArray("results");
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


                                callBack.onSuccess();

                                Log.e("makeQuery (onResponse)","Charity name is " + currCharity.getName());
                            }

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

    private void generateList(String zip){
        Log.e("generateList:", "in mgeneratelist");
        listviewArray = new ArrayList<>();
        bloodQuery(zip, new VolleyCallBack() {
            @Override
            public void onSuccess() {
                    // add current charity to list view
                    listviewArray.add(new bloodList("NA",
                            currCharity.getName(), "NA",
                            "NA",currCharity.getAddress(),
                            currCharity.getDonateURL(), "NA"));

                    //Log.e("ON SUCEESS", "donation url is " + currCharity.getDonateURL());
                    //Log.e("ON SUCESS", "category is " + currCharity.getCategory());
                    adapter.notifyDataSetChanged();
            }
        });

    }

}




