package com.goal98.flipdroid2.model.cachesystem;

import com.goal98.flipdroid2.client.LastModifiedStampedResult;
import com.goal98.flipdroid2.exception.NoNetworkException;
import com.goal98.flipdroid2.model.Article;
import com.goal98.flipdroid2.model.OnSourceLoadedListener;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 8/30/11
 * Time: 9:47 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class BaseCacheableArticleSource implements CacheableArticleSource {
    protected OnSourceLoadedListener listener;

    protected boolean loaded;

    protected LinkedList list = new LinkedList<Article>();

    protected byte[] cachedBytes;
    protected byte[] loadedBytes;
    protected long lastModified;
    protected InputStream content;
    private LastModifiedStampedResult lastModifiedStampedResult;

    public void registerOnLoadListener(OnSourceLoadedListener listener) {
        this.listener = listener;
    }

    public boolean isNoMoreToLoad() {
        if (loaded)
            return true;
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean getForceMagzine() {
        return true;
    }

    public boolean reset() {
        list.clear();
        loaded = false;
        return true;
    }

    public List<Article> getArticleList() {
        return list;
    }

    public LastModifiedStampedResult loadLatestSource() throws NoNetworkException {
        LastModifiedStampedResult loadedBytes = loadFromSource();
        if (loadedBytes == null)
            return null;


        this.cachedBytes = loadedBytes.getResult().toString().getBytes();
        return loadedBytes;
    }

    protected LastModifiedStampedResult loadFromSource() {
        lastModifiedStampedResult = getLatestSource();
        if (lastModifiedStampedResult != null) {
            loadedBytes = lastModifiedStampedResult.getResult().toString().getBytes();
            if (lastModified == 0) {//not from cache
                lastModified = new Date().getTime() - 2000;
            }
        }
        return lastModifiedStampedResult;
    }

    protected abstract LastModifiedStampedResult getLatestSource();

    public void fromCache(SourceCacheObject cachedObject) {
        cachedBytes = cachedObject.getContent().getBytes();
        lastModified = cachedObject.getLastModified();

        this.content = new ByteArrayInputStream(cachedBytes);
    }

    public boolean loadMore() {
        if (content == null) {
            if (loadFromSource() == null)
                return false;
            else {
                content = new ByteArrayInputStream(loadedBytes);
                try {
                    listener.onLoaded(lastModifiedStampedResult);
                } catch (IOException e) {
                    //ignore
                }
            }
        }
        if (load()) {
            loaded = true;

            return true;
        }
        return false;
    }

    protected abstract boolean load();
}
