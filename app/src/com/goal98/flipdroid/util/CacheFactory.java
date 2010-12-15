package com.goal98.flipdroid.util;

import java.util.HashMap;
import java.util.Map;

public class CacheFactory {


    private Map<String, Cache> cacheMap = new HashMap<String, Cache>();

    private CacheFactory(){

    }

    private static CacheFactory instance;

    public static CacheFactory getInstance(){
        if(instance == null)
            instance = new CacheFactory();
        return instance;
    }

    public Cache getCache(String name){
        if(!cacheMap.containsKey(name)){
            cacheMap.put(name, new SimpleCache());
        }
        return cacheMap.get(name);
    }

}
