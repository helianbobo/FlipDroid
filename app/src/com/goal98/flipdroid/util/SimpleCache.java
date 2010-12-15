package com.goal98.flipdroid.util;

import java.util.HashMap;
import java.util.Map;

public class SimpleCache implements Cache{


    private Map cache = new HashMap();

    public Object get(Object key) {
        return cache.get(key);
    }

    public void put(Object key, Object value) {
        cache.put(key, value);
    }
}
