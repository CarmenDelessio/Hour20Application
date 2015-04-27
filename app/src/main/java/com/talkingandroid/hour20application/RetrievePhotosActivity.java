package com.talkingandroid.hour20application;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class RetrievePhotosActivity extends Activity {

    RetrievePhotoReceiver photoReceiver = new RetrievePhotoReceiver();
    TextView resultsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrieve_photos);
        resultsTextView = (TextView) findViewById(R.id.textView);
        Intent retrieveIntent = new Intent(getApplicationContext(), RetrievePhotoService.class);
        startService(retrieveIntent);
    }

    @Override
    protected void onResume (){
        super.onResume();
        registerReceiver(photoReceiver, new IntentFilter(RetrievePhotoService.ACTION_FAIL));
        registerReceiver(photoReceiver, new IntentFilter(RetrievePhotoService.ACTION_RECEIVE));
    }

    protected void onPause (){
        super.onPause();
        unregisterReceiver(photoReceiver);
    }

    public class RetrievePhotoReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(RetrievePhotoService.ACTION_FAIL)){
                String message = intent.getExtras().getString(RetrievePhotoService.EXTRA_MESSAGE);
                resultsTextView.setText( message);
            }else if(intent.getAction().equals(RetrievePhotoService.ACTION_RECEIVE)){
                String message = intent.getExtras().getString(RetrievePhotoService.EXTRA_MESSAGE);
                resultsTextView.setText( message);
            }
        }
    }

}
