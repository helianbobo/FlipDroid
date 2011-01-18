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
import com.goal98.flipdroid.model.Source;
import com.goal98.flipdroid.util.Constants;

import java.util.HashMap;
import java.util.Map;

public class SourceDB extends AbstractDB{

    public SourceDB(Context context) {
        super(context);
    }

    @Override
    protected String getTableName() {
        return Source.TABLE_NAME;
    }

    public static Map<String, String> buildSource(String accountType,String name, String id, String desc, String imageUrl){
        Map<String, String> result = new HashMap<String, String>();
        result.put(Source.KEY_SOURCE_NAME, name);
        result.put(Source.KEY_SOURCE_ID, id);
        result.put(Source.KEY_SOURCE_DESC, desc);
        result.put(Source.KEY_ACCOUNT_TYPE, accountType);
        result.put(Source.KEY_IMAGE_URL, imageUrl);
        return  result;
    }

    public long insert(ContentValues values) {
        SQLiteDatabase db = helper.getWritableDatabase();
        return db.insert(Source.TABLE_NAME, Source.KEY_ACCOUNT_TYPE, values);
    }

    public long insert(Map<String, String> source) {
        ContentValues values = new ContentValues();
        values.put(Source.KEY_SOURCE_NAME, source.get(Source.KEY_SOURCE_NAME));
        values.put(Source.KEY_ACCOUNT_TYPE, source.get(Source.KEY_ACCOUNT_TYPE));
        values.put(Source.KEY_SOURCE_ID, source.get(Source.KEY_SOURCE_ID));
        values.put(Source.KEY_IMAGE_URL, source.get(Source.KEY_IMAGE_URL));
        values.put(Source.KEY_SOURCE_DESC, source.get(Source.KEY_SOURCE_DESC));
        return insert(values);
    }

    public long insert(String accountType, String sourceName, String sourceId, String sourceDesc) {
        ContentValues values = new ContentValues();
        values.put(Source.KEY_SOURCE_NAME, sourceName);
        values.put(Source.KEY_ACCOUNT_TYPE, accountType);
        values.put(Source.KEY_SOURCE_ID, sourceId);
        values.put(Source.KEY_SOURCE_DESC, sourceDesc);
        return insert(values);
    }

    public int update(ContentValues values, String where, String[] whereArgs) {
        SQLiteDatabase db = helper.getWritableDatabase();
        int count = db.update(Source.TABLE_NAME, values, where, whereArgs);
        return count;
    }
}
