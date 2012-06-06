package com.goal98.flipdroid.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.goal98.flipdroid.model.Article;

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
    public static final String FAVORITE = "favorite";

    public static final String SEPARATOR = ";";
    public static final String TABLE_NAME = "rssurl";
    public static final int STATUS_NEW = 1;
    public static final int STATUS_READ = 2;
    public static final int STATUS_ANY = 0;
    public static final int recordPerPage = 10;

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

    public synchronized long insert(Article article) {
        boolean exist = exist(article.getSourceURL(), article.getContent());
        if (exist) {
            return 0;
        }
        if (exist(article.getSourceURL())) {
            return updateArticle(article);
        } else {
            ContentValues values = new ContentValues();
            values.put(RSSURLDB.URL, article.getSourceURL());
            values.put(RSSURLDB.CONTENT, article.getContent());
            values.put(RSSURLDB.TITLE, article.getTitle());
            values.put(RSSURLDB.DATE, article.getCreatedDate().getTime());
            values.put(RSSURLDB.AUTHOR, article.getAuthor());
            if(article.getPortraitImageUrl()!=null)
                values.put(RSSURLDB.AUTHOR_IMAGE, article.getPortraitImageUrl().toExternalForm());

            values.put(RSSURLDB.SOURCE, article.getFrom());
            values.put(RSSURLDB.STATUS, RSSURLDB.STATUS_NEW + "");
            values.put(RSSURLDB.TYPE, article.getSourceType());
            values.put(RSSURLDB.FAVORITE, article.isFavorite());

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
    }

    public synchronized long updateArticle(Article article) {
        boolean exist = exist(article.getSourceURL());
        if (!exist) {
            return 0;
        }
        ContentValues values = new ContentValues();
        values.put(RSSURLDB.FAVORITE, article.isFavorite());
        values.put(RSSURLDB.CONTENT, article.getContent());
        return update(values, RSSURLDB.URL + " = ?", new String[]{article.getSourceURL()});
    }

    public boolean exist(String sourceURL, String content) {
        String[] projection = null;
        String selection = RSSURLDB.URL + " = ? and " + RSSURLDB.CONTENT  + " = ?";
        String[] selectionArgs = {sourceURL,content};

        Cursor cursor = null;
        try {
            cursor = query(projection, selection, selectionArgs, null);

            return cursor.getCount() != 0;
        } finally {
            if (cursor != null)
                cursor.close();
        }
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
        return countByStatus(status, null);
    }

    public int countByStatus(int status, int inDaysFrom, int inDaysTo) {
        return countByStatus(status, null, inDaysFrom, inDaysTo);
    }

    public List<Article> findAllByStatus(int status, int offset) {
        return findAllByStatus(status, null, offset, -1, -1);
    }

    public int countByStatus(int statusNew, String from) {
        return countByStatus(statusNew, from, -1, -1);
    }

    public int countByStatus(int statusNew, String from, int inDaysFrom, int inDaysTo) {
        String[] projection = null;
        String selection = " 1 = 1";

        if (statusNew != 0)
            selection += " and " + (RSSURLDB.STATUS + " = " + statusNew);

        if (from != null)
            selection += " and " + RSSURLDB.SOURCE + " = '" + from + "' ";

        if (inDaysFrom != -1)
            selection += " and " + RSSURLDB.DATE + " < " + (new Date().getTime() - inDaysFrom * 1000l * 3600 * 24) + " and " + RSSURLDB.DATE + ">" + (new Date().getTime() - inDaysTo * 1000l * 3600 * 24);

        String[] selectionArgs = null;

        selectionArgs = new String[]{};

        Cursor cursor = null;
        try {
            cursor = query(projection, selection, selectionArgs, null);
            return cursor.getCount();
        } finally {
            if (cursor != null)
                cursor.close();
        }
    }

    public List<Article> findAllByStatus(int statusNew, int offset, int inDaysFrom, int inDaysTo) {
        return findAllByStatus(statusNew, null, offset, inDaysFrom, inDaysTo);
    }

    public List<Article> findAllByStatus(int statusNew, String from, int offset, int inDaysFrom, int inDaysTo) {
        List<Article> articles = new ArrayList<Article>();
        String fromClause = "";
        if (from != null) {
            fromClause += " and " + RSSURLDB.SOURCE + " = '" + from + "'";
        }
        if (inDaysFrom != -1) {
            fromClause += " and " + RSSURLDB.DATE + " < " + (new Date().getTime() - inDaysFrom * 1000l * 3600 * 24) + " and " + RSSURLDB.DATE + ">" + (new Date().getTime() - inDaysTo * 1000l * 3600 * 24);
        }
        Cursor cursor = rawQuery("select * from " + getTableName() + " where " + RSSURLDB.STATUS + " = ? " + fromClause + " order by " + RSSURLDB.DATE + " desc limit " + recordPerPage + " offset " + offset, new String[]{statusNew + ""});
        try {
            if (cursor.getCount() == 0) {
                return null;
            }
            cursor.moveToFirst();
            do {
                Article article = cursorToArticle(cursor);
                articles.add(article);
            } while (cursor.moveToNext());


        } finally {
            cursor.close();
        }
        return articles;
    }

    private Article cursorToArticle(Cursor cursor) {
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
        article.setFrom(cursor.getString(cursor.getColumnIndex(RSSURLDB.SOURCE)));
        String imageString = cursor.getString(cursor.getColumnIndex(RSSURLDB.IMAGES));
        int favorite = cursor.getInt(cursor.getColumnIndex(RSSURLDB.FAVORITE));
        article.setIsFavorite(favorite != 0);
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
        return article;
    }

    public List<Article> findAllFavorite(String from, int offset) {
        List<Article> articles = new ArrayList<Article>();
        String fromClause = RSSURLDB.FAVORITE + "= 1 ";
        if (from != null) {
            fromClause += " and " + RSSURLDB.SOURCE + " = '" + from + "'";
        }
        Cursor cursor = rawQuery("select * from " + getTableName() + " where " + fromClause + " order by " + RSSURLDB.DATE + " desc limit " + recordPerPage + " offset " + offset, new String[]{});
        try {
            if (cursor.getCount() == 0) {
                return null;
            }
            cursor.moveToFirst();
            do {
                Article article = cursorToArticle(cursor);
                articles.add(article);
            } while (cursor.moveToNext());


        } finally {
            cursor.close();
        }
        return articles;
    }

    public int countByFavorite(String from) {
        String[] projection = null;
        String selection = " 1 = 1";

        selection += " and "+ RSSURLDB.FAVORITE + " = " + "1";
        if (from != null)
            selection += " and " + RSSURLDB.SOURCE + " = '" + from + "' ";


        String[] selectionArgs = null;

        selectionArgs = new String[]{};

        Cursor cursor = null;
        try {
            cursor = query(projection, selection, selectionArgs, null);
            return cursor.getCount();
        } finally {
            if (cursor != null)
                cursor.close();
        }
    }

    public int countByFavorite() {
        return countByFavorite(null);
    }

    public List<Article> findAllFavorite(int offset) {
        return findAllFavorite(null,offset);
    }
}
