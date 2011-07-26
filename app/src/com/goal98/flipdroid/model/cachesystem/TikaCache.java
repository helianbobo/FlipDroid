package com.goal98.flipdroid.model.cachesystem;

import com.goal98.flipdroid.client.TikaExtractResponse;

import java.net.URL;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 4/9/11
 * Time: 2:38 PM
 * To change this template use File | Settings | File Templates.
 */
public interface TikaCache {
    public TikaExtractResponse load(URL url);

    public void put(String key, TikaExtractResponse extractResponse);

    public void shutdown();
}
