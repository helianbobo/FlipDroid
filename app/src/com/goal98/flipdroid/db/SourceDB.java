package com.goal98.flipdroid.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.provider.BaseColumns;
import android.util.Log;
import com.goal98.flipdroid.util.Constants;

import java.util.HashMap;
import java.util.Map;

public class SourceDB {

    public static final String TABLE_NAME = "source";
    public static final String KEY_SOURCE_NAME = "source_name";
    public static final String KEY_SOURCE_DESC = "source_desc";
    public static final String KEY_SOURCE_ID = "source_id";
    public static final String KEY_IMAGE_URL = "image_url";
    public static final String KEY_ACCOUNT_TYPE = "account_type";

    private SQLiteOpenHelper sourcehelper;

    public SourceDB(Context context) {
        sourcehelper = new SourceOpenHelper(context);
    }

    public static Map<String, String> buildSource(String accountType,String name, String id, String desc, String imageUrl){
        Map<String, String> result = new HashMap<String, String>();
        result.put(KEY_SOURCE_NAME, name);
        result.put(KEY_SOURCE_ID, id);
        result.put(KEY_SOURCE_DESC, desc);
        result.put(KEY_ACCOUNT_TYPE, accountType);
        result.put(KEY_IMAGE_URL, imageUrl);
        return  result;
    }

    public int deleteAll() {

        SQLiteDatabase db = sourcehelper.getWritableDatabase();
        return db.delete(TABLE_NAME, null, null);
    }

    public Cursor query(String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(TABLE_NAME);

        SQLiteDatabase db = sourcehelper.getReadableDatabase();
        Cursor cursor = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);

        return cursor;
    }

    public Cursor findAll(){
        String[] projection = null;
        String selection = null;
        String[] selectionArgs = null;

        Cursor cursor = query(projection, selection, selectionArgs, null);
        return cursor;
    }

    public long insert(ContentValues values) {
        SQLiteDatabase db = sourcehelper.getWritableDatabase();
        return db.insert(TABLE_NAME, KEY_ACCOUNT_TYPE, values);
    }

    public long insert(Map<String, String> source) {
        ContentValues values = new ContentValues();
        values.put(KEY_SOURCE_NAME, source.get(KEY_SOURCE_NAME));
        values.put(KEY_ACCOUNT_TYPE, source.get(KEY_ACCOUNT_TYPE));
        values.put(KEY_SOURCE_ID, source.get(KEY_SOURCE_ID));
        values.put(KEY_IMAGE_URL, source.get(KEY_IMAGE_URL));
        values.put(KEY_SOURCE_DESC, source.get(KEY_SOURCE_DESC));
        return insert(values);
    }

    public long insert(String accountType, String sourceName, String sourceId, String sourceDesc) {
        ContentValues values = new ContentValues();
        values.put(KEY_SOURCE_NAME, sourceName);
        values.put(KEY_ACCOUNT_TYPE, accountType);
        values.put(KEY_SOURCE_ID, sourceId);
        values.put(KEY_SOURCE_DESC, sourceDesc);
        return insert(values);
    }

    public int update(ContentValues values, String where, String[] whereArgs) {
        SQLiteDatabase db = sourcehelper.getWritableDatabase();
        int count = db.update(TABLE_NAME, values, where, whereArgs);
        return count;
    }

    public void close(){
        if(sourcehelper != null)
            sourcehelper.close();
    }


    public static class SourceOpenHelper extends SQLiteOpenHelper {

        private static final String ACCOUNT_TABLE_CREATE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        BaseColumns._ID + " INTEGER," +
                        KEY_ACCOUNT_TYPE + " TEXT, " +
                        KEY_SOURCE_NAME + " TEXT, " +
                        KEY_SOURCE_DESC + " TEXT, " +
                        KEY_SOURCE_ID + " TEXT, " +
                        KEY_IMAGE_URL + " TEXT, " +
                        "PRIMARY KEY ("+KEY_ACCOUNT_TYPE + "," + KEY_SOURCE_NAME +")" +
                        ");";


        public SourceOpenHelper(Context context) {
            super(context, Constants.DATABASE_NAME, null, Constants.DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(ACCOUNT_TABLE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            Log.w(this.getClass().getName(), "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);

        }
    }
}
