package com.goal98.flipdroid.model.cachesystem;

import com.goal98.flipdroid.client.TikaResponse;

import java.net.URL;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 4/9/11
 * Time: 2:38 PM
 * To change this template use File | Settings | File Templates.
 */
public class FakeTikaCache implements TikaCache {
    public TikaResponse load(URL url){
       return null;
    }

    public void put(String key, TikaResponse response){

    }
}
