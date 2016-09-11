package com.talkingandroid.hour20application;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.util.Log;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


public class PhotoService extends IntentService {
    public static final String ACTION_SUCCESS = "com.talkingandroid.hour20application.action.SUCCEED";
    public static final String ACTION_FAIL = "com.talkingandroid.hour20application.action.FAIL";
    public static final String EXTRA_MESSAGE = "com.talkingandroid.hour20application.extra.MESSAGE";
//    public final static String API_KEY ="---ADD YOUR KEY HERE---";
    public final static String API_KEY ="37ad78fdb433e497de195f7c452ad8af";


    public final static String NUM_PHOTOS ="12";

    public PhotoService() {
        super("PhotoService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            try {
                HttpURLConnection connection = null;
                URL dataUrl = new URL("https://api.flickr.com/services/rest/?method=flickr.photos.getRecent&api_key=" + API_KEY + "&per_page=" + NUM_PHOTOS + "&format=json&nojsoncallback=1");
                connection = (HttpURLConnection) dataUrl.openConnection();
                connection.connect();
                int status = 0;
                status = connection.getResponseCode();
                Log.d("connection", "status " + status);
                if (status == 200) {
                    InputStream is = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                    String responseString;
                    StringBuilder sb = new StringBuilder();
                    while ((responseString = reader.readLine()) != null) {
                        sb = sb.append(responseString);
                    }
                    String photoData = sb.toString();
                    ArrayList<FlickrPhoto> photos = FlickrPhoto.makePhotoList(photoData);
                    for (FlickrPhoto currentPhoto : photos) {
                        ContentValues newValues = new ContentValues();
                        if (currentPhoto.id != null){
                            newValues.put(FlickrPhotoDbAdapter.FLICKR_ID, currentPhoto.id);
                        }
                        newValues.put(FlickrPhotoDbAdapter.OWNER, currentPhoto.owner);
                        newValues.put(FlickrPhotoDbAdapter.SECRET, currentPhoto.secret);
                        newValues.put(FlickrPhotoDbAdapter.SERVER, currentPhoto.server);
                        newValues.put(FlickrPhotoDbAdapter.FARM, currentPhoto.farm);
                        newValues.put(FlickrPhotoDbAdapter.TITLE, currentPhoto.title);
                        newValues.put(FlickrPhotoDbAdapter.IS_PUBLIC, currentPhoto.isPublic);
                        newValues.put(FlickrPhotoDbAdapter.IS_FRIEND, currentPhoto.isFriend);
                        newValues.put(FlickrPhotoDbAdapter.IS_FAMILY, currentPhoto.isFamily);
                        newValues.put(FlickrPhotoDbAdapter.IS_FAVORITE, currentPhoto.isFavorite);
                        newValues.put(FlickrPhotoDbAdapter.LARGE_IMAGE, currentPhoto.largeImage);
                        newValues.put(FlickrPhotoDbAdapter.SMALL_IMAGE, currentPhoto.smallImage);
                        try{
                            getContentResolver().insert(FlickrPhotoContentProvider.CONTENT_URI, newValues);
                        } catch (Exception e){
                            e.printStackTrace();
                        }
                    }


                    Intent broadcastIntent = new Intent();
                    broadcastIntent.setAction(ACTION_SUCCESS);
                    broadcastIntent.putExtra(EXTRA_MESSAGE, photos.size() + " photos inserted");
                    sendBroadcast(broadcastIntent);

                }
            } catch (IOException e) {
                e.printStackTrace();
                Intent broadcastIntent = new Intent();
                broadcastIntent.setAction(ACTION_FAIL);
                broadcastIntent.putExtra(EXTRA_MESSAGE, "FAILED TO LOAD DATA");
                sendBroadcast(broadcastIntent);
            } catch (JSONException e) {
                e.printStackTrace();
                Intent broadcastIntent = new Intent();
                broadcastIntent.setAction(ACTION_FAIL);
                broadcastIntent.putExtra(EXTRA_MESSAGE, "FAILED TO PARSE JSON");
                sendBroadcast(broadcastIntent);
            }
        }

    }
}
