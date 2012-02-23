package com.goal98.flipdroid.model.cachesystem;

import android.content.Context;
import com.goal98.flipdroid.db.SourceContentDB;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by IntelliJ IDEA.
 * User: ITS
 * Date: 11-7-29
 * Time: 上午9:37
 * To change this template use File | Settings | File Templates.
 */
public class SourceCache {
    private SourceContentDB contentDB;
    private static Lock lock = new ReentrantLock();

    public SourceCache(Context context) {
        contentDB = new SourceContentDB(context);
    }

    public void put(String type, String url, String content, long lastModified, String imageUrl, String author) {
        lock.lock();
        try{
        SourceCacheObject cacheObject = new SourceCacheObject();
        cacheObject.setType(type);
        cacheObject.setUrl(url);
        cacheObject.setContent(content);
        cacheObject.setLastModified(lastModified);
        cacheObject.setImageUrl(imageUrl);
        cacheObject.setAuthor(author);
        contentDB.persist(cacheObject);
        }finally {
            lock.unlock();
        }
    }

    public synchronized SourceCacheObject find(String type, String url) {
        lock.lock();
        try{
        SourceCacheObject cacheObject = new SourceCacheObject();
        cacheObject.setType(type);
        cacheObject.setUrl(url);

        cacheObject = contentDB.findByURL(cacheObject);
        if (cacheObject != null)
            return cacheObject;
        return null;
        }finally {
            lock.unlock();
        }
    }

    public synchronized void clear(String type, String url) {
        lock.lock();
        try{
        SourceCacheObject cacheObject = new SourceCacheObject();
        cacheObject.setType(type);
        cacheObject.setUrl(url);

        contentDB.clear(cacheObject);
        }finally {
            lock.unlock();
        }
    }

    public void close() {
        contentDB.close();
    }
}
