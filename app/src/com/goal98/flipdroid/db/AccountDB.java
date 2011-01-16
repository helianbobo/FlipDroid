package com.goal98.flipdroid.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;
import android.util.Log;
import com.goal98.flipdroid.util.Constants;

public class AccountDB {

    public static final String ACCOUNT_TABLE_NAME = "account";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_ACCOUNT_TYPE = "account_type";

    private SQLiteOpenHelper accounthelper;

    public AccountDB(Context context) {
        accounthelper = new AccountOpenHelper(context);
    }

    public void close(){
        if(accounthelper!= null)
            accounthelper.close();
    }

    public int deleteAll() {

        SQLiteDatabase db = accounthelper.getWritableDatabase();
        return db.delete(ACCOUNT_TABLE_NAME, null, null);
    }

    public boolean exist(String username, String accountType) {
        String[] projection = {KEY_USERNAME, KEY_ACCOUNT_TYPE};
        String selection = KEY_USERNAME + " = ? and " + KEY_ACCOUNT_TYPE + " = ?";
        String[] selectionArgs = {username, accountType};

        Cursor cursor = query(projection, selection, selectionArgs, null);
        boolean result = cursor != null && cursor.getCount() > 0;
        if(cursor!=null)
            cursor.close();
        return result;

    }

    public Cursor findAll(){
        String[] projection = null;
        String selection = null;
        String[] selectionArgs = null;

        Cursor cursor = query(projection, selection, selectionArgs, null);
        return cursor;
    }

    public Cursor findByType(String accountType){
        String[] projection = null;
        String selection = KEY_ACCOUNT_TYPE + " = ?";
        String[] selectionArgs = {accountType};

        Cursor cursor = query(projection, selection, selectionArgs, null);
        return cursor;
    }

    public Cursor findByTypeAndUsername(String accountType, String username){
        String[] projection = null;
        String selection = KEY_ACCOUNT_TYPE + " = ? AND " + KEY_USERNAME + " = ?";
        String[] selectionArgs = {accountType, username};

        Cursor cursor = query(projection, selection, selectionArgs, null);
        return cursor;
    }

    public Cursor query(String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(ACCOUNT_TABLE_NAME);

        SQLiteDatabase db = accounthelper.getReadableDatabase();
        Cursor cursor = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);

        return cursor;
    }

    public long insert(ContentValues values) {
        SQLiteDatabase db = accounthelper.getWritableDatabase();
        return db.insert(ACCOUNT_TABLE_NAME, KEY_ACCOUNT_TYPE, values);
    }

    public long insert(String username, String password, String accountType) {
        ContentValues values = new ContentValues();
        values.put(KEY_USERNAME, username);
        values.put(KEY_PASSWORD, password);
        values.put(KEY_ACCOUNT_TYPE, accountType);
        return insert(values);
    }

    public long insertOrUpdate(String username, String password, String accountType) {

        boolean accountExsit = exist(username, accountType);

        ContentValues values = new ContentValues();
        values.put(KEY_USERNAME, username);
        values.put(KEY_PASSWORD, password);
        values.put(KEY_ACCOUNT_TYPE, accountType);

        if (!accountExsit) {
            return insert(values);
        } else {
            return update(values, KEY_USERNAME + " = ? and " + KEY_ACCOUNT_TYPE + " = ?", new String[]{username, accountType});
        }


    }

    public int update(ContentValues values, String where, String[] whereArgs) {
        SQLiteDatabase db = accounthelper.getWritableDatabase();
        int count = db.update(ACCOUNT_TABLE_NAME, values, where, whereArgs);
        return count;
    }

    public boolean hasAccount(String type) {
        String[] projection = {KEY_ACCOUNT_TYPE};
        String selection = KEY_ACCOUNT_TYPE + " = ?";
        String[] selectionArgs = {type};

        Cursor cursor = query(projection, selection, selectionArgs, null);
        boolean result = cursor != null && cursor.getCount() > 0;
        if(cursor != null){
            cursor.close();
        }
        return result;
    }

    public static class AccountOpenHelper extends SQLiteOpenHelper {

        private static final int DATABASE_VERSION = 5;

        private static final String ACCOUNT_TABLE_CREATE =
                "CREATE TABLE " + ACCOUNT_TABLE_NAME + " (" +
                        BaseColumns._ID + " INTEGER," +
                        KEY_ACCOUNT_TYPE + " TEXT, " +
                        KEY_USERNAME + " TEXT, " +
                        KEY_PASSWORD + " TEXT, " +
                        "PRIMARY KEY (" + KEY_ACCOUNT_TYPE + "," + KEY_USERNAME + ")" +
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
