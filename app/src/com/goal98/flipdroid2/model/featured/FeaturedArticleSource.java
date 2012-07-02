package com.goal98.flipdroid2.model.featured;

import com.goal98.flipdroid2.client.LastModifiedStampedResult;
import com.goal98.flipdroid2.client.TikaClientException;
import com.goal98.flipdroid2.model.Article;
import com.goal98.flipdroid2.model.RemoteArticleSource;
import com.goal98.flipdroid2.model.cachesystem.CacheToken;
import com.goal98.tika.common.TikaConstants;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 1/14/12
 * Time: 12:12 AM
 * To change this template use File | Settings | File Templates.
 */
public class FeaturedArticleSource extends RemoteArticleSource {

    public FeaturedArticleSource(String remoteSourceToken, String sourceName, String sourceImage) {
        super(remoteSourceToken, sourceName, sourceImage);
    }

    public CacheToken getCacheToken() {
        CacheToken token = new CacheToken();
        token.setType(TikaConstants.TYPE_FEATURED);
        token.setToken(this.remoteSourceToken);
        return token;
    }

    protected LastModifiedStampedResult getLatestSource() {
        try {
            LastModifiedStampedResult feedJSON = tikaClient.getFeaturedJSON(remoteSourceToken, lastModified);
            if (feedJSON == null)
                return null;
            return feedJSON;
        } catch (TikaClientException e) {
            return null;
        }
    }

    protected void setSourceType(Article article) {
        article.setSourceType(TikaConstants.TYPE_FEATURED);
    }
}
