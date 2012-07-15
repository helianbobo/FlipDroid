package com.goal98.girl.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.goal98.girl.model.RecommendSource;
import com.goal98.girl.model.Source;

import java.util.*;

public class RecommendSourceDB extends AbstractDB {
    private static RecommendSourceDB recommendSourceDB;

    private RecommendSourceDB(Context context) {
        super(context);
    }

    public static synchronized RecommendSourceDB getInstance(Context context) {
        if (recommendSourceDB == null) {
            recommendSourceDB = new RecommendSourceDB(context);
        }
        return recommendSourceDB;
    }

    @Override
    protected String getTableName() {
        return RecommendSource.TABLE_NAME;
    }

    public long insert(ContentValues values) {
        SQLiteDatabase db = helper.getWritableDatabase();
        return db.insert(getTableName(), Source.KEY_SOURCE_TYPE, values);
    }

    public long insert(String body, String type, long lastModified) {
        ContentValues values = new ContentValues();
        values.put(RecommendSource.KEY_UPDATE_TIME, lastModified);
        values.put(RecommendSource.KEY_BODY, body);
        values.put(RecommendSource.KEY_TYPE, type);
        return insert(values);
    }


    public void update(String body, String type, long lastModified) {
        deleteByType(type);
        insert(body, type,lastModified);
    }

    public long getLastModified(String type){
        RecommendSource sourceByType = findSourceByType(type);
        if(sourceByType==null)
            return -1;

        return sourceByType.getUpdateTime() .getTime();
    }

    public void deleteByType(String type) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete(RecommendSource.TABLE_NAME, RecommendSource.KEY_TYPE + " = ? ", new String[]{type});
    }

    public RecommendSource findSourceByType(String type) {
        String[] projection = new String[]{
                RecommendSource.KEY_BODY,
                RecommendSource.KEY_UPDATE_TIME
        };

        String selection = RecommendSource.KEY_TYPE + " = ? ";
        String[] selectionArgs = {type};

        Cursor cursor = query(projection, selection, selectionArgs, null);
        RecommendSource rs = new RecommendSource();
        try {
            if (cursor.getCount() == 0) {
                return null;
            }
            cursor.moveToFirst();
            rs.setBody(cursor.getString(0));
            rs.setType(type);
            rs.setUpdateTime(new Date(cursor.getLong(1)));
        } finally {
            cursor.close();

        }
        return rs;
    }
}
