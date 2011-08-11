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
    private Context context;
    private SourceContentDB contentDB;

    public SourceCache(Context context) {
        this.context = context;
        contentDB = new SourceContentDB(context);
    }

    public void put(String type, String url, String content) {
        SourceCacheObject cacheObject = new SourceCacheObject();
        cacheObject.setType(type);
        cacheObject.setUrl(url);
        cacheObject.setContent(content);

        contentDB.persist(cacheObject);
    }

    public SourceCacheObject find(String type, String url) {
        SourceCacheObject cacheObject = new SourceCacheObject();
        cacheObject.setType(type);
        cacheObject.setUrl(url);

        cacheObject = contentDB.findByURL(cacheObject);
        if(cacheObject!=null)
            return cacheObject;
        return null;
    }

    public void clear(String type, String url){
        SourceCacheObject cacheObject = new SourceCacheObject();
        cacheObject.setType(type);
        cacheObject.setUrl(url);

        contentDB.clear(cacheObject);
    }
}
