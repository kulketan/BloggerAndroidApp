package com.example.bloggertest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    //UI VIews
    private RecyclerView postsRv;
    private Button loadMoreBtn;

    private String url = "";
    private String nextToken = "";

    private ArrayList<ModelPost> postArrayList;
    private AdapterPost adapterPost;

    private ProgressDialog progressDialog;

    private static final String TAG = "MAIN_TAG";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        postsRv = findViewById(R.id.postsRv);
        loadMoreBtn = findViewById(R.id.loadMoreBtn);

        //setup progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("PLease wait...");

        //init & clear list efore adding data into it
        postArrayList = new ArrayList<>();
        postArrayList.clear();
        
        loadPosts();

        //load more button

        loadMoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadPosts();
            }
        });
    }

    private void loadPosts() {
        progressDialog.show();

        if(nextToken.equals("")){
            Log.d(TAG,"loadPosts: Next Page token is empty, no more posts");
            url = "https://www.googleapis.com/blogger/v3/blogs/"
                    + Constants.BLOG_ID
                    +"/posts?maxResults="+Constants.MAX_POST_RESULTS
                    +"&key="+ Constants.API_KEY;
        }
        else if (nextToken.equals("end")){
            Log.d(TAG,"loadPosts: Next token is empty/end, no more posts");
            Toast.makeText(this,"No More Posts...",Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            return;
        }
        else {
            Log.d(TAG,"loadPOsts: next token: " + nextToken);
            url = "https://www.googleapis.com/blogger/v3/blogs/"
                    + Constants.BLOG_ID
                    +"/posts?maxResults="+Constants.MAX_POST_RESULTS
                    +"&pageToken=" + nextToken
                    +"&key="+ Constants.API_KEY;
        }
        Log.d(TAG,"loadPOsts: next URL: " + url);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // we got response, so dissmising progress dialogue
                progressDialog.dismiss();
                Log.d(TAG, "onResponse: " + response);

                try{
                    //response is in JSON object
                    JSONObject jsonObject = new JSONObject(response);
                    try {
                        nextToken = jsonObject.getString("nextPageToken");
                        Log.d(TAG,"onResponse: NextPageToken: " + nextToken);
                    }catch (Exception e){
                        Toast.makeText(MainActivity.this,"Reached end of page...",Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onResponse: reached end of page" + e.getMessage());
                        nextToken = "end";
                    }

                    //get json array data from json
                    JSONArray jsonArray = jsonObject.getJSONArray("items");

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
                            String authorName = jsonObject1.getJSONObject("author").getString("displayName");
                            //String image = jsonObject1.getJSONObject("author").getString("image");

                            //set data
                            ModelPost modelPost = new ModelPost(""+authorName,
                                    ""+content,
                                    ""+id,
                                    ""+published,
                                    ""+selfLink,
                                    ""+title,
                                    ""+updated,
                                    ""+url);

                            //add data/model to list
                            postArrayList.add(modelPost);

                        }
                        catch (Exception e){
                            Log.d(TAG, "onResponse: 1: " + e.getMessage());
                            Toast.makeText(MainActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();

                        }
                    }


                    //setup adapter
                    adapterPost = new AdapterPost(MainActivity.this,postArrayList);
                    //set adapter to recylerview
                    postsRv.setAdapter(adapterPost);
                    progressDialog.dismiss();


                }catch (Exception e){

                    Log.d(TAG, "onResponse: 2: " + e.getMessage());
                    Toast.makeText(MainActivity.this, ""+e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse : " + error.toString());
                Toast.makeText(MainActivity.this, ""+error.getMessage(),Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();

            }
        });

        //add request to queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}