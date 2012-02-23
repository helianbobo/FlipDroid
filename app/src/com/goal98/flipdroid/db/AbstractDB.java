package com.goal98.flipdroid.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.provider.BaseColumns;
import android.util.Log;
import com.goal98.flipdroid.model.Account;
import com.goal98.flipdroid.model.RecommendSource;
import com.goal98.flipdroid.model.Source;
import com.goal98.flipdroid.util.Constants;
import com.goal98.tika.common.TikaConstants;

import java.util.Date;

public abstract class AbstractDB {

    protected SQLiteOpenHelper helper;
    protected Context context;

    public AbstractDB(Context context) {
        this.context = context;
        helper = new DBOpenHelper(context);
    }

    public void close() {
        if (helper != null)
            helper.close();
    }

    public void open() {
        if(helper == null)
            helper = new DBOpenHelper(context);
    }

    public Cursor query(String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(getTableName());
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);

        return cursor;
    }
    
    public Cursor rawQuery(String sql, String[] args){
        SQLiteDatabase db = helper.getReadableDatabase();
        return db.rawQuery(sql,args);
    }

    protected abstract String getTableName();

    public int update(ContentValues values, String where, String[] whereArgs) {
        SQLiteDatabase db = helper.getWritableDatabase();
        int count = db.update(getTableName(), values, where, whereArgs);
        return count;
    }

    public Cursor findAll() {
        String[] projection = null;
        String selection = null;
        String[] selectionArgs = null;

        Cursor cursor = query(projection, selection, selectionArgs, null);
        return cursor;
    }

    public int deleteAll() {
        SQLiteDatabase db = helper.getWritableDatabase();
        return db.delete(getTableName(), null, null);
    }

    public static class DBOpenHelper extends SQLiteOpenHelper {

        private static final String ACCOUNT_TABLE_CREATE =
                "CREATE TABLE " + Account.TABLE_NAME + " (" +
                        BaseColumns._ID + " INTEGER," +
                        Account.KEY_ACCOUNT_TYPE + " TEXT, " +
                        Account.KEY_USERNAME + " TEXT, " +
                        Account.KEY_PASSWORD + " TEXT, " +
                        Account.KEY_PASSWORD_SECRET + " TEXT, " +
                        Account.KEY_IMAGE_URL + " TEXT, " +
                        "PRIMARY KEY (" + Account.KEY_ACCOUNT_TYPE + "," + Account.KEY_USERNAME + ")" +
                        ");";

        private static final String SOURCE_TABLE_CREATE =
                "CREATE TABLE " + Source.TABLE_NAME + " (" +
                        BaseColumns._ID + " INTEGER," +
                        Source.KEY_SOURCE_TYPE + " TEXT, " +
                        Source.KEY_SOURCE_NAME + " TEXT, " +
                        Source.KEY_SOURCE_DESC + " TEXT, " +
                        Source.KEY_SOURCE_ID + " TEXT, " +
                        Source.KEY_IMAGE_URL + " TEXT, " +
                        Source.KEY_CONTENT_URL + " TEXT, " +
                        Source.KEY_CAT + " TEXT, " +
                        Source.KEY_UPDATE_TIME + " LONG, " +
                        "PRIMARY KEY (" + Source.KEY_SOURCE_TYPE + "," + Source.KEY_SOURCE_NAME + ")" +
                        ");";

        private static final String URL_TABLE_CREATE =
                "CREATE TABLE " + URLDB.TABLE_NAME + " (" +
                        BaseColumns._ID + " INTEGER," +
                        URLDB.URL + " TEXT, " +
                        URLDB.CONTENT + " TEXT, " +
                        URLDB.TITLE + " TEXT, " +
                        URLDB.IMAGES + " TEXT, " +
                        "PRIMARY KEY (" + URLDB.URL + ")" +
                        ");";

        private static final String RSS_URL_TABLE_CREATE =
                "CREATE TABLE " + RSSURLDB.TABLE_NAME + " (" +
                        BaseColumns._ID + " INTEGER," +
                        RSSURLDB.URL + " TEXT, " +
                        RSSURLDB.CONTENT + " TEXT, " +
                        RSSURLDB.TITLE + " TEXT, " +
                        RSSURLDB.IMAGES + " TEXT, " +
                        RSSURLDB.AUTHOR + " TEXT, "+
                        RSSURLDB.AUTHOR_IMAGE + " TEXT, "+
                        RSSURLDB.DATE + " LONG, "+
                        RSSURLDB.SOURCE + " TEXT, "+
                        RSSURLDB.STATUS + " INT, "+
                        RSSURLDB.TYPE + " TEXT, "+
                        "PRIMARY KEY (" + RSSURLDB.URL + ")" +
                        ");";

        private static final String RECOMMAND_SOURCE_TABLE_CREATE =
                "CREATE TABLE " + RecommendSource.TABLE_NAME + " (" +
                        BaseColumns._ID + " INTEGER," +
                        RecommendSource.KEY_BODY + " TEXT, " +
                        RecommendSource.KEY_UPDATE_TIME + " LONG, " +
                        RecommendSource.KEY_TYPE + " TEXT, " +
                        "PRIMARY KEY (" + BaseColumns._ID + ")" +
                        ");";

        private static final String SOURCE_CONTENT_TABLE_CREATE =
                "CREATE TABLE " + SourceContentDB.TABLE_NAME + " (" +
                        BaseColumns._ID + " INTEGER," +
                        SourceContentDB.URL + " TEXT, " +
                        SourceContentDB.TYPE + " TEXT, " +
                        SourceContentDB.CONTENT + " TEXT, " +
                        SourceContentDB.LAST_MODIFIED + " LONG, "+
                        SourceContentDB.IMAGEURL + " TEXT, "+
                        SourceContentDB.AUTHOR + " TEXT, "+
                        "PRIMARY KEY (" + SourceContentDB.URL + ")" +
                        ");";

        private static final String SOURCE_INIT_DATA = "INSERT INTO " + Source.TABLE_NAME +
                " (" + Source.KEY_SOURCE_NAME + "," + Source.KEY_SOURCE_ID + "," + Source.KEY_SOURCE_TYPE + ")" +
                " values ('FAKE', 'FAKE', '" + TikaConstants.TYPE_FAKE + "');";

        public DBOpenHelper(Context context) {
            super(context, Constants.DATABASE_NAME, null, Constants.DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            createAccountTable(sqLiteDatabase);
            createSourceTable(sqLiteDatabase);
            createURLTable(sqLiteDatabase);
            createSourceContentTable(sqLiteDatabase);
            createRecommandTable(sqLiteDatabase);
            createRSSURLTable(sqLiteDatabase);
        }

        private void createRecommandTable(SQLiteDatabase sqLiteDatabase) {
            Log.w(this.getClass().getName(), "Creating table " + RecommendSource.TABLE_NAME);
            try {
                sqLiteDatabase.execSQL(RECOMMAND_SOURCE_TABLE_CREATE);
            } catch (SQLException e) {
                Log.e(this.getClass().getName(), e.getMessage(), e);
            }
        }


        private void createURLTable(SQLiteDatabase sqLiteDatabase) {
            Log.w(this.getClass().getName(), "Creating table " + URLDB.TABLE_NAME);
            try {
                sqLiteDatabase.execSQL(URL_TABLE_CREATE);
            } catch (SQLException e) {
                Log.e(this.getClass().getName(), e.getMessage(), e);
            }
        }

        private void createRSSURLTable(SQLiteDatabase sqLiteDatabase) {
            Log.w(this.getClass().getName(), "Creating table " + RSSURLDB.TABLE_NAME);
            try {
                sqLiteDatabase.execSQL(RSS_URL_TABLE_CREATE);
            } catch (SQLException e) {
                Log.e(this.getClass().getName(), e.getMessage(), e);
            }
        }

        private void createSourceContentTable(SQLiteDatabase sqLiteDatabase) {
            Log.w(this.getClass().getName(), "Creating table " + URLDB.TABLE_NAME);
            try {
                sqLiteDatabase.execSQL(SOURCE_CONTENT_TABLE_CREATE);
            } catch (SQLException e) {
                Log.e(this.getClass().getName(), e.getMessage(), e);
            }
        }

        private void createAccountTable(SQLiteDatabase sqLiteDatabase) {
            Log.w(this.getClass().getName(), "Creating table " + Account.TABLE_NAME);
            try {
                sqLiteDatabase.execSQL(ACCOUNT_TABLE_CREATE);
            } catch (SQLException e) {
                Log.e(this.getClass().getName(), e.getMessage(), e);
            }
        }

        private void createSourceTable(SQLiteDatabase sqLiteDatabase) {
            Log.w(this.getClass().getName(), "Creating table " + Source.TABLE_NAME);
            try {
                sqLiteDatabase.execSQL(SOURCE_TABLE_CREATE);
            } catch (SQLException e) {
                Log.e(this.getClass().getName(), e.getMessage(), e);
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(this.getClass().getName(), "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + SourceContentDB.TABLE_NAME);
            onCreate(db);

        }

    }


}
