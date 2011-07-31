package com.goal98.flipdroid.model.cachesystem;

import android.content.Context;
import com.goal98.flipdroid.activity.PageActivity;
import com.goal98.flipdroid.db.SourceContentDB;
import com.goal98.flipdroid.model.Article;
import com.goal98.flipdroid.model.ArticleSource;
import com.goal98.flipdroid.model.OnSourceLoadedListener;
import com.goal98.flipdroid.model.rss.RSSArticleSource;
import com.goal98.flipdroid.util.Constants;

import java.io.ByteArrayInputStream;
import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: ITS
 * Date: 11-7-29
 * Time: 上午9:10
 * To change this template use File | Settings | File Templates.
 */
public class CachedArticleSource implements ArticleSource {
    private CacheableArticleSource articleSource;
    private SourceCache dbCache;
    private PageActivity pageActivity;
    private boolean cacheFound;

    public CachedArticleSource(final CacheableArticleSource articleSource, PageActivity pageActivity) {
        this.dbCache = new SourceCache(pageActivity);
        this.pageActivity = pageActivity;
        this.articleSource = articleSource;
        final SourceCacheObject cacheObject = dbCache.find(articleSource.getCacheToken().getType(), articleSource.getCacheToken().getToken());

        if (cacheObject != null){
            this.articleSource.fromCache(cacheObject);
            cacheFound = true;
        }

        articleSource.registerOnLoadListener(new OnSourceLoadedListener() {
            public String onLoaded(String s) {
                dbCache.put(articleSource.getCacheToken().getType(), articleSource.getCacheToken().getToken(), s);
                return s;
            }
        });

    }

    public Date lastModified() {
        return articleSource.lastModified();
    }

    public List<Article> getArticleList() {
        return articleSource.getArticleList();
    }

    volatile  boolean updating = false;
    volatile  boolean updated = false;

    public synchronized boolean loadMore() {
        if (!updating && !updated && cacheFound) {
            new Thread(new Runnable() {
                public void run() {
                    updating = true;
                    checkUpdate();
                }
            }).start();
        }
        return articleSource.loadMore();
    }

    public boolean isNoMoreToLoad() {
        return articleSource.isNoMoreToLoad();
    }

    private void checkUpdate() {
        boolean hasUpdates = articleSource.loadLatestSource();
        this.updated = true;
        System.out.println("has update:" + hasUpdates);
        if (hasUpdates) {
            pageActivity.notifyHasNew();
            this.updating = false;
        }
    }

    public void setUpdating(boolean updating) {
        this.updating = updating;
    }

    public boolean getForceMagzine() {
        return articleSource.getForceMagzine();
    }

    public boolean reset() {
        return articleSource.reset();  //To change body of implemented methods use File | Settings | File Templates.
    }
}
