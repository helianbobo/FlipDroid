package com.goal98.flipdroid.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.goal98.flipdroid.model.RecommendSource;
import com.goal98.flipdroid.model.Source;
import com.goal98.flipdroid.util.EachCursor;
import com.goal98.flipdroid.util.ManagedCursor;
import com.goal98.tika.common.TikaConstants;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SourceDB extends AbstractDB {

    public SourceDB(Context context) {
        super(context);
    }

    @Override
    protected String getTableName() {
        return Source.TABLE_NAME;
    }

    public static Map<String, String> buildSource(String accountType, String name, String id, String desc, String imageUrl, String contentURL, String cat) {
        Map<String, String> result = new HashMap<String, String>();
        result.put(Source.KEY_SOURCE_NAME, name);
        result.put(Source.KEY_SOURCE_ID, id);
        result.put(Source.KEY_SOURCE_DESC, desc);
        result.put(Source.KEY_SOURCE_TYPE, accountType);
        result.put(Source.KEY_IMAGE_URL, imageUrl);
        result.put(Source.KEY_CONTENT_URL, contentURL);
        result.put(Source.KEY_CAT, cat);
        return result;
    }

    public static Map<String, String> buildSource(String accountType, String name, String id, String desc, String imageUrl, String cat) {
        return buildSource(accountType, name, id, desc, imageUrl, null, cat);
    }

    public long insert(ContentValues values) {
        SQLiteDatabase db = helper.getWritableDatabase();
        return db.insert(Source.TABLE_NAME, Source.KEY_SOURCE_TYPE, values);
    }

    public long insert(Map<String, String> source) {
        ContentValues values = new ContentValues();
        values.put(Source.KEY_SOURCE_NAME, source.get(Source.KEY_SOURCE_NAME));
        values.put(Source.KEY_SOURCE_TYPE, source.get(Source.KEY_SOURCE_TYPE));
        values.put(Source.KEY_SOURCE_ID, source.get(Source.KEY_SOURCE_ID));
        values.put(Source.KEY_IMAGE_URL, source.get(Source.KEY_IMAGE_URL));
        values.put(Source.KEY_SOURCE_DESC, source.get(Source.KEY_SOURCE_DESC));
        values.put(Source.KEY_CONTENT_URL, source.get(Source.KEY_CONTENT_URL));
        values.put(Source.KEY_CAT, source.get(Source.KEY_CAT));
        return insert(values);
    }

    public long insert(String accountType, String sourceName, String sourceId, String sourceDesc, String contentURL, String cat, String imageURL) {
        ContentValues values = new ContentValues();
        values.put(Source.KEY_SOURCE_NAME, sourceName);
        values.put(Source.KEY_SOURCE_TYPE, accountType);
        values.put(Source.KEY_SOURCE_ID, sourceId);
        values.put(Source.KEY_SOURCE_DESC, sourceDesc);
        values.put(Source.KEY_CONTENT_URL, contentURL);
        values.put(Source.KEY_CAT, cat);
        values.put(Source.KEY_IMAGE_URL, imageURL);
        return insert(values);
    }

    public boolean isMySinaWeiboAccountExist() {
        String[] projection = {Source.KEY_SOURCE_TYPE};
        String selection = Source.KEY_SOURCE_TYPE + " = ? ";
        String[] selectionArgs = {TikaConstants.TYPE_MY_SINA_WEIBO};

        Cursor cursor = query(projection, selection, selectionArgs, null);
        boolean result = cursor != null && cursor.getCount() > 0;
        if (cursor != null)
            cursor.close();
        return result;
    }

    public int update(ContentValues values, String where, String[] whereArgs) {
        SQLiteDatabase db = helper.getWritableDatabase();
        int count = db.update(Source.TABLE_NAME, values, where, whereArgs);
        return count;
    }

    public void removeSourceByName(String sourceName) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete(Source.TABLE_NAME, Source.KEY_SOURCE_NAME + " = ? ", new String[]{sourceName});
    }

    public Cursor findSourceByName(final String name) {
        String[] projection = null;
        String selection = Source.KEY_SOURCE_NAME + " = ? ";
        String[] selectionArgs = {name};

        return query(projection, selection, selectionArgs, null);
    }
}
