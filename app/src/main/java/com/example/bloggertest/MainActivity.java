package com.example.bloggertest;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

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
import java.util.List;

public class MainActivity extends AppCompatActivity {


    //UI VIews
    private RecyclerView postsRv;
    private Button loadMoreBtn;
    private EditText searchEt;
    private ImageButton searchBtn;

    private String url = "";
    private String nextToken = "";
    private boolean isSearch = false;
    private boolean isFirstResult = false;

    private ArrayList<ModelPost> postArrayList;
    private AdapterPost adapterPost;

    private ProgressDialog progressDialog;

    private static final String TAG = "MAIN_TAG";

    private ActionBar actionBar;
    private NavigationView navigationView;

    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        actionBar = getSupportActionBar();
        actionBar.setTitle(Constants.BLOG_NAME);
        actionBar.setSubtitle(Constants.BLOG_SLOGAN);

        postsRv = findViewById(R.id.postsRv);
        loadMoreBtn = findViewById(R.id.loadMoreBtn);
        searchEt = findViewById(R.id.searchEt);
        searchBtn = findViewById(R.id.searchBtn);

        // drawer layout instance to toggle the menu icon to open
        // drawer and back button to close drawer
        drawerLayout = findViewById(R.id.my_drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                Log.d(TAG, "onOptionsItemSelected nav: id" + id);
                Intent intent = new Intent(MainActivity.this,PostByLabelActivity.class);

                //handle menu  item clicks
                   // startActivity(new Intent(MainActivity.this,ContactActivity.class));
                switch (id) {
                    case R.id.label1:
                        intent.putExtra("label", "CSAT");
                        break;

                    case R.id.label2:
                        intent.putExtra("label", "Current Affairs");
                        break;

                    case R.id.label3:
                        intent.putExtra("label", "Economics");
                        break;

                    case R.id.label4:
                        intent.putExtra("label", "English Grammar");
                        break;

                    case R.id.label5:
                        intent.putExtra("label", "Geography");
                        break;

                    case R.id.label6:
                        intent.putExtra("label", "History");
                        break;

                    case R.id.label7:
                        intent.putExtra("label", "Indian Polity");
                        break;

                    case R.id.label8:
                        intent.putExtra("label", "mpsc");
                        break;

                    case R.id.label9:
                        intent.putExtra("label", "Science");
                        break;

                    case R.id.label10:
                        intent.putExtra("label", "upsc");
                        break;

                    case R.id.label11:
                        intent.putExtra("label", "पोलीस भरती");
                        break;

                    case R.id.label12:
                        intent.putExtra("label", "मराठी व्याकरण");
                        break;

                    case R.id.label13:
                        intent.putExtra("label", "समाजसुधारक");
                        break;
                    case R.id.item6:
                        intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/917798653068"));
                        break;
                    case R.id.item7:
                        intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:+917798653068"));
                        break;
                    case R.id.item8:
                        intent = new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:" + "yashacharajmarg@gmail.com"));
                        break;
                }
                startActivity(intent);
                return true;
            }
        });

        // pass the Open and Close toggle for the drawer layout listener
        // to toggle the button
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        // to make the Navigation drawer icon always appear on the action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //setup progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait...");

        //init & clear list before adding data into it
        postArrayList = new ArrayList<>();
        postArrayList.clear();

        //we need to check if network connection is available
        //if available loadposts will be called i.e posts will be fetched by API and then db will be updated as well.
        //else, posts will be loaded from database.


        //loadPosts();

        if(isOnline())
            loadPosts();
        else{
            Toast.makeText(this,"Internet not connected!",Toast.LENGTH_SHORT).show();
            loadPostsFromRoom();
        }
        //load more button

        loadMoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String query = searchEt.getText().toString().trim();
                if (isOnline()) {
                    if (TextUtils.isEmpty(query)) {
                        loadPosts();
                    }
                    else {
                        searchPosts(query);
                    }
                }
                else {
                    Toast.makeText(MainActivity.this, "Internet not connected!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextToken = "";
                url = "";

                postArrayList = new ArrayList<>();
                postArrayList.clear();


                String query = searchEt.getText().toString().trim();
                if (isOnline()) {
                    if (TextUtils.isEmpty(query)) {
                        loadPosts();
                    }
                    else {
                        searchPosts(query);
                    }
                }
                else {
                    Toast.makeText(MainActivity.this, "Internet not connected!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadPostsFromRoom() {
        progressDialog.show();
        ArrayList<ModelPost> localPostArrayList = new ArrayList<>();
        AppDataBase db = AppDataBase.getDbInstance(this.getApplicationContext());
        List<ModelPost> modelPostList = db.postDAO().getPosts();
        localPostArrayList.addAll(modelPostList);
        if(localPostArrayList.size() >  1 ) {
            adapterPost = new AdapterPost(MainActivity.this, localPostArrayList);
            //set adapter to recylerview
            postsRv.setAdapter(adapterPost);
        }
        else
            Toast.makeText(this, "No Posts to fetch!", Toast.LENGTH_SHORT).show();

        progressDialog.dismiss();

    }

    private void searchPosts(String query) {
        isSearch = true;
        Log.d(TAG, "searchPosts: isSearch:" + isSearch);

        progressDialog.show();

        if(nextToken.equals("")){

            Log.d(TAG,"searchPosts: Next Page token is empty, no more posts");
            url = "https://www.googleapis.com/blogger/v3/blogs/"
                    + Constants.BLOG_ID
                    +"/posts/search?q="+query
                    +"&key="+ Constants.API_KEY;
        }
        else if (nextToken.equals("end")){
            Log.d(TAG,"searchPosts: Next token is empty/end, no more posts");
            Toast.makeText(this,"No More Posts...",Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            return;
        }
        else {
            Log.d(TAG,"searchPosts: next token: " + nextToken);
            url = "https://www.googleapis.com/blogger/v3/blogs/"
                    + Constants.BLOG_ID
                    +"/posts/search?q="+query
                    +"&pageToken=" + nextToken
                    +"&key="+ Constants.API_KEY;
        }
        Log.d(TAG,"searchPosts: next URL: " + url);

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

    private void loadPosts() {
        isSearch = false;
        Log.d(TAG, "loadPosts: isSearch: "+isSearch);
        progressDialog.show();

        //This will load first time posts.
        if(nextToken.equals("")){
            isFirstResult = true;
            Log.d(TAG,"loadPosts: Next Page token is empty, no more posts");
            url = "https://www.googleapis.com/blogger/v3/blogs/"
                    + Constants.BLOG_ID
                    +"/posts?maxResults="+Constants.MAX_POST_RESULTS
                    +"&key="+ Constants.API_KEY;
        }
        else if (nextToken.equals("end")){
            isFirstResult = false;
            Log.d(TAG,"loadPosts: Next token is empty/end, no more posts");
            Toast.makeText(this,"No More Posts...",Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            return;
        }
        else {
            isFirstResult = false;
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

                    //adding this to database
                    if(isFirstResult){
                        AppDataBase db = AppDataBase.getDbInstance(MainActivity.this.getApplicationContext());
                        //deleting existing entries from table
                        db.postDAO().deleteAll();
                        //inserting new records
                        db.postDAO().insertPosts(postArrayList);
                    }
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

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else
            super.onBackPressed();
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
        if(id == R.id.nav_contact){
            startActivity(new Intent(this,ContactActivity.class));
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