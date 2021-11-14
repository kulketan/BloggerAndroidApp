package com.example.bloggertest;

import static javax.xml.transform.OutputKeys.ENCODING;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PostDetailsActivity extends AppCompatActivity {

    //UI Views
    private TextView titleTv,publishInfoTv;
    private WebView webView;

    private String postId;
    private static final String TAG = "POST_DETAILS_TAG";

    //actionbar
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);

        //init actionbar
        actionBar = getSupportActionBar();
        actionBar.setTitle("Post Details");
        //add back button
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        //init ui views
        titleTv = findViewById(R.id.titleTv);
        publishInfoTv = findViewById(R.id.publishInfoTv);
        webView = findViewById(R.id.webView);

        //get post id from intent
        postId = getIntent().getStringExtra("postId");
        Log.d(TAG,"onCreate: " + postId);

        //setup webview
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());

        loadPostDetails();
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
                    String dislayName = jsonObject.getJSONObject("author").getString("displayName");

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
                    publishInfoTv.setText("By " + dislayName + " " + formattedDate);
                    //content contains web page like html, so load in webview
                    webView.loadDataWithBaseURL(null, content,"text/html",ENCODING,null);



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
        onBackPressed(); //go to previous activity, when back utton of actionbar clicked
        return super.onSupportNavigateUp();
    }
}