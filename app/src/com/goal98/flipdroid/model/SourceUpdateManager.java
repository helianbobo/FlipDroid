package com.goal98.flipdroid.model;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.BaseAdapter;
import com.goal98.flipdroid.activity.IndexActivity;
import com.goal98.flipdroid.db.SourceDB;
import com.goal98.flipdroid.model.cachesystem.CacheableArticleSource;
import com.goal98.flipdroid.model.cachesystem.CachedArticleSource;
import com.goal98.flipdroid.model.cachesystem.SourceCache;
import com.goal98.flipdroid.model.rss.RSSArticleSource;
import com.goal98.flipdroid.util.Constants;
import com.goal98.flipdroid.util.EachCursor;
import com.goal98.flipdroid.util.ManagedCursor;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 8/11/11
 * Time: 11:23 AM
 * To change this template use File | Settings | File Templates.
 */
public class SourceUpdateManager {
    private IndexActivity indexActivity;
    private SourceDB sourceDB;
//    private SourceCache sourceCache;
    private Map<String, CachedArticleSource> cachedArticleSourceMap = new HashMap<String, CachedArticleSource>();
//    private BaseAdapter adapter;

    public SourceUpdateManager(IndexActivity indexActivity, BaseAdapter adapter) {
        this.indexActivity = indexActivity;
        this.sourceDB = new SourceDB(indexActivity);
//        this.sourceCache = new SourceCache(indexActivity);
//        this.adapter = adapter;
    }


    public void updateAll() {
        Cursor c = sourceDB.findAll();
        ManagedCursor mc = new ManagedCursor(c);
        final SourceCache sourceCache = new SourceCache(indexActivity);
        final List<CachedArticleSource> cachedArticleSources = new ArrayList<CachedArticleSource>();
        mc.each(new EachCursor() {
            public void call(Cursor c, int index) {
                String sourceType = c.getString(c.getColumnIndex(Source.KEY_SOURCE_TYPE));
                String sourceContentUrl = c.getString(c.getColumnIndex(Source.KEY_CONTENT_URL));
                String sourceName = c.getString(c.getColumnIndex(Source.KEY_SOURCE_NAME));
                String sourceImage = c.getString(c.getColumnIndex(Source.KEY_IMAGE_URL));
                String sourceID = c.getString(c.getColumnIndex(Source.KEY_SOURCE_ID));


//                adapter.getItem()
                CachedArticleSource cachedArticleSource = null;
                if (sourceType.equals(Constants.TYPE_RSS)) {
                    RSSArticleSource rssArticleSource = new RSSArticleSource(sourceContentUrl, sourceName, sourceImage);
                    cachedArticleSource = new CachedArticleSource(rssArticleSource, indexActivity, indexActivity, sourceCache);
                }

                if (cachedArticleSource != null) {
                    cachedArticleSources.add(cachedArticleSource);
                }
            }
        });
        this.sourceDB.close();
//        sourceCache.close();
        for (int i = 0; i < cachedArticleSources.size(); i++) {
            CachedArticleSource cachedArticleSource = cachedArticleSources.get(i);
            cachedArticleSource.loadSourceFromCache();
            cachedArticleSource.checkUpdate();
        }

    }
}
