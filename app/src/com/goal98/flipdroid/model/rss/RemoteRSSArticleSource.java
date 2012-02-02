package com.goal98.flipdroid.model.rss;

import com.goal98.flipdroid.model.Article;
import com.goal98.flipdroid.model.RemoteArticleSource;
import com.goal98.flipdroid.model.cachesystem.CacheToken;
import com.goal98.tika.common.TikaConstants;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 1/14/12
 * Time: 12:36 AM
 * To change this template use File | Settings | File Templates.
 */
public class RemoteRSSArticleSource extends RemoteArticleSource {
    public RemoteRSSArticleSource(String remoteSourceToken, String sourceName, String sourceImage) {
        super(remoteSourceToken, sourceName, sourceImage);
    }

    public CacheToken getCacheToken() {
        CacheToken token = new CacheToken();
        token.setType(TikaConstants.TYPE_RSS);
        token.setToken(this.remoteSourceToken);
        return token;
    }




    protected void setSourceType(Article article) {
        article.setSourceType(TikaConstants.TYPE_RSS);
    }
}
