package com.goal98.flipdroid.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;
import com.goal98.flipdroid.util.Constants;

public class AccountDB {

    private static final String ACCOUNT_TABLE_NAME = "dictionary";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_ACCOUNT_TYPE = "account_type";

    private SQLiteOpenHelper accounthelper;

    public AccountDB(Context context) {
        accounthelper = new AccountOpenHelper(context);
    }

    public int deleteAll() {

        SQLiteDatabase db = accounthelper.getWritableDatabase();
        return db.delete(ACCOUNT_TABLE_NAME, null, null);
    }

    public Cursor query(String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(ACCOUNT_TABLE_NAME);

        SQLiteDatabase db = accounthelper.getReadableDatabase();
        Cursor cursor = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);

        return cursor;
    }

    public static class AccountOpenHelper extends SQLiteOpenHelper {

        private static final int DATABASE_VERSION = 1;

        private static final String ACCOUNT_TABLE_CREATE =
                "CREATE TABLE " + ACCOUNT_TABLE_NAME + " (" +
                        KEY_ACCOUNT_TYPE + " TEXT, " +
                        KEY_USERNAME + " TEXT, " +
                        KEY_PASSWORD + " TEXT" +
                        ");";


        public AccountOpenHelper(Context context) {
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
            db.execSQL("DROP TABLE IF EXISTS " + ACCOUNT_TABLE_NAME);
            onCreate(db);

        }
    }
}
