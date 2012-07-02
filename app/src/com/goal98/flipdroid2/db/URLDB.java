package com.goal98.flipdroid2.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.goal98.flipdroid2.client.TikaExtractResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: ITS
 * Date: 11-7-26
 * Time: 下午2:30
 * To change this template use File | Settings | File Templates.
 */
public class URLDB extends AbstractDB {
    public static final String URL = "url";
    public static final String CONTENT = "content";
    public static final String TITLE = "TITLE";
    public static final String IMAGES = "images";
    public static final String SEPARATOR = ";";
    public static final String TABLE_NAME = "url";

    public URLDB(Context context) {
        super(context);
    }

    @Override
    protected String getTableName() {
        return "url";
    }

    public long insert(String url, TikaExtractResponse response) {
        ContentValues values = new ContentValues();
        values.put(URLDB.URL, url);
        values.put(URLDB.CONTENT, response.getContent());
        values.put(URLDB.TITLE, response.getTitle());
        String images = "";
        for (int i = 0; i < response.getImages().size(); i++) {
            String imageURL = response.getImages().get(i);
            images = images.concat(imageURL + ";");
        }
        if (images.length() >= 1)
            values.put(URLDB.IMAGES, images.substring(0, images.length() - 1));
        else
            values.put(URLDB.IMAGES, "");
        return insert(values);
    }

    public long insert(ContentValues values) {
        SQLiteDatabase db = helper.getWritableDatabase();
        return db.insert(getTableName(), URLDB.URL, values);
    }

    public  TikaExtractResponse findByURL(String url) {
        String[] projection = null;
        String selection = URLDB.URL + " = ?";
        String[] selectionArgs = {url};

        Cursor cursor = query(projection, selection, selectionArgs, null);
        TikaExtractResponse response = new TikaExtractResponse();
        try {
            if (cursor.getCount() == 0) {
                return null;
            }


            cursor.moveToFirst();
            response.setSourceURL(url);
            response.setContent(cursor.getString(2));
            response.setTitle(cursor.getString(3));
            List<String> images = new ArrayList<String>();
            String imageString = cursor.getString(4);
            String[] imageArr = imageString.split(";");
            for (int i = 0; i < imageArr.length; i++) {
                String image = imageArr[i];
                images.add(image);
            }
            response.setImages(images);
        } finally {
            cursor.close();

        }
        return response;
    }
}
