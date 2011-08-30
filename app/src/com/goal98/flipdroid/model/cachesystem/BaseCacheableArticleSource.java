package com.goal98.flipdroid.model.cachesystem;

import com.goal98.flipdroid.exception.NoNetworkException;
import com.goal98.flipdroid.model.Article;
import com.goal98.flipdroid.model.OnSourceLoadedListener;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
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

    protected InputStream content;

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
        return list;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public byte[] loadLatestSource() throws NoNetworkException {
        byte[] loadedBytes = loadFromSource();
        if (loadedBytes == null)
            return null;

        byte[] latestSource = this.loadedBytes;
        boolean needUpdate = false;
        if (cachedBytes != null) {
            for (int i = 0; i < latestSource.length; i++) {
                if (i >= cachedBytes.length) {
                    needUpdate = true;
                    break;
                }
                if (latestSource[i] != cachedBytes[i]) {
                    needUpdate = true;
                    break;
                }
            }
        } else {
            needUpdate = true;
        }

        if (needUpdate) {
            this.cachedBytes = loadedBytes;
            return loadedBytes;
        }
        return null;
    }

    protected byte[] loadFromSource(){
        loadedBytes = getLatestSource();
        return loadedBytes;
    }

    protected abstract byte[] getLatestSource();

    public void fromCache(SourceCacheObject cachedObject) {
        cachedBytes = cachedObject.getContent().getBytes();
        this.content = new ByteArrayInputStream(cachedBytes);
    }

    public boolean loadMore(){
        if (content == null) {
            if (loadFromSource() == null)
                return false;
            else {
                content = new ByteArrayInputStream(loadedBytes);
                try {
                    listener.onLoaded(loadedBytes);
                } catch (IOException e) {
                    //ignore
                }
            }
        }
        if(load()){
            loaded = true;
            return true;
        }
        return false;
    }

    protected abstract boolean load();
}
