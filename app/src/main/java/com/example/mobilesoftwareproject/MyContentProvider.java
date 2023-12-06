package com.example.mobilesoftwareproject;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

public class MyContentProvider extends ContentProvider {

    // Authority는 ContentProvider를 식별하는 데 사용됩니다.
    public static final String AUTHORITY = "com.example.MyContentProvider";

    // URI를 사용하여 ContentProvider에 대한 접근을 식별합니다.
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/data");

    // ContentProvider에 대한 MIME 유형 정의
    private static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/data";
    private static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/data";

    // 데이터베이스와 관련된 상수 정의
    private static final String DATABASE_NAME = "mydatabase";
    private static final String DATABASE_TABLE = "mytable";
    private static final int DATABASE_VERSION = 4;

    // 데이터베이스 테이블의 열 이름 정의
    public static final String LOCATION = "location";
    public static final String TYPE = "type";
    public static final String FOOD_NAME = "food_name";
    public static final String IMPRESSIONS = "impressions";
    public static final String TIME = "time";
    public static final String COST = "cost";
    public static final String PHOTO = "photo";
    public static final String ID = "_id";
    public static final String CALORIE ="calorie";

    // SQL 문 정의
    private static final String DATABASE_CREATE =
            "CREATE TABLE " + DATABASE_TABLE + " (" +
                    ID +" INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    LOCATION + " TEXT, " +
                    TYPE + " TEXT," +
                    FOOD_NAME + " TEXT, " +
                    CALORIE + " INT, " +
                    IMPRESSIONS + " TEXT, " +
                    TIME + " TEXT, " +
                    COST + " INT," +
                    PHOTO + " BLOB);";

    // UriMatcher를 사용하여 URI를 기반으로 ContentProvider에 대한 작업 식별
    private static final UriMatcher sUriMatcher;
    private static final int DATA = 1;
    private static final int DATA_ID = 2;

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(AUTHORITY, "data", DATA);
        sUriMatcher.addURI(AUTHORITY, "data/#", DATA_ID);
    }

    // SQLiteOpenHelper를 사용하여 데이터베이스를 관리
    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // 버전이 변경될 때마다 호출되는 메서드입니다.
            // 여기에서는 기존 테이블을 삭제하고, 새로운 테이블을 생성하거나 업그레이드 로직을 수행합니다.
            // 필요에 따라서는 기존 데이터의 백업 및 복원 작업도 수행할 수 있습니다.

            Log.w(DatabaseHelper.class.getName(), "Upgrading database from version " + oldVersion + " to " + newVersion);

            // 테이블 삭제
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);

            // 새로운 테이블 생성
            db.execSQL(DATABASE_CREATE);

            // 여러 버전의 업그레이드 로직이 필요한 경우, switch 문 등을 사용하여 추가할 수 있습니다.
        }
    }

    private DatabaseHelper dbHelper;

    @Override
    public boolean onCreate() {
        // ContentProvider가 생성되었을 때 호출됩니다.
        dbHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long rowId = db.insert(DATABASE_TABLE, null, values);
        if (rowId > 0) {
            Uri insertedUri = ContentUris.withAppendedId(CONTENT_URI, rowId);
            getContext().getContentResolver().notifyChange(insertedUri, null);
            return insertedUri;
        }
        throw new SQLException("Failed to insert row into " + uri);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) {
            case DATA:
                count = db.delete(DATABASE_TABLE, selection, selectionArgs);
                break;
            case DATA_ID:
                String rowId = uri.getPathSegments().get(1);
                count = db.delete(DATABASE_TABLE, "_id=" + rowId +
                        (!selection.isEmpty() ? " AND (" + selection + ')' : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) {
            case DATA:
                count = db.update(DATABASE_TABLE, values, selection, selectionArgs);
                break;
            case DATA_ID:
                String rowId = uri.getPathSegments().get(1);
                count = db.update(DATABASE_TABLE, values, "_id=" + rowId +
                        (!selection.isEmpty() ? " AND (" + selection + ')' : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor;
        switch (sUriMatcher.match(uri)) {
            case DATA:
                cursor = db.query(DATABASE_TABLE, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case DATA_ID:
                String rowId = uri.getPathSegments().get(1);
                cursor = db.query(DATABASE_TABLE, projection, "_id=" + rowId +
                        (!selection.isEmpty() ? " AND (" + selection + ')' : ""), selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case DATA:
                return CONTENT_TYPE;
            case DATA_ID:
                return CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }
    public Cursor queryDataByMealAndTime(String selectedMeal, String selectedTime, String[] projection, String sortOrder) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selection = TYPE + "=? AND " + TIME + "=?";
        String[] selectionArgs = new String[]{selectedMeal, selectedTime};
        return db.query(DATABASE_TABLE, projection, selection, selectionArgs, null, null, sortOrder);
    }
}
