package com.goal98.flipdroid.model;

import com.goal98.flipdroid.client.FeedJSONParser;
import com.goal98.flipdroid.client.TikaClientException;
import com.goal98.flipdroid.client.TikaExtractResponse;
import com.goal98.flipdroid.db.SourceContentDB;
import com.goal98.flipdroid.model.cachesystem.SourceCacheObject;
import com.goal98.tika.common.TikaConstants;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2/2/12
 * Time: 11:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class AllLocalArticleSource implements ArticleSource {
    private List<Article> articles = new ArrayList<Article>();
    SourceContentDB contentDB;
    FeedJSONParser jsonParser = new FeedJSONParser();
    private boolean isNoMoreToLoad;

    public AllLocalArticleSource(SourceContentDB contentDB) {
        this.contentDB = contentDB;
    }

    public Date lastModified() {
        return new Date();  //To change body of implemented methods use File | Settings | File Templates.
    }

    public List<Article> getArticleList() {
        return articles;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean loadMore() {
        List<SourceCacheObject> sourceCacheObjects = contentDB.findAllByType(TikaConstants.TYPE_RSS);
        if(sourceCacheObjects==null){
            isNoMoreToLoad = true;
            return false;
        }
        int sourceCacheObjectSize = sourceCacheObjects.size();
        for (int i = 0; i < sourceCacheObjectSize; i++) {
            SourceCacheObject sourceCacheObject = sourceCacheObjects.get(i);
            List<TikaExtractResponse> tikaExtractResponses = jsonParser.getFeedsFromFeedJSON(sourceCacheObject.getContent());
            int tikaExtractResponsesSize = tikaExtractResponses.size();
            for (int j = 0; j < tikaExtractResponsesSize; j++) {
                Article article = new Article();
                article.setSourceType(TikaConstants.TYPE_RSS);
                TikaExtractResponse tikaExtractResponse = tikaExtractResponses.get(j);

                article.setAuthor(sourceCacheObject.getAuthor());

                try {
                    article.setPortraitImageUrl(new URL(sourceCacheObject.getImageUrl()));
                    jsonParser.toArticle(tikaExtractResponse, article);
                } catch (MalformedURLException e) {

                }
                articles.add(article);
            }

        }
        Collections.sort(articles);
        isNoMoreToLoad = true;
        return true;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean isNoMoreToLoad() {
        return isNoMoreToLoad;  //To change body of implemented methods use File | Settings | File Templates.
    }


    public boolean getForceMagzine() {
        return true;
    }

    public boolean reset() {
        articles.clear();
        return isNoMoreToLoad=false;
    }
}
