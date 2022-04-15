package com.example.charitychecker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.charitychecker.databinding.ActivityFoodBankMapBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;

public class FoodBankMapActivity extends FragmentActivity implements OnMapReadyCallback {

    public GoogleMap mMap;
    private ActivityFoodBankMapBinding binding;

    charityList currCharity = new charityList();
    public Vector<charityList> mapMarker = new Vector<charityList>();
    public int n = 0;


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

    public void geoCode(String address, final FoodBankMapActivity.VolleyCallBack callBack) {
        String tag = "geoCode";
        String APIKEY= getAPIKey("com.google.android.geo.API_KEY");
        String addressURL = address.replaceAll(" ","+");
        // create JSON request URL
        String URL = "https://maps.googleapis.com/maps/api/geocode/json?address=" + addressURL + "&key=" + APIKEY;

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

                            LatLng coords = new LatLng(lat[0], lng[0]);
                            currCharity.setGeoLocation(coords);
                            callBack.onSuccess();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e(tag, "JSONException");
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
                                // Get current json object
                                JSONObject json = response.getJSONObject(i);

                                // Get the current name (json object) data
                                String name = response.getJSONObject(i).getString("charityName");
                                currCharity.setName(name);

                                // Get the current EIN (json object) data
                                String ein = json.getString("ein");
                                currCharity.setEIN(ein);

                                // Get the current tagline (json object) data
                                // try - if there is an exception, set tagline to "not available"
                                try {
                                    String tag = json.getString("tagLine");
                                    // change "Null" to "Not available"
                                    if (tag.equals("null")){
                                        currCharity.setTagLine("Not available");
                                    } else {
                                        currCharity.setTagLine(tag);
                                    }
                                } catch (JSONException e){
                                    currCharity.setTagLine("Not available");
                                }


                                // Get the current address
                                JSONObject mailAddress = json.getJSONObject("mailingAddress");
                                String address = mailAddress.getString("streetAddress1")
                                        + "," + mailAddress.getString("city") + ","
                                        + mailAddress.getString("stateOrProvince") + " "
                                        + mailAddress.getString("postalCode");
                                currCharity.setAddress(address);

                                mapMarker.add(currCharity);
                                callBack.onSuccess();



                                Log.e("makeQuery (onResponse)", "n is" + n);

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


    public void pre(GoogleMap googleMap, final FoodBankMapActivity.VolleyCallBack callBack){
        foodBankQuery("01460", new FoodBankMapActivity.VolleyCallBack() {
            @Override
            public void onSuccess() {
                currCharity.setCause("Food bank");
                geoCode(currCharity.getAddress(), new FoodBankMapActivity.VolleyCallBack() {
                    @Override
                    public void onSuccess() {
                        mapMarker.add(currCharity);
                            Log.e("ON SUCESS", "charity name is " + mapMarker.lastElement().getName());
                            Log.e("ON SUCCESS", "name : " + currCharity.getName() + " location: "
                                    + currCharity.getGeoLocation().latitude + "," + currCharity.getGeoLocation().longitude);
                         callBack.onSuccess();
                    }
                });
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        String tag = "onMapReady";
        mMap = googleMap;
        pre(mMap, new FoodBankMapActivity.VolleyCallBack(){
            @Override
            public void onSuccess(){
                mMap.moveCamera(CameraUpdateFactory.newLatLng(currCharity.getGeoLocation()));
                mMap.moveCamera(CameraUpdateFactory.zoomTo(10));
                Log.e("IN MY PRE THING", "name : ");
                Log.e(tag,"size is : " + mapMarker.size());
                for (int i = 0; i < mapMarker.size(); i++){
                    charityList thisCharity = mapMarker.elementAt(i);
                    mMap.addMarker(new MarkerOptions()
                            .position(thisCharity.getGeoLocation())
                            .title(thisCharity.getName())
                            .snippet(thisCharity.getTagline())
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
                    );
                }

            }

        });


    }
}