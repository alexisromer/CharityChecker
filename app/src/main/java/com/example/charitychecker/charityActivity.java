package com.example.charitychecker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

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

public class charityActivity extends AppCompatActivity implements View.OnClickListener {
    ArrayList<charityList> listviewArray;

    charityAdapter adapter;
    ListView charityListview;
    public charityList currCharity = new charityList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charity);

        //Intent callerIntent = getIntent();
        //String zipInfo = callerIntent.getStringExtra("zipCode");
        //EditText zipCodeEditText = findViewById(R.id.zipCode);
        //String zipInfo = zipCodeEditText.getText().toString();

        Intent callerIntent = getIntent();
        String zipCode = callerIntent.getStringExtra("zipCode");
        Log.e("onCreate:", "the zipcode is" + zipCode);

        generateList(zipCode);
        Log.e("onCreate:", "Before generatelist");

        //listviewArray.addAll(allEventsList);

        adapter = new charityAdapter(this, listviewArray);
        charityListview = findViewById(R.id.charityListView);
        charityListview.setAdapter(adapter);
        charityListview.setDivider(null);
        Button charityTest = findViewById(R.id.charityTestButton);
        charityTest.setOnClickListener((View.OnClickListener) this);

        adapter.notifyDataSetChanged();
        adapter.clear();

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
           Log.e("Get Donate URL", "API KEY : " + myAPIKey);
            return myAPIKey;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("Get Donate URL",
                    "Failed to load meta-data, NameNotFound: " + e.getMessage());
            return "ERROR";
        } catch (NullPointerException e) {
            Log.e("Get Donate URL",
                    "Failed to load meta-data, NullPointer: " + e.getMessage());
            return "ERROR";
        }
    }


    public String getDonateURL(String EIN) {
        // create url for donation
        String stringurl = "https://www.charitynavigator.org/index.cfm?bay=my.donations.makedonation&ein=" + EIN;
        return stringurl;
    }

    public void makeQuery(String zip, final VolleyCallBack callBack){
        // create queue object
        RequestQueue queue;
        queue = Volley.newRequestQueue(this);

        Log.e("makeQuery:", "in make query");

        // set URL based on api key
        String auth = "?app_id=" + getAPIKey("idValue") + "&app_key=" +getAPIKey("keyValue");

        String pageSize = "&pageSize=100";
        String sort = "&sort=RATING%3ADESC";
        //String fundRaise = "&fundraisingOrgs=true";
        String myUrl = "https://api.data.charitynavigator.org/v2/Organizations"  + auth + sort + pageSize + "&zip=" + zip;


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

                                // Get the current name (json object) data
                                String name = test.getString("charityName");
                                currCharity.setName(name);

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


                                // Get cause JSON object
                                // try - if there is an exception, set cause to "not available"
                                 try {
                                    JSONObject causeObj = test.getJSONObject("cause");
                                    String cause = causeObj.getString("causeName");

                                    // change "Null" to "Not available"
                                    if (cause.equals("null")){
                                        currCharity.setCause("Not available");
                                    } else {
                                        currCharity.setCause(cause);
                                    }
                                } catch (JSONException e){
                                    currCharity.setCause("Not available");
                                }


                                // Get the current address
                                JSONObject mailAddress = test.getJSONObject("mailingAddress");
                                String address = mailAddress.getString("streetAddress1")
                                        + "," + mailAddress.getString("city") + ","
                                        + mailAddress.getString("stateOrProvince") + " "
                                        + mailAddress.getString("postalCode");
                                currCharity.setAddress(address);

                                // set the donate url
                                String dUrl = getDonateURL(currCharity.getEIN());
                                currCharity.setDonateURL(dUrl);

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
        makeQuery(zip, new VolleyCallBack() {
            @Override
            public void onSuccess() {
               if (!currCharity.getCause().equals("Not available")){
                    // add current charity to list view
                    listviewArray.add(new charityList(currCharity.getEIN(),
                            currCharity.getName(), currCharity.getTagline(),
                            currCharity.getCause(),currCharity.getAddress(),
                            currCharity.getDonateURL()));

                    Log.e("ON SUCEESS", "donation url is " + currCharity.getDonateURL());
               }

            }
        });

    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.charityTestButton) {

            adapter.notifyDataSetChanged();


        }


    }

}
