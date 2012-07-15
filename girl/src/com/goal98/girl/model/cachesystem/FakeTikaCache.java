package com.goal98.girl.model.cachesystem;

import com.goal98.girl.client.TikaExtractResponse;

import java.net.URL;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 4/9/11
 * Time: 2:38 PM
 * To change this template use File | Settings | File Templates.
 */
public class FakeTikaCache implements TikaCache {
    public TikaExtractResponse load(URL url){

       return null;
    }

    public void put(String key, TikaExtractResponse extractResponse){

    }

    public void shutdown() {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
