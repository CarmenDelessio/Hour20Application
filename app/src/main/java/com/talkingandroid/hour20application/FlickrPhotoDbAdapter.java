package com.talkingandroid.hour20application;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class FlickrPhotoDbAdapter {

    public static final String KEY_ROWID = "_id";
    public static final String FLICKR_ID = "flickrId";
    public static final String OWNER = "owner";
    public static final String SECRET = "secret";
    public static final String SERVER = "server";
    public static final String FARM = "farm";
    public static final String TITLE = "title";
    public static final String IS_PUBLIC = "isPublic";
    public static final String IS_FRIEND = "isFriend";
    public static final String IS_FAMILY = "isFamily";
    public static final String IS_FAVORITE = "isFavorite";
    public static final String LARGE_IMAGE = "largeImage";
    public static final String SMALL_IMAGE = "smallImage";


    public static final String[] FLICKR_PHOTO_FIELDS = new String[] {
            KEY_ROWID,
            FLICKR_ID,
            OWNER,
            SECRET,
            SERVER,
            FARM,
            TITLE,
            IS_PUBLIC,
            IS_FRIEND,
            IS_FAMILY,
            IS_FAVORITE,
            LARGE_IMAGE,
            SMALL_IMAGE
    };


    private DatabaseHelper mDbHelper;
    SQLiteDatabase mDb;

    private static final String CREATE_PHOTO_TABLE =
            "create table photo (" +  KEY_ROWID +" INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + FLICKR_ID +" not null,"
                    + OWNER +" text,"
                    + SECRET + " text,"
                    + SERVER + " text,"
                    + FARM + " text,"
                    + TITLE + " text,"
                    + IS_PUBLIC + " INTEGER,"
                    + IS_FRIEND + " INTEGER,"
                    + IS_FAMILY + " INTEGER,"
                    + IS_FAVORITE + " INTEGER,"
                    + LARGE_IMAGE + " text,"
                    + SMALL_IMAGE + " text"
                    +");";





    private final Context mCtx;
    public static String TAG = FlickrPhotoDbAdapter.class.getSimpleName();
    static final String DATABASE_NAME = "flickr_photos_db";
    static final String PHOTO_TABLE = "photo";
    private static final int DATABASE_VERSION = 2;

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_PHOTO_TABLE);
        }
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS "+ PHOTO_TABLE);
            onCreate(db);
        }
    }


    public FlickrPhotoDbAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    public FlickrPhotoDbAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }
    public void close() {
        if(mDbHelper!=null){
            mDbHelper.close();
        }
    }
    public void upgrade() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx); //open
        mDb = mDbHelper.getWritableDatabase();
        mDbHelper.onUpgrade(mDb, 1, 0);
    }


    public long insertPhoto(ContentValues initialValues) {
        return mDb.insert(PHOTO_TABLE, null, initialValues);
    }

    public int updatePhoto(String selection, String[] selectionArgs, ContentValues newValues) {
        return mDb.update(PHOTO_TABLE, newValues, selection, selectionArgs );
    }


    public int deletePhoto(String selection, String[] selectionArgs) {
        return mDb.delete(PHOTO_TABLE,  selection, selectionArgs );
    }

    public Cursor getPhoto(int id) {
        String[] selectionArgs = {String.valueOf(id)};
        return mDb.query(PHOTO_TABLE, FLICKR_PHOTO_FIELDS,  KEY_ROWID + "=?", selectionArgs, null, null, null);

    }

    public Cursor getPhotos() {
        return mDb.query(PHOTO_TABLE, FLICKR_PHOTO_FIELDS, null, null, null, null, null);
    }

    public Cursor queryPhotos(String[] projection, String selection,
                            String[] selectionArgs, String sortOrder) {

        return mDb.query(PHOTO_TABLE, projection, selection,
                selectionArgs, null, null, sortOrder);
    }


    public static FlickrPhoto getPhotoFromCursor(Cursor cursor){
        FlickrPhoto photo = new FlickrPhoto();
        photo.id = cursor.getString(cursor.getColumnIndex(FLICKR_ID));
        photo.owner = cursor.getString(cursor.getColumnIndex(OWNER));
        photo.secret = cursor.getString(cursor.getColumnIndex(SECRET));
        photo.server = cursor.getString(cursor.getColumnIndex(SERVER));
        photo.farm = cursor.getString(cursor.getColumnIndex(FARM));
        photo.title = cursor.getString(cursor.getColumnIndex(TITLE));
        photo.isPublic = (cursor.getInt(cursor.getColumnIndex(IS_PUBLIC)) == 1);
        photo.isFriend = (cursor.getInt(cursor.getColumnIndex(IS_FRIEND)) == 1);
        photo.isFamily = (cursor.getInt(cursor.getColumnIndex(IS_FAMILY)) == 1);
        photo.isFavorite = (cursor.getInt(cursor.getColumnIndex(IS_FAVORITE)) == 1);
        photo.largeImage = cursor.getString(cursor.getColumnIndex(LARGE_IMAGE));
        photo.smallImage = cursor.getString(cursor.getColumnIndex(SMALL_IMAGE));
        return(photo);
    }

}

