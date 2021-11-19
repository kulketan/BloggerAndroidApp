package com.example.bloggertest;

import android.app.Application;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.onesignal.OSNotificationOpenedResult;
import com.onesignal.OneSignal;

import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;

public class ApplicationClass extends Application {
    private static final String ONESIGNAL_APP_ID = "9759070b-0fb6-47a0-97b5-374e1405da69";
    private static final String TAG = "APPLICATION_TAG";
    ArrayList<String> id = new ArrayList<>();
    @Override
    public void onCreate() {
        super.onCreate();

        // Enable verbose OneSignal logging to debug issues if needed.
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);
        // OneSignal Initialization
        OneSignal.setNotificationOpenedHandler(new NotificationHandler());
        OneSignal.initWithContext(this);
        OneSignal.setAppId(ONESIGNAL_APP_ID);

    }
    class NotificationHandler implements OneSignal.OSNotificationOpenedHandler{
        @Override
        public void notificationOpened(OSNotificationOpenedResult result) {
            Intent intent;
            String launchUrl = result.getNotification().getLaunchURL();
            Log.i("OneSignalExample", "launchUrl set with value: " + launchUrl);
            if (launchUrl != null) {
                // The following can be used to open an Activity of your choice.
                // Replace - getApplicationContext() - with any Android Context.
                // Replace - YOURACTIVITY.class with your activity to deep link
                String postId = getPostId(launchUrl).get(0);
                if(!postId.equals("")) {
                    intent = new Intent(getApplicationContext(), PostDetailsActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("postId", postId);
                    Log.i("OneSignalExample", "openURL = " + launchUrl);
                }
                else{
                    intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                    Log.d(TAG, "notificationOpened: invalid url hence redirecting to mainactivity.");
                }
                startActivity(intent);
            }

        }
    }

    private ArrayList<String> getPostId(String launchUrl) {
        //String postId = "";
        try {
            URL url = new URL(launchUrl);
            String path = url.getPath();

            String url1 = "https://www.googleapis.com/blogger/v3/blogs/"+ Constants.BLOG_ID
                    +"/posts/bypath?path="+path
                    +"&key=" + Constants.API_KEY;
            Log.d(TAG, "loadPostDetails: URL" + url1);

            StringRequest stringRequest = new StringRequest(Request.Method.GET, url1, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    //successfully recieved response
                    Log.d(TAG, "onResponse: " + response);
                    //response is json object
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        //get data
                         id.add(jsonObject.getString("id"));
                    }catch (Exception e){
                        Log.d(TAG, "onResponse: "+e.getMessage());
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //failed to retrive post show error
                    Log.d(TAG, "onResponse: "+error.getMessage());

                    //Toast.makeText(PostDetailsActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

            //add request to queue
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);

        }catch (Exception e){
            Log.d(TAG, "getPostId: exception" + e.getMessage());
            Toast.makeText(this, "Invalid URL, Redirecting to home", Toast.LENGTH_SHORT).show();
        }
        return id;
    }
}
