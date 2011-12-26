package it.tika;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 12/26/11
 * Time: 7:30 PM
 * To change this template use File | Settings | File Templates.
 */
public class FeedCache {
    private static FeedCache feedCache = new FeedCache();
    private Map<String, CachedContent> feedContentMap = new HashMap<String, CachedContent>();

    private FeedCache(){

    }

    public CachedContent get(Object key) {
        return feedContentMap.get(key);
    }

    public CachedContent put(String key, CachedContent value) {
        return feedContentMap.put(key, value);
    }

    public boolean containsKey(Object key) {
        return feedContentMap.containsKey(key);
    }

    public static FeedCache getInstance(){
        return feedCache;
    }


}
