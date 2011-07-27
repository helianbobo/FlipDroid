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
    private Context context;
    private URLDB urldb;
    private static SQLLiteTikaCache cache;
    private static volatile boolean shutdown;

    private SQLLiteTikaCache(Context context) {
        this.context = context;
        urldb = new URLDB(context);
    }

    public TikaExtractResponse load(URL url) {
        TikaExtractResponse response = urldb.findByURL(url.toExternalForm());
        return response;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void put(String url, TikaExtractResponse extractResponse) {
        urldb.insert(url, extractResponse);
    }

    public void shutdown() {
        urldb.close();
        shutdown = true;
    }

    public static synchronized TikaCache getInstance(Context context) {
        if (cache == null || shutdown) {
            cache = new SQLLiteTikaCache(context);
        }
        return cache;
    }
}
