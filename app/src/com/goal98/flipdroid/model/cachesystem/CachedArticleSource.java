package com.goal98.flipdroid.model.cachesystem;

import android.content.Context;
import com.goal98.flipdroid.model.Article;
import com.goal98.flipdroid.model.ArticleSource;
import com.goal98.flipdroid.model.OnSourceLoadedListener;
import com.goal98.flipdroid.util.EncodingDetector;

import java.io.ByteArrayInputStream;
import java.io.IOException;
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
    private SourceUpdateable sourceUpdateable;
    private OnSourceLoadedListener listener;

    public CachedArticleSource(final CacheableArticleSource articleSource, Context context, SourceUpdateable sourceUpdateable, SourceCache dbCache) {
        this.dbCache = dbCache;
        this.sourceUpdateable = sourceUpdateable;
        this.articleSource = articleSource;
        this.listener = new OnSourceLoadedListener() {
            public String onLoaded(byte[] updatedBytes) throws IOException {
                String encoding = EncodingDetector.detect(new ByteArrayInputStream(updatedBytes));
                String content = new String(updatedBytes, encoding);
                CachedArticleSource.this.dbCache.put(articleSource.getCacheToken().getType(), articleSource.getCacheToken().getToken(), content);
                return content;
            }
        };
        articleSource.registerOnLoadListener(listener);
    }

    public CacheToken getToken() {
        return articleSource.getCacheToken();
    }

    public void loadSourceFromCache() {
        final SourceCacheObject cacheObject = dbCache.find(articleSource.getCacheToken().getType(), articleSource.getCacheToken().getToken());

        if (cacheObject != null) {
            this.articleSource.fromCache(cacheObject);
        }


    }

    public Date lastModified() {
        return articleSource.lastModified();
    }

    public List<Article> getArticleList() {
        return articleSource.getArticleList();
    }

    volatile boolean updating = false;
    volatile boolean updated = false;

    public synchronized boolean loadMore() {
        return articleSource.loadMore();
    }

    public boolean isNoMoreToLoad() {
        return articleSource.isNoMoreToLoad();
    }

    public void checkUpdate() {
        new Thread(new Runnable() {
            public void run() {
                updating = true;
                try {
                    sourceUpdateable.notifyUpdating(CachedArticleSource.this);
                    byte[] updatedBytes = articleSource.loadLatestSource();
                    updated = true;
                    System.out.println("has update:" + updatedBytes != null);
                    if (updatedBytes != null) {
                        listener.onLoaded(updatedBytes);
                        sourceUpdateable.notifyHasNew(CachedArticleSource.this);
                    } else {
                        sourceUpdateable.notifyNoNew(CachedArticleSource.this);
                    }
                    sourceUpdateable.notifyUpdateDone(CachedArticleSource.this);
                } catch (IOException e) {
                } finally {
                    updating = false;
                }
            }
        }).start();
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