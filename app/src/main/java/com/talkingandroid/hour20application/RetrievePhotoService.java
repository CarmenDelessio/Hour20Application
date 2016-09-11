package com.talkingandroid.hour20application;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class RetrievePhotoService extends IntentService {
    public static final String ACTION_RECEIVE = "com.talkingandroid.hour20application.action.RECEIVE";
    public static final String ACTION_FAIL = "com.talkingandroid.hour20application.action.FAIL";
    public static final String EXTRA_MESSAGE = "com.talkingandroid.hour20application.extra.MESSAGE";
    public final static String API_KEY ="37ad78fdb433e497de195f7c452ad8af";
    public final static String NUM_PHOTOS ="100";

    public RetrievePhotoService() {
        super("RetrievePhotoService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            try {
                HttpURLConnection connection = null;
                URL dataUrl = new URL("https://api.flickr.com/services/rest/?method=flickr.photos.getRecent&api_key="+ API_KEY+ "&per_page=" + NUM_PHOTOS +"&format=json&nojsoncallback=1");
                connection = (HttpURLConnection) dataUrl.openConnection();
                connection.connect();
                int status = 0;
                status = connection.getResponseCode();
                Log.d("connection", "status " + status);
                if (status ==200) {
                    InputStream is = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                    String responseString;
                    StringBuilder sb = new StringBuilder();
                    while ((responseString = reader.readLine()) != null) {
                        sb = sb.append(responseString);
                    }
                    String photoData = sb.toString();

                    Intent broadcastIntent = new Intent();
                    broadcastIntent.setAction(ACTION_RECEIVE);
                    broadcastIntent.putExtra(EXTRA_MESSAGE, photoData);
                    sendBroadcast(broadcastIntent);

                }
            } catch (IOException e) {
                Intent broadcastIntent = new Intent();
                broadcastIntent.setAction(ACTION_FAIL);
                broadcastIntent.putExtra(EXTRA_MESSAGE, "FAILED TO RETRIEVE DATA");
                sendBroadcast(broadcastIntent);
                e.printStackTrace();
            }



        }
    }


}
