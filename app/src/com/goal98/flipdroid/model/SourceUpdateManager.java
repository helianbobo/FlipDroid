package com.goal98.flipdroid.model;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.BaseAdapter;
import com.goal98.flipdroid.activity.IndexActivity;
import com.goal98.flipdroid.client.TikaClient;
import com.goal98.flipdroid.db.RecommendSourceDB;
import com.goal98.flipdroid.db.SourceDB;
import com.goal98.flipdroid.model.cachesystem.CacheableArticleSource;
import com.goal98.flipdroid.model.cachesystem.CachedArticleSource;
import com.goal98.flipdroid.model.cachesystem.SourceCache;
import com.goal98.flipdroid.model.cachesystem.SourceUpdateable;
import com.goal98.flipdroid.model.featured.FeaturedArticleSource;
import com.goal98.flipdroid.model.rss.RSSArticleSource;
import com.goal98.flipdroid.util.Constants;
import com.goal98.flipdroid.util.EachCursor;
import com.goal98.flipdroid.util.ManagedCursor;
import com.goal98.tika.common.TikaConstants;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 8/11/11
 * Time: 11:23 AM
 * To change this template use File | Settings | File Templates.
 */
public class SourceUpdateManager {
    private SourceDB sourceDB;
    private SourceCache sourceCache;
    private SourceUpdateable updateable;
    private RecommendSourceDB recommendSourceDB;

    public SourceUpdateManager(SourceDB sourceDB, SourceCache sourceCache, SourceUpdateable updateable, RecommendSourceDB recommendSourceDB) {
        this.sourceDB = sourceDB;
        this.sourceCache = sourceCache;
        this.updateable = updateable;
        this.recommendSourceDB = recommendSourceDB;
    }

    public void updateAll() {
        Cursor c = sourceDB.findAll();
        ManagedCursor mc = new ManagedCursor(c);
        final List<CachedArticleSource> cachedArticleSources = new ArrayList<CachedArticleSource>();
        mc.each(new EachCursor() {
            public void call(Cursor c, int index) {
                String sourceType = c.getString(c.getColumnIndex(Source.KEY_SOURCE_TYPE));
                String sourceContentUrl = c.getString(c.getColumnIndex(Source.KEY_CONTENT_URL));
                String sourceName = c.getString(c.getColumnIndex(Source.KEY_SOURCE_NAME));
                String sourceImage = c.getString(c.getColumnIndex(Source.KEY_IMAGE_URL));

                CachedArticleSource cachedArticleSource = null;
                if (sourceType.equals(TikaConstants.TYPE_RSS)) {
                    FeaturedArticleSource featuredArticleSource = new FeaturedArticleSource(sourceContentUrl, sourceName, sourceImage);
                    cachedArticleSource = new CachedArticleSource(featuredArticleSource, updateable, sourceCache);
                }

                if (cachedArticleSource != null) {
                    cachedArticleSources.add(cachedArticleSource);
                }
            }
        });
        this.sourceDB.close();
        for (int i = 0; i < cachedArticleSources.size(); i++) {
            CachedArticleSource cachedArticleSource = cachedArticleSources.get(i);
            cachedArticleSource.loadSourceFromCache();
            cachedArticleSource.checkUpdate();
        }

        Thread t = new Thread(new Runnable() {
            public void run() {
                String rssUpdatedSource = new TikaClient(Constants.TIKA_HOST).updateRecommendSource(TikaConstants.TYPE_RSS);
                recommendSourceDB.update(rssUpdatedSource, TikaConstants.TYPE_RSS);
                System.out.println("recommend source updated");

                String sinaWeiboUpdatedSource = new TikaClient(Constants.TIKA_HOST).updateRecommendSource(TikaConstants.TYPE_SINA_WEIBO);
                recommendSourceDB.update(sinaWeiboUpdatedSource, TikaConstants.TYPE_SINA_WEIBO);
                System.out.println("recommend source updated");
            }
        });
        t.start();
    }
}
