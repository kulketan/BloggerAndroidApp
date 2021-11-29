package com.example.bloggertest;

import static javax.xml.transform.OutputKeys.ENCODING;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class PostDetailsActivity extends AppCompatActivity {

    //UI Views
    private TextView titleTv,publishInfoTv;
    private WebView webView;
    private RecyclerView labelsRv;

    private Button retry;

    private String postId;
    private static final String TAG = "POST_DETAILS_TAG";

    private ArrayList<ModelLabel> labelArrayList;
    private AdapterLabel adapterLabel;

    //actionbar
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //init actionbar
        actionBar = getSupportActionBar();
        actionBar.setTitle(Constants.BLOG_NAME);
        actionBar.setSubtitle(Constants.BLOG_SLOGAN);
        //add back button
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        if (isOnline()){
            setContentView(R.layout.activity_post_details);

            //init ui views
            titleTv = findViewById(R.id.titleTv);
            publishInfoTv = findViewById(R.id.publishInfoTv);
            webView = findViewById(R.id.webView);
            labelsRv = findViewById(R.id.labelsRv);


            //get post id from intent
            postId = getIntent().getStringExtra("postId");
            Log.d(TAG, "onCreate: " + postId);

            //setup webview
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setDomStorageEnabled(true);
            webView.setWebViewClient(new WebViewClient());
            webView.setWebChromeClient(new WebChromeClient());

            loadPostDetails();
        }
        else{
            setContentView(R.layout.no_internet_layout);
            retry = findViewById(R.id.retry);

            retry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                    startActivity(getIntent());
                }
            });
        }
}

    private void loadPostDetails() {
        String url = "https://www.googleapis.com/blogger/v3/blogs/"+ Constants.BLOG_ID
                +"/posts/"+postId
                +"?key=" + Constants.API_KEY;
        Log.d(TAG, "loadPostDetails: URL" + url);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                //successfully recieved response
                Log.d(TAG, "onResponse: " + response);
                //response is json object
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    //get data

                    String title = jsonObject.getString("title");
                    String published = jsonObject.getString("published");
                    String content = jsonObject.getString("content");
                    String url = jsonObject.getString("url");
                    String displayName = jsonObject.getJSONObject("author").getString("displayName");

                    String gmtDate = published;
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                    SimpleDateFormat dateFormat2 = new SimpleDateFormat("dd/MM/yyyy K:mm a"); //e.g 25/10/2021 02:12 PM
                    String formattedDate = "";
                    try{
                        Date date = dateFormat.parse(gmtDate);
                        formattedDate = dateFormat2.format(date);
                    }catch (Exception e){
                        formattedDate = published;
                        e.printStackTrace();
                    }

                    //set data
                    actionBar.setSubtitle(title);
                    titleTv.setText(title);
                    publishInfoTv.setText("By " + displayName + " " + formattedDate);
                    //content contains web page like html, so load in webview
                    webView.loadDataWithBaseURL(null, content,"text/html",ENCODING,null);

                    //get labels of post
                    try {
                        labelArrayList = new ArrayList<>();
                        labelArrayList.clear();

                        //json array of labels
                        JSONArray jsonArray = jsonObject.getJSONArray("labels");
                        for (int i=0; i<jsonArray.length();i++){
                            String label = jsonArray.getString(i);
                            //add label in model
                            ModelLabel modelLabel = new ModelLabel(label);

                            labelArrayList.add(modelLabel);
                        }
                        //setup adapter
                        adapterLabel = new AdapterLabel(PostDetailsActivity.this,labelArrayList);
                        //set adapter to reclerview
                        labelsRv.setAdapter(adapterLabel);



                    }catch (Exception e){
                        Log.d(TAG, "onResponse: "+e.getMessage());

                    }
                }catch (Exception e){
                    Log.d(TAG, "onResponse: "+e.getMessage());
                    Toast.makeText(PostDetailsActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                    //failed to retrive post show error
                Toast.makeText(PostDetailsActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        //add request to queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); //go to previous activity, when back button of actionbar clicked
        return super.onSupportNavigateUp();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.post_share_menu, menu);

        // first parameter is the file for icon and second one is menu
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.share) {
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);

            // type of the content to be shared
            sharingIntent.setType("text/plain");

            // Body of the content
            String shareBody = getIntent().getStringExtra("url");
            Log.d(TAG, "onOptionsItemSelected: url():" + getIntent().getStringExtra("url"));
            // subject of the content. you can share anything
            String shareSubject = getIntent().getStringExtra("title");
            Log.d(TAG, "onOptionsItemSelected: Title():" + getIntent().getStringExtra("title"));

            // passing body of the content
            sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);

            // passing subject of the content
            sharingIntent.putExtra(Intent.EXTRA_TITLE, shareSubject);
           // Uri uri = Uri.parse("https://i.ibb.co/jWSxjyH/Whats-App-Image-2021-11-28-at-11-33-44.jpg");
          //  sharingIntent.setData(uri);

            startActivity(Intent.createChooser(sharingIntent, "Share to"));

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