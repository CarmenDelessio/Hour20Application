package com.talkingandroid.hour20application;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;

public class FlickrPhotoContentProvider extends ContentProvider {
    FlickrPhotoDbAdapter mFlickrPhotoDbAdapter;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sUriMatcher.addURI("com.talkingandroid.hour20application.provider", "photo", 1);
        sUriMatcher.addURI("com.talkingandroid.hour20application.provider/#", "photo", 2);
    }

    public static final Uri CONTENT_URI = Uri.parse("content://com.talkingandroid.hour20application.provider/photo");



    public FlickrPhotoContentProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return mFlickrPhotoDbAdapter.deletePhoto(selection, selectionArgs);

    }

    @Override
    public String getType(Uri uri) {
        int uriType = sUriMatcher.match(uri);
        switch (uriType) {
            case 1:
                return ContentResolver.CURSOR_DIR_BASE_TYPE + "/com.talkingandroid.hour20application.FlickrPhoto";
            case 2:
                return ContentResolver.CURSOR_ITEM_BASE_TYPE + "/com.talkingandroid.hour20application.FlickrPhoto";
            default:
                return null;
        }

    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long id = mFlickrPhotoDbAdapter.insertPhoto(values);
        if (id > 0) {
            Uri newUri = ContentUris.withAppendedId(uri, id);

            return newUri;
        }
        else {
            throw new SQLException("Failed to insert row into " + uri);
        }

    }

    @Override
    public boolean onCreate() {
        mFlickrPhotoDbAdapter = new FlickrPhotoDbAdapter(getContext());
        mFlickrPhotoDbAdapter.open();
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Cursor cursor;
        int uriType = sUriMatcher.match(uri);
        switch (uriType) {
            case 1:
                cursor =  mFlickrPhotoDbAdapter.queryPhotos(projection, selection,
                        selectionArgs, sortOrder);
                break;
            case 2:
                cursor =  mFlickrPhotoDbAdapter.getPhoto(Integer.valueOf(uri.getLastPathSegment()));
                break;
            default:
                throw new IllegalArgumentException("Unknown URI");
        }
        return cursor;


    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        return mFlickrPhotoDbAdapter.updatePhoto(selection, selectionArgs, values);


    }
}
