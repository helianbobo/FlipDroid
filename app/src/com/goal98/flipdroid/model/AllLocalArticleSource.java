package com.goal98.flipdroid.model;

import com.goal98.flipdroid.client.FeedJSONParser;
import com.goal98.flipdroid.client.TikaClientException;
import com.goal98.flipdroid.client.TikaExtractResponse;
import com.goal98.flipdroid.db.RSSURLDB;
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
    RSSURLDB contentDB;
    private boolean isNoMoreToLoad;
    private int offset = 0;
    private int totalNumber = 0;

    public AllLocalArticleSource(RSSURLDB contentDB) {
        this.contentDB = contentDB;
        ;
    }

    public Date lastModified() {
        return new Date();  //To change body of implemented methods use File | Settings | File Templates.
    }

    public List<Article> getArticleList() {
        return articles;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean loadMore() {
        if(totalNumber == 0){
            totalNumber = contentDB.countByStatus(RSSURLDB.STATUS_NEW);
        }
        List<Article> loadedArticles = contentDB.findAllByStatus(RSSURLDB.STATUS_NEW, offset);
        if(loadedArticles==null){
            isNoMoreToLoad = true;
            return false;
        }

        offset += RSSURLDB.recordPerPage;
        if(offset > totalNumber){
            isNoMoreToLoad = true;
        }
        this.articles.addAll(loadedArticles);
        Collections.sort(articles);
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
        offset = 0;
        return isNoMoreToLoad=false;
    }
}
