package com.example.charitychecker;

import androidx.fragment.app.FragmentActivity;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.charitychecker.databinding.ActivityFoodBankMapBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class FoodBankMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityFoodBankMapBinding binding;

    charityList currCharity = new charityList();
    List<charityList> mapMarkers;


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

    public LatLng geoCode(String address, final FoodBankMapActivity.VolleyCallBack callBack) {
        Log.e("geoCode", "In geoCode");
        String APIKEY= getAPIKey("com.google.android.geo.API_KEY");
        String URL = "https://maps.googleapis.com/maps/api/geocode/json?address=1600+Amphitheatre+Parkway,+Mountain+View,+CA&key=" + APIKEY;

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

                            // Get current json object
                            JSONArray thing = response.getJSONArray("results");
                            JSONObject results = thing.getJSONObject(0);
                            Log.e("GeoCode", "HERE1");

                            JSONObject geometry = results.getJSONObject("geometry");
                            Log.e("GeoCode", "HERE2");
                            JSONObject location = geometry.getJSONObject("location");
                            lat[0] = location.getDouble("lat");
                            Log.e("GeoCode", "HERE3");
                            lng[0] = location.getDouble("lng");

                            Log.e("INSIDE GEOCODE","lat is " + lat[0] + " long is " + lng[0]);
                            callBack.onSuccess();

                        } catch (JSONException e) {
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
        LatLng coords = new LatLng(lat[0], lng[0]);
        Log.e("geoCode", "latitude is " + coords.latitude + " longitude is " + coords.longitude);
        return coords;

    }


    public void foodBankQuery(String zip, final FoodBankMapActivity.VolleyCallBack callBack){
        // create queue object
        RequestQueue queue;
        queue = Volley.newRequestQueue(this);

        Log.e("makeQuery:", "in make query");

        // set URL based on api key
        String auth = "?app_id=" + getAPIKey("idValue") + "&app_key=" +getAPIKey("keyValue");

        String foodBank = "&categoryID=6&causeID=18";
        String pageSize = "&pageSize=1";
        String sort = "&sort=RATING%3ADESC";
        String myUrl = "https://api.data.charitynavigator.org/v2/Organizations"  + auth + pageSize + foodBank;

        Log.e("BEFORE REQUEST", "url is " + myUrl);


        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, myUrl, null,
                // if request is successful
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try{
                            for (int i = 0; i < response.length(); i++) {
                                Log.e("makeQuery (on Response)", "request successful");

                                // Get current json object
                                JSONObject test = response.getJSONObject(i);

                               //Log.e("??", test.getJSONObject("name").getString("name"));

                                // Get the current name (json object) data
                                String name = response.getJSONObject(0).getString("charityName");
                                Log.e("??", name);
                                currCharity.setName(name);
                                Log.e("make query", "name is " + name);

                                // Get the current EIN (json object) data
                                String ein = test.getString("ein");
                                currCharity.setEIN(ein);

                                // Get the current tagline (json object) data
                                // try - if there is an exception, set tagline to "not available"
                                try {
                                    String tag = test.getString("tagLine");
                                    // change "Null" to "Not available"
                                    if (tag.equals("null")){
                                        currCharity.setTagLine("Not available");
                                    } else {
                                        currCharity.setTagLine(tag);
                                    }
                                } catch (JSONException e){
                                    currCharity.setTagLine("Not available");
                                }

                                currCharity.setCause("Food bank");


                                // Get the current address
                                JSONObject mailAddress = test.getJSONObject("mailingAddress");
                                String address = mailAddress.getString("streetAddress1")
                                        + "," + mailAddress.getString("city") + ","
                                        + mailAddress.getString("stateOrProvince") + " "
                                        + mailAddress.getString("postalCode");
                                currCharity.setAddress(address);


                                //Log.e("in query", "coords are " + thing.longitude + "," + thing.latitude);

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
        foodBankQuery("01460", new FoodBankMapActivity.VolleyCallBack() {
            @Override
            public void onSuccess() {
                //mapMarkers.add(new charityList(currCharity.getEIN(),
                //         currCharity.getName(), currCharity.getTagline(),
                //         currCharity.getCause(),currCharity.getAddress(),
                //          currCharity.getDonateURL(), currCharity.getGeoLocation()));
                // geocode the address
                LatLng thing = geoCode("TEST", new FoodBankMapActivity.VolleyCallBack() {
                    @Override
                    public void onSuccess() {
                        Log.e("on sucess", "Yay!");
                        //mapMarkers.add(new charityList(currCharity.getEIN(),
                        //         currCharity.getName(), currCharity.getTagline(),
                        //         currCharity.getCause(),currCharity.getAddress(),
                        //          currCharity.getDonateURL(), currCharity.getGeoLocation()));
                        // geocode the address
                    }
                });
                Log.e("map", "coords are " + thing.longitude + "," + thing.latitude);

            }
        });

        LatLng currLocLatLong = new LatLng(14.0583, 108.2772);
        LatLng otherLatLong = new LatLng(15.1, 103.278);
        this.mMap.moveCamera(CameraUpdateFactory.newLatLng(currLocLatLong));
        mMap.addMarker(new MarkerOptions().position(currLocLatLong).title("Marker in"+ currLocLatLong.latitude+currLocLatLong.longitude));
        mMap.addMarker(new MarkerOptions().position(otherLatLong).title("Marker in"+ otherLatLong.latitude+otherLatLong.longitude));


    }
}