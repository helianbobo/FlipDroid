//package com.goal98.flipdroid.model.rss;
//
//import com.goal98.flipdroid.model.Article;
//import com.goal98.flipdroid.model.RemoteArticleSource;
//import com.goal98.flipdroid.model.cachesystem.CacheToken;
//import com.goal98.tika.common.TikaConstants;
//
///**
// * Created with IntelliJ IDEA.
// * User: jleo
// * Date: 12-6-2
// * Time: 下午7:40
// * To change this template use File | Settings | File Templates.
// */
//public class RemoteFeaturedArticleSource extends RemoteArticleSource {
//    public RemoteFeaturedArticleSource(String remoteSourceToken, String sourceName, String sourceImage) {
//        super(remoteSourceToken, sourceName, sourceImage);
//    }
//
//    public CacheToken getCacheToken() {
//        CacheToken token = new CacheToken();
//        token.setType(TikaConstants.TYPE_FEATURED);
//        token.setToken(this.remoteSourceToken);
//        return token;
//    }
//
//    protected void setSourceType(Article article) {
//        article.setSourceType(TikaConstants.TYPE_FEATURED);
//    }
//}
