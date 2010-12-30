package com.goal98.flipdroid.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.goal98.flipdroid.util.Constants;

public class AccountOpenHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 0;
    private static final String ACCOUNT_TABLE_NAME = "dictionary";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_ACCOUNT_TYPE = "account_type";
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
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
