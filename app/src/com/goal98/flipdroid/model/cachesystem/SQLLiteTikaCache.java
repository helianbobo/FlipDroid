package com.goal98.flipdroid.model.cachesystem;

import android.content.Context;
import com.goal98.flipdroid.client.TikaExtractResponse;
import com.goal98.flipdroid.db.URLDB;
import com.goal98.flipdroid.util.Cache;

import java.net.URL;

/**
 * Created by IntelliJ IDEA.
 * User: ITS
 * Date: 11-7-26
 * Time: 下午4:18
 * To change this template use File | Settings | File Templates.
 */
public class SQLLiteTikaCache implements TikaCache {
    private static URLDB urldb;
    private static SQLLiteTikaCache cache;
    private static volatile boolean shutdown;

    private SQLLiteTikaCache(Context context) {
        urldb = new URLDB(context);
    }

    public synchronized TikaExtractResponse load(URL url) {
        TikaExtractResponse response = urldb.findByURL(url.toExternalForm());
        return response;
    }

    public synchronized void put(String url, TikaExtractResponse extractResponse) {
        urldb.insert(url, extractResponse);
    }

    public synchronized void shutdown() {
        cache = null;
        shutdown = true;
        urldb.close();
        urldb=null;
    }

    public static synchronized TikaCache getInstance(Context context) {
        if (cache == null) {
            cache = new SQLLiteTikaCache(context);
        }
        if(shutdown){
            shutdown = false;
            urldb = new URLDB(context);
        }
        return cache;
    }
}
