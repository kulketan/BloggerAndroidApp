package com.example.bloggertest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class PostByLabelActivity extends AppCompatActivity {
    private RecyclerView postsRv;
    private Button loadMoreBtn;
    private Button retry;

    private String label;
    private String url = "";
    private String nextToken = "";

    private ArrayList<ModelPost> postArrayList;
    private AdapterPost adapterPost;

    private ProgressDialog progressDialog;

    private static final String TAG = "LABEL_POST_TAG";

    private ActionBar actionBar;

    private NavigationView navigationView;

    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_by_label);

        actionBar = getSupportActionBar();
        actionBar.setTitle(Constants.BLOG_NAME);
        actionBar.setSubtitle(Constants.BLOG_SLOGAN);

        postsRv = findViewById(R.id.postsRv);
        loadMoreBtn = findViewById(R.id.loadMoreBtn);

        //setup progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait...");

        //init & clear list before adding data into it
        postArrayList = new ArrayList<>();
        postArrayList.clear();

        //we need to check if network connection is available
        //if available loadposts will be called i.e posts will be fetched by API and then db will be updated as well.
        //else, posts will be loaded from database.
        // drawer layout instance to toggle the menu icon to open
        // drawer and back button to close drawer
 //       drawerLayout = findViewById(R.id.my_drawer_layout);
 //       actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);
//        navigationView = findViewById(R.id.nav_view);
//        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                int id = item.getItemId();
//                Log.d(TAG, "onOptionsItemSelected nav: id" + id);
//                //handle menu  item clicks
//                if(id == R.id.nav_contact){
//                    startActivity(new Intent(PostByLabelActivity.this,ContactActivity.class));
//
//                    //Toast.makeText(MainActivity.this,"contact us page clicked", Toast.LENGTH_SHORT).show();
//                    return true;
//                }
//
//                return true;
//            }
//        });
//
//        // pass the Open and Close toggle for the drawer layout listener
//        // to toggle the button
//        drawerLayout.addDrawerListener(actionBarDrawerToggle);
//        actionBarDrawerToggle.syncState();
//
//        // to make the Navigation drawer icon always appear on the action bar
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //loadPosts();

        if(isOnline()) {
            //get post id from intent
            label = getIntent().getStringExtra("label");
            Log.d(TAG, "onCreate: " + label);
            loadPosts();
        }
        else{
            progressDialog.show();
            setContentView(R.layout.no_internet_layout);
            retry = findViewById(R.id.retry);
            progressDialog.dismiss();
            retry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                    startActivity(getIntent());
                }
            });
        }
        //load more button

        loadMoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isOnline()) {
                        loadPosts();
                }
                else {
                    progressDialog.show();
                    setContentView(R.layout.no_internet_layout);
                    retry = findViewById(R.id.retry);
                    progressDialog.dismiss();
                    retry.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            finish();
                            startActivity(getIntent());
                        }
                    });                }
            }
        });


    }


    private void loadPosts() {
        progressDialog.show();

        //This will load first time posts.
        if(nextToken.equals("")){
            Log.d(TAG,"loadPosts: Next Page token is empty, no more posts");
            url = "https://www.googleapis.com/blogger/v3/blogs/"
                    + Constants.BLOG_ID
                    +"/posts?labels="+label
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
                    +"/posts?labels="+label
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
                        Toast.makeText(PostByLabelActivity.this,"Reached end of page...",Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(PostByLabelActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();

                        }
                    }

                    //setup adapter
                    adapterPost = new AdapterPost(PostByLabelActivity.this,postArrayList);
                    //set adapter to recylerview
                    postsRv.setAdapter(adapterPost);

                    progressDialog.dismiss();


                }catch (Exception e){

                    Log.d(TAG, "onResponse: 2: " + e.getMessage());
                    Toast.makeText(PostByLabelActivity.this, ""+e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse : " + error.toString());
                Toast.makeText(PostByLabelActivity.this, ""+error.getMessage(),Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();

            }

        });


        //add request to queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //inflate menu
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //get id of clicked menu item
        int id = item.getItemId();
        Log.d(TAG, "onOptionsItemSelected: id" + id);
        //handle menu  item clicks
        if (id == R.id.action_pages){
            startActivity(new Intent(this,PagesActivity.class));
        }
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            Log.d(TAG, "onOptionsItemSelected: item" + item.toString());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

}