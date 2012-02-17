package com.goal98.flipdroid.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.goal98.flipdroid.client.TikaExtractResponse;
import com.goal98.flipdroid.model.Article;
import com.goal98.flipdroid.model.Source;
import com.goal98.flipdroid.model.cachesystem.SourceCacheObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: ITS
 * Date: 11-7-26
 * Time: 下午2:30
 * To change this template use File | Settings | File Templates.
 */
public class RSSURLDB extends AbstractDB {
    public static final String URL = "url";
    public static final String CONTENT = "content";
    public static final String TITLE = "TITLE";
    public static final String IMAGES = "images";
    public static final String AUTHOR = "author";
    public static final String AUTHOR_IMAGE = "authorimage";
    public static final String DATE = "date";
    public static final String SOURCE = "source";
    public static final String STATUS = "status";
    public static final String TYPE = "type";

    public static final String SEPARATOR = ";";
    public static final String TABLE_NAME = "rssurl";
    public static final int STATUS_NEW = 1;
    public static final int STATUS_READ = 2;
    public static final int recordPerPage = 20;

    public RSSURLDB(Context context) {
        super(context);
    }

    @Override
    protected String getTableName() {
        return TABLE_NAME;
    }

    public int getCount() {
        Cursor cursor = this.findAll();

        try {
            return cursor.getCount();
        } finally {
            cursor.close();
        }
    }

    public void deleteFromFrom(String from) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete(RSSURLDB.TABLE_NAME, RSSURLDB.SOURCE + " = ? ", new String[]{from});
    }

    public synchronized long insert(Article article, String from) {
        boolean exist = exist(article.getSourceURL());
        if (exist) {
            return 0;
        }
        ContentValues values = new ContentValues();
        values.put(RSSURLDB.URL, article.getSourceURL());
        values.put(RSSURLDB.CONTENT, article.getContent());
        values.put(RSSURLDB.TITLE, article.getTitle());
        values.put(RSSURLDB.DATE, article.getCreatedDate().getTime());
        values.put(RSSURLDB.AUTHOR, article.getAuthor());
        values.put(RSSURLDB.AUTHOR_IMAGE, article.getPortraitImageUrl().toExternalForm());
        values.put(RSSURLDB.SOURCE, from);
        values.put(RSSURLDB.STATUS, RSSURLDB.STATUS_NEW + "");
        values.put(RSSURLDB.TYPE, article.getSourceType());

        String images = "";
        for (int i = 0; i < article.getImages().size(); i++) {
            String imageURL = article.getImages().get(i);
            images = images.concat(imageURL + ";");
        }
        if (images.length() >= 1)
            values.put(RSSURLDB.IMAGES, images.substring(0, images.length() - 1));
        else
            values.put(RSSURLDB.IMAGES, "");
        return insert(values);
    }

    public boolean exist(String sourceURL) {
        String[] projection = null;
        String selection = RSSURLDB.URL + " = ?";
        String[] selectionArgs = {sourceURL};

        Cursor cursor = null;
        try {
            cursor = query(projection, selection, selectionArgs, null);

            return cursor.getCount() != 0;
        } finally {
            if (cursor != null)
                cursor.close();
        }
    }

    public long insert(ContentValues values) {
        SQLiteDatabase db = helper.getWritableDatabase();
        return db.insert(getTableName(), RSSURLDB.URL, values);
    }

    public int countByStatus(int status) {
        String[] projection = null;
        String selection = RSSURLDB.STATUS + " = ?";
        String[] selectionArgs = {status + ""};

        Cursor cursor = null;
        try {
            cursor = query(projection, selection, selectionArgs, null);
            return cursor.getCount();
        } finally {
            if (cursor != null)
                cursor.close();
        }
    }

    public List<Article> findAllByStatus(int status, int offset) {
        List<Article> articles = new ArrayList<Article>();
        Cursor cursor = rawQuery("select * from " + getTableName() + " where " + RSSURLDB.STATUS + " = ? limit "+recordPerPage+" offset " + offset, new String[]{status + ""});
        try {
            if (cursor.getCount() == 0) {
                return null;
            }
            cursor.moveToFirst();

            do {
                Article article = new Article();
                article.setAuthor(cursor.getString(cursor.getColumnIndex(RSSURLDB.AUTHOR)));
                try {
                    article.setPortraitImageUrl(new URL(cursor.getString(cursor.getColumnIndex(RSSURLDB.AUTHOR_IMAGE))));
                } catch (MalformedURLException e) {

                }
                article.setContent(cursor.getString(cursor.getColumnIndex(RSSURLDB.CONTENT)));

                article.setTitle(cursor.getString(cursor.getColumnIndex(RSSURLDB.TITLE)));
                article.setCreatedDate(new Date(cursor.getLong(cursor.getColumnIndex(RSSURLDB.DATE))));
                article.setAuthor(cursor.getString(cursor.getColumnIndex(RSSURLDB.AUTHOR)));
                article.setSourceType(cursor.getString(cursor.getColumnIndex(RSSURLDB.TYPE)));
                article.setSourceURL(cursor.getString(cursor.getColumnIndex(RSSURLDB.URL)));
                String imageString = cursor.getString(cursor.getColumnIndex(RSSURLDB.IMAGES));
                String[] imageArr = imageString.split(";");
                List<String> images = new ArrayList<String>();
                for (int i = 0; i < imageArr.length; i++) {
                    String image = imageArr[i];
                    images.add(image);
                }
                if (images.size() != 0)
                    try {
                        article.setImageUrl(new URL(images.get(0)));
                    } catch (MalformedURLException e) {

                    }

                articles.add(article);
            } while (cursor.moveToNext());


        } finally {
            cursor.close();
        }
        return articles;
    }
}
