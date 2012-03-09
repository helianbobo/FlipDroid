package com.goal98.flipdroid.model.cachesystem;

import android.content.Context;
import android.util.Log;
import com.goal98.flipdroid.client.LastModifiedStampedResult;
import com.goal98.flipdroid.db.RSSURLDB;
import com.goal98.flipdroid.model.Article;
import com.goal98.flipdroid.model.ArticleSource;
import com.goal98.flipdroid.model.OnSourceLoadedListener;
import com.goal98.flipdroid.util.EncodingDetector;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
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
    private String TAG = this.getClass().getName();

    public CachedArticleSource(final CacheableArticleSource articleSource, SourceUpdateable sourceUpdateable, SourceCache dbCache, final RSSURLDB rssurlDB) {
        this.dbCache = dbCache;
        this.sourceUpdateable = sourceUpdateable;
        this.articleSource = articleSource;
        this.listener = new OnSourceLoadedListener() {
            public String onLoaded(LastModifiedStampedResult updatedBytes) throws IOException {
                byte[] bytes = updatedBytes.getResult().toString().getBytes();
                String encoding = EncodingDetector.detect(new ByteArrayInputStream(bytes));
                String content = new String(bytes, encoding);
                CachedArticleSource.this.dbCache.put(articleSource.getCacheToken().getType(), articleSource.getCacheToken().getToken(), content, updatedBytes.getLastModified(), articleSource.getImageUrl(), articleSource.getAuthor());

                List<Article> articles = articleSource.contentToArticles(content);
                for (int i = 0; i < articles.size(); i++) {
                    Article article =  articles.get(i);
                    article.setFrom(articleSource.getCacheToken().getToken());
                    rssurlDB.insert(article);
                }
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

        if (cacheObject != null && cacheObject.getContent().trim().length() != 0) {
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

    public Thread checkUpdate(boolean block) {
        Thread t = new Thread(new Runnable() {
            public void run() {

                updating = true;
                try {
                    Log.d(TAG, "checking update");
                    sourceUpdateable.notifyUpdating(CachedArticleSource.this);

                    LastModifiedStampedResult updatedBytes = articleSource.loadLatestSource();
                    updated = true;
                    Log.d(TAG, "has update:" + (updatedBytes != null));
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
        });
        t.start();
        if (block) {
            try {
                t.join();
            } catch (InterruptedException e) {

            }
        }
        return t;
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