package com.example.mobilesoftwareproject;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

public class MyContentProvider extends ContentProvider {
    static final String PROVIDER_NAME = "com.example.mobilesoftwareproject.MyContentProvider";
    static final String URL = "content://" + PROVIDER_NAME + "/food";
    static final Uri CONTENT_URI = Uri.parse(URL);

    static final String _ID = "_id";
    static final String LOCATION = "location";
    static final String FOOD_NAME = "food_name";
    static final String BEVERAGE_NAME = "beverage_name";
    static final String IMPRESSIONS = "impressions";
    static final String TIME = "time";
    static final String COST = "cost";

    public FoodDBManager foodDBManager;

    public MyContentProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
//        return foodDBManager.delete(selection, selectionArgs);
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long rowID = foodDBManager.insert(values);
        return null;
    }

    @Override
    public boolean onCreate() {
        foodDBManager = FoodDBManager.getInstance(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        return foodDBManager.query(projection, selection, selectionArgs, null, null, sortOrder);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}