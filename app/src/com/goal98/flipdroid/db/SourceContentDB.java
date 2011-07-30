package com.goal98.flipdroid.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.goal98.flipdroid.client.TikaExtractResponse;
import com.goal98.flipdroid.model.cachesystem.SourceCacheObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: ITS
 * Date: 11-7-29
 * Time: 上午9:44
 * To change this template use File | Settings | File Templates.
 */
public class SourceContentDB extends AbstractDB {
    public static final String URL = "url";
    public static final String TYPE = "type";
    public static final String CONTENT = "CONTENT";
    public static final String TABLE_NAME = "sourceContent";

    public SourceContentDB(Context context) {
        super(context);
    }

    protected String getTableName() {
        return TABLE_NAME;
    }

    public long persist(SourceCacheObject sourceCacheObject) {
        ContentValues values = new ContentValues();
        values.put(SourceContentDB.URL, sourceCacheObject.getUrl());
        values.put(SourceContentDB.CONTENT, sourceCacheObject.getContent());
        values.put(SourceContentDB.TYPE, sourceCacheObject.getType());

        if (findByURL(sourceCacheObject) != null) {
            return update(sourceCacheObject);
        } else
            return insert(values);
    }

    public long update(SourceCacheObject sourceCacheObject) {
        ContentValues values = new ContentValues();
        values.put(SourceContentDB.URL, sourceCacheObject.getUrl());
        values.put(SourceContentDB.CONTENT, sourceCacheObject.getContent());
        values.put(SourceContentDB.TYPE, sourceCacheObject.getType());
        String selection = SourceContentDB.URL + " = ? and " + SourceContentDB.TYPE + "= ?";
        String[] selectionArgs = {sourceCacheObject.getUrl(), sourceCacheObject.getType()};
        return update(values, selection, selectionArgs);
    }

    public long insert(ContentValues values) {
        SQLiteDatabase db = helper.getWritableDatabase();
        return db.insert(getTableName(), SourceContentDB.URL, values);
    }

    public SourceCacheObject findByURL(SourceCacheObject sourceCacheObject) {
        String[] projection = null;
        String selection = SourceContentDB.URL + " = ? and " + SourceContentDB.TYPE + "= ?";
        String[] selectionArgs = {sourceCacheObject.getUrl(), sourceCacheObject.getType()};

        Cursor cursor = query(projection, selection, selectionArgs, null);
        if (cursor.getCount() == 0) {
            return null;
        }

        cursor.moveToFirst();
        sourceCacheObject.setContent(cursor.getString(3));

        return sourceCacheObject;
    }
}
