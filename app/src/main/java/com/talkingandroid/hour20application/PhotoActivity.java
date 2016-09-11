package com.talkingandroid.hour20application;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;

public class PhotoActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor> {
    PhotoCursorAdapter adapter;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        listView = (ListView) findViewById(R.id.photoListView);
        getLoaderManager().initLoader(0, null, this);
        adapter = new PhotoCursorAdapter(this, null, 0);
        listView.setAdapter(adapter);
        Intent intent = new Intent(getApplicationContext(), PhotoService.class);
        startService(intent);

    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader loader;

        loader = new CursorLoader(this,
                FlickrPhotoContentProvider.CONTENT_URI,
                null, null, null, null);
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }


    public class PhotoCursorAdapter extends CursorAdapter {
        Context mContext;
        public PhotoCursorAdapter(Context context, Cursor c, int flags) {
            super(context, c, flags);
            mContext = context;
        }

        @Override
        public void bindView(View v, Context context, Cursor c) {
            ViewHolder vh = (ViewHolder) v.getTag();
            if (vh == null) {
                vh = new ViewHolder();
                vh.photoImageView =  (ImageView) v.findViewById(R.id.photoImageView);
                vh.textView = (TextView) v.findViewById(R.id.nameTextView);
            }

            FlickrPhoto currentPhoto = FlickrPhotoDbAdapter.getPhotoFromCursor(c);
            vh.id = currentPhoto.id;
            v.setTag(vh);
            vh.textView.setText(currentPhoto.title);
            Picasso.with(mContext).load(currentPhoto.smallImage).into( vh.photoImageView);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            LayoutInflater li = (LayoutInflater) mContext.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            View v = li.inflate(R.layout.photo_view_item, parent, false);
            return (v);

        }

        class ViewHolder {
            String id;
            ImageView photoImageView;
            TextView textView;
        }
    }
}
