package com.goal98.flipdroid.model.cachesystem;

import com.goal98.flipdroid.model.ArticleSource;
import com.goal98.flipdroid.model.OnSourceLoadedListener;

/**
 * Created by IntelliJ IDEA.
 * User: ITS
 * Date: 11-7-29
 * Time: 上午11:50
 * To change this template use File | Settings | File Templates.
 */
public interface CacheableArticleSource extends ArticleSource {
    CacheToken getCacheToken();

    void fromCache(SourceCacheObject cachedObject);

//    void registerOnLoadListener(OnSourceLoadedListener listener);

    byte[] loadLatestSource();
}
