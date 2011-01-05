package com.goal98.flipdroid.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;
import com.goal98.flipdroid.util.Constants;

public class SourceDB {

    private static final String TABLE_NAME = "source";
    private static final String KEY_SOURCE_NAME = "source_name";
    private static final String KEY_ACCOUNT_TYPE = "account_type";

    private SQLiteOpenHelper accounthelper;

    public SourceDB(Context context) {
        accounthelper = new SourceOpenHelper(context);
    }

    public int deleteAll() {

        SQLiteDatabase db = accounthelper.getWritableDatabase();
        return db.delete(TABLE_NAME, null, null);
    }

    public Cursor query(String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(TABLE_NAME);

        SQLiteDatabase db = accounthelper.getReadableDatabase();
        Cursor cursor = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);

        return cursor;
    }

    public long insert(ContentValues values) {
        SQLiteDatabase db = accounthelper.getWritableDatabase();
        return db.insert(TABLE_NAME, KEY_ACCOUNT_TYPE, values);
    }

    public long insert(String sourceName, String accountType) {
        ContentValues values = new ContentValues();
        values.put(KEY_SOURCE_NAME, sourceName);
        values.put(KEY_ACCOUNT_TYPE, accountType);
        return insert(values);
    }

    public int update(ContentValues values, String where, String[] whereArgs) {
        SQLiteDatabase db = accounthelper.getWritableDatabase();
        int count = db.update(TABLE_NAME, values, where, whereArgs);
        return count;
    }


    public static class SourceOpenHelper extends SQLiteOpenHelper {

        private static final int DATABASE_VERSION = 1;

        private static final String ACCOUNT_TABLE_CREATE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        KEY_ACCOUNT_TYPE + " TEXT, " +
                        KEY_SOURCE_NAME + " TEXT, " +
                        "PRIMARY KEY ("+KEY_ACCOUNT_TYPE + "," + KEY_SOURCE_NAME +")" +
                        ");";


        public SourceOpenHelper(Context context) {
            super(context, Constants.DATABASE_NAME, null, DATABASE_VERSION);
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
