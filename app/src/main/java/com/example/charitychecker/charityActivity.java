package com.example.charitychecker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

import java.util.ArrayList;

public class charityActivity extends AppCompatActivity {
    ArrayList<charityList> listviewArray;
    ArrayList<charityList> dummyArray;
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
       // Button charityTest = findViewById(R.id.charityTestButton);
       // charityTest.setOnClickListener((View.OnClickListener) this);

        adapter.notifyDataSetChanged();
        adapter.clear();

    }

    public interface VolleyCallBack {
        void onSuccess();
    }

    public void makeQuery(String zip, final VolleyCallBack callBack){
        // create queue object
        RequestQueue queue;
        queue = Volley.newRequestQueue(this);

        Log.e("makeQuery:", "in make query");

        // set URL
        String auth = "?app_id=afeabef6&app_key=d5e89d0fa78f1da2caa6aa1afcd4c324";
        String myUrl = "https://api.data.charitynavigator.org/v2/Organizations"  + auth + "&pageSize=2&zip=" + zip;


        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, myUrl, null,
                // if request is successful
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try{

                            Log.e("OnResponse:", "Respose length is " + response.length());
                            for (int i = 0; i < response.length(); i++) {
                                Log.e("makeQuery (on Response)", "request successful");
                                // Get current json object
                                JSONObject test = response.getJSONObject(i);

                                // Get the current name (json object) data
                                String name = test.getString("charityName");
                                currCharity.setName(name);

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
                                // Get the current tagline (json object) data
                                // try - if there is an exception, set tagline to "not available"
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


        Log.e("makeQuery", "dummyArray size:" + dummyArray.size());

        queue.add(request);


    }

    private void generateList(String zip){
        Log.e("generateList:", "in mgeneratelist");
        listviewArray = new ArrayList<>();
        dummyArray = new ArrayList<>();

        makeQuery(zip, new VolleyCallBack() {

            @Override
            public void onSuccess() {
                // add current charity to list view
                listviewArray.add(new charityList(currCharity.getName(), currCharity.getTagline(),
                        currCharity.getCause(),currCharity.getAddress()));
                adapter.notifyDataSetChanged();

            }
        });

    }




}
