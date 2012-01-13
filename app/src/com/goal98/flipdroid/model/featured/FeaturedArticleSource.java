package com.goal98.flipdroid.model.featured;

import com.goal98.flipdroid.client.LastModifiedStampedResult;
import com.goal98.flipdroid.model.cachesystem.BaseCacheableArticleSource;
import com.goal98.flipdroid.model.cachesystem.CacheToken;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 1/14/12
 * Time: 12:12 AM
 * To change this template use File | Settings | File Templates.
 */
public class FeaturedArticleSource extends BaseCacheableArticleSource {
    protected LastModifiedStampedResult getLatestSource() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    protected boolean load() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public CacheToken getCacheToken() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Date lastModified() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
