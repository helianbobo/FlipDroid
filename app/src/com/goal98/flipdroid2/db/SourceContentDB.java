package com.goal98.flipdroid2.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.goal98.flipdroid2.model.cachesystem.SourceCacheObject;

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
    public static final String LAST_MODIFIED = "last_modified";

    public static final String TABLE_NAME = "sourceContent";
    public static final String IMAGEURL = "imageurl";
    public static final String AUTHOR = "author";

    public SourceContentDB(Context context) {
        super(context);
    }

    protected String getTableName() {
        return TABLE_NAME;
    }

    public long persist(SourceCacheObject sourceCacheObject) {

        SourceCacheObject sample = new SourceCacheObject();
        sample.setType(sourceCacheObject.getType());
        sample.setUrl(sourceCacheObject.getUrl());
        if (findByURL(sample) != null) {
            return update(sourceCacheObject);
        } else {
            ContentValues values = new ContentValues();
            values.put(SourceContentDB.URL, sourceCacheObject.getUrl());
            values.put(SourceContentDB.IMAGEURL, sourceCacheObject.getImageUrl());
            values.put(SourceContentDB.CONTENT, sourceCacheObject.getContent());
            values.put(SourceContentDB.TYPE, sourceCacheObject.getType());
            values.put(SourceContentDB.LAST_MODIFIED, sourceCacheObject.getLastModified());
            values.put(SourceContentDB.AUTHOR, sourceCacheObject.getAuthor());
            return insert(values);
        }
    }

    public long update(SourceCacheObject sourceCacheObject) {
        ContentValues values = new ContentValues();
        values.put(SourceContentDB.URL, sourceCacheObject.getUrl());
        values.put(SourceContentDB.CONTENT, sourceCacheObject.getContent());
        values.put(SourceContentDB.TYPE, sourceCacheObject.getType());
        values.put(SourceContentDB.LAST_MODIFIED, sourceCacheObject.getLastModified());
        values.put(SourceContentDB.IMAGEURL, sourceCacheObject.getImageUrl());
        values.put(SourceContentDB.AUTHOR, sourceCacheObject.getAuthor());

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
        try {
            if (cursor.getCount() == 0) {
                return null;
            }
            cursor.moveToFirst();
            sourceCacheObject.setContent(cursor.getString(3));
            sourceCacheObject.setLastModified(cursor.getLong(4));
            sourceCacheObject.setImageUrl(cursor.getString(5));
            sourceCacheObject.setAuthor(cursor.getString(6));
        } finally {
            cursor.close();
        }
        return sourceCacheObject;
    }

    public List<SourceCacheObject> findAllByType(String type) {

        String[] projection = null;
        String selection = SourceContentDB.TYPE + "= ?";
        String[] selectionArgs = {type};

        List<SourceCacheObject> sourceCacheObjects = new ArrayList<SourceCacheObject>();
        Cursor cursor = query(projection, selection, selectionArgs, null);
        try {
            if (cursor.getCount() == 0) {
                return null;
            }
            cursor.moveToFirst();

            do {
                SourceCacheObject sourceCacheObject = new SourceCacheObject();
                sourceCacheObject.setType(type);
                sourceCacheObject.setContent(cursor.getString(3));
                sourceCacheObject.setLastModified(cursor.getLong(4));
                sourceCacheObject.setImageUrl(cursor.getString(5));
                sourceCacheObject.setAuthor(cursor.getString(6));
                sourceCacheObjects.add(sourceCacheObject);
            } while (cursor.moveToNext());


        } finally {
            cursor.close();
        }
        return sourceCacheObjects;
    }

    public void clear(SourceCacheObject cacheObject) {
        SQLiteDatabase db = helper.getWritableDatabase();

        String selection = SourceContentDB.TYPE + " = ? and " + SourceContentDB.URL + "= ?";
        String[] selectionArgs = {cacheObject.getType(), cacheObject.getUrl()};

        db.delete(getTableName(), selection, selectionArgs);
    }
}
