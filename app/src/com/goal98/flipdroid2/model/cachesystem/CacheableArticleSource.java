package com.goal98.flipdroid2.model.cachesystem;

import com.goal98.flipdroid2.client.LastModifiedStampedResult;
import com.goal98.flipdroid2.model.Article;
import com.goal98.flipdroid2.model.ArticleSource;
import com.goal98.flipdroid2.model.OnSourceLoadedListener;

import java.util.List;

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

    void registerOnLoadListener(OnSourceLoadedListener listener);

    LastModifiedStampedResult loadLatestSource();

    String getImageUrl();

    String getAuthor();

    List<Article> contentToArticles(String content);
}
