package com.example.mobilesoftwareproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class FoodDBManager extends SQLiteOpenHelper {
    public static final String FOOD_DB = "food.db";
    public static final int DB_VERSION = 1;

    public static final String TABLE_NAME = "food";
    public static final String COL_ID = "_id";
    public static final String COL_LOCATION = "location";
    public static final String COL_FOOD_NAME = "food_name";
    public static final String COL_BEVERAGE_NAME = "beverage_name";
    public static final String COL_IMPRESSIONS = "impressions";
    public static final String COL_TIME = "time";
    public static final String COL_COST = "cost";

    private static FoodDBManager foodDBManager = null;
    Context context = null;

    public static FoodDBManager getInstance(Context context) {
        foodDBManager = new FoodDBManager(context, FOOD_DB, null, DB_VERSION);
        return foodDBManager;
    }
    public FoodDBManager(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context = context;
    }

    // Create the table
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table " + TABLE_NAME + " ( " +
                COL_ID + " integer primary key autoincrement, " +
                COL_LOCATION + " text, " +
                COL_FOOD_NAME + " text, " +
                COL_BEVERAGE_NAME + " text, " +
                COL_IMPRESSIONS + " text, " +
                COL_TIME + " text, " +
                COL_COST + " text )";
        db.execSQL(sql);
    }

    // Upgrade the table
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "drop table if exists " + TABLE_NAME;
        db.execSQL(sql);

        onCreate(db);
    }

    public long insert(ContentValues addRowValue) {
        SQLiteDatabase db = getWritableDatabase();
        return db.insert(TABLE_NAME, null, addRowValue);
    }

    public Cursor query(String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
        return getReadableDatabase().query(TABLE_NAME, columns, selection, selectionArgs, groupBy, having, orderBy);
    }
}
