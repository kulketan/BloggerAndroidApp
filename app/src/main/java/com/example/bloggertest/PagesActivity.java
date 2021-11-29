package com.example.bloggertest;

import static javax.xml.transform.OutputKeys.ENCODING;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class PagesActivity extends AppCompatActivity {

    private ActionBar actionBar;

    //UI Views
    private RecyclerView pagesRv;

    //arraylist of model page
    private ArrayList<ModelPage> pageArrayList;
    //adapter page instance
    private AdapterPage adapterPage;

    //TAG
    private static final String TAG = "PAGES_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pages);

        actionBar = getSupportActionBar();
        actionBar.setTitle(Constants.BLOG_NAME);
        actionBar.setSubtitle(Constants.BLOG_SLOGAN);
        //add back button
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        //init UI views
        pagesRv = findViewById(R.id.pagesRv);

        loadPages();
    }


    private void loadPages() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setMessage("Loading pages...");
        progressDialog.show();

        String url = "https://www.googleapis.com/blogger/v3/blogs/"+ Constants.BLOG_ID
                +"/pages/"
                +"?key=" + Constants.API_KEY;
        Log.d(TAG, "loadPages: URL" + url);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                //successfully recieved response
                Log.d(TAG, "onResponse: " + response);

                try {
                    //response is json object
                    JSONObject jsonObject = new JSONObject(response);
                    //get json array data from json
                    JSONArray jsonArray = jsonObject.getJSONArray("items");

                    //init and clear list before adding jsonObject
                    pageArrayList = new ArrayList<>();
                    pageArrayList.clear();

                    //continue getting data while its completed
                    for (int i = 0; i<jsonArray.length(); i++){
                        try {
                            //get data
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            String id = jsonObject1.getString("id");
                            String title = jsonObject1.getString("title");
                            String content = jsonObject1.getString("content");
                            String published = jsonObject1.getString("published");
                            String updated = jsonObject1.getString("updated");
                            String url = jsonObject1.getString("url");
                            String selfLink = jsonObject1.getString("selfLink");
                            String displayName = jsonObject1.getJSONObject("author").getString("displayName");
                            String image = jsonObject1.getJSONObject("author").getJSONObject("image").getString("url");

                            //set data
                            ModelPage modelPage = new ModelPage(""+displayName,
                                    ""+content,
                                    ""+id,
                                    ""+published,
                                    ""+selfLink,
                                    ""+title,
                                    ""+updated,
                                    ""+url);

                            //add data/model to list
                            pageArrayList.add(modelPage);

                        }
                        catch (Exception e){
                            Log.d(TAG, "onResponse: 1: " + e.getMessage());
                            Toast.makeText(PagesActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();

                        }
                    }

                } catch (JSONException e) {
                    Log.d(TAG, "onResponse: " + e.getMessage());
                }

                //setup adapter
                adapterPage = new AdapterPage(PagesActivity.this,pageArrayList);
                //set adapter to recylerview
                pagesRv.setAdapter(adapterPage);
                progressDialog.dismiss();
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //failed to retrive pages show error
                Toast.makeText(PagesActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        //add request to queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); //go previous activity when back button of actionbar is pressed.
        return super.onSupportNavigateUp();
    }
}