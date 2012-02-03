package com.goal98.flipdroid.model;

import android.database.Cursor;
import com.goal98.flipdroid.client.LastModifiedStampedResult;
import com.goal98.flipdroid.client.TikaClient;
import com.goal98.flipdroid.db.RecommendSourceDB;
import com.goal98.flipdroid.db.SourceDB;
import com.goal98.flipdroid.model.cachesystem.CachedArticleSource;
import com.goal98.flipdroid.model.cachesystem.SourceCache;
import com.goal98.flipdroid.model.cachesystem.SourceUpdateable;
import com.goal98.flipdroid.model.rss.RemoteRSSArticleSource;
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

    public void updateAll(boolean block) {
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
                    RemoteArticleSource remoteRSSArticleSource = new RemoteRSSArticleSource(sourceContentUrl, sourceName, sourceImage);
                    cachedArticleSource = new CachedArticleSource(remoteRSSArticleSource, updateable, sourceCache);
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

        updateSourceList(block);
    }

    public void updateSourceList(boolean block) {
        Thread t = new Thread(new Runnable() {
            public void run() {
                for (String updateType : UPDATE_TYPE) {
                    long lastModified = recommendSourceDB.getLastModified(updateType);
                    LastModifiedStampedResult rssUpdatedSource = new TikaClient(Constants.TIKA_HOST).updateRecommendSource(updateType, lastModified);
                    if (rssUpdatedSource != null && rssUpdatedSource.getResult() != null && ((String) rssUpdatedSource.getResult()).length() != 0)
                        recommendSourceDB.update((String) rssUpdatedSource.getResult(), updateType, rssUpdatedSource.getLastModified());
                }
            }
        });
        t.start();
        if (block) {
            try {
                t.join();
            } catch (InterruptedException e) {

            }
        }
    }

    public static final String[] UPDATE_TYPE = new String[]{TikaConstants.TYPE_RSS, TikaConstants.TYPE_SINA_WEIBO, TikaConstants.TYPE_DEFAULT};
}
