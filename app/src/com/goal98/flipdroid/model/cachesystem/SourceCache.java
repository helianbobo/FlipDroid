package com.goal98.flipdroid.model.cachesystem;

import android.content.Context;
import com.goal98.flipdroid.db.SourceContentDB;

/**
 * Created by IntelliJ IDEA.
 * User: ITS
 * Date: 11-7-29
 * Time: 上午9:37
 * To change this template use File | Settings | File Templates.
 */
public class SourceCache {
    private SourceContentDB contentDB;
    private static SourceCache sourceCache;

    public synchronized static SourceCache getInstance(Context context) {
        if (sourceCache == null) {
            sourceCache = new SourceCache(context);
        }
        return sourceCache;
    }

    private SourceCache(Context context) {
        contentDB = new SourceContentDB(context);
    }

    public synchronized void put(String type, String url, String content) {
        SourceCacheObject cacheObject = new SourceCacheObject();
        cacheObject.setType(type);
        cacheObject.setUrl(url);
        cacheObject.setContent(content);

        contentDB.persist(cacheObject);
    }

    public synchronized SourceCacheObject find(String type, String url) {
        SourceCacheObject cacheObject = new SourceCacheObject();
        cacheObject.setType(type);
        cacheObject.setUrl(url);

        cacheObject = contentDB.findByURL(cacheObject);
        if (cacheObject != null)
            return cacheObject;
        return null;
    }

    public synchronized void clear(String type, String url) {
        SourceCacheObject cacheObject = new SourceCacheObject();
        cacheObject.setType(type);
        cacheObject.setUrl(url);

        contentDB.clear(cacheObject);
    }

    public void close() {
        contentDB.close();
    }
}
