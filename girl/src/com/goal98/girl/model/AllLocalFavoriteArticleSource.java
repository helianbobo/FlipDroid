package com.goal98.girl.model;

import com.goal98.girl.db.RSSURLDB;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2/2/12
 * Time: 11:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class AllLocalFavoriteArticleSource implements ArticleSource {
    private List<Article> articles = new ArrayList<Article>();
    RSSURLDB contentDB;
    private boolean isNoMoreToLoad;
    private int offset = 0;
    private int totalNumber = 0;
    private String from;


    public AllLocalFavoriteArticleSource(RSSURLDB contentDB, String from) {
        this.contentDB = contentDB;
        this.from = from;
    }

    public Date lastModified() {
        return new Date();  //To change body of implemented methods use File | Settings | File Templates.
    }

    public List<Article> getArticleList() {
        return articles;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean loadMore() {
        contentDB.open();
        List<Article> loadedArticles = null;
        if (from == null) {
            if (totalNumber == 0) {
                totalNumber = contentDB.countByFavorite();
            }

            loadedArticles = contentDB.findAllFavorite(offset);
        } else {
            if (totalNumber == 0) {
                totalNumber = contentDB.countByFavorite(from);
            }
            loadedArticles = contentDB.findAllFavorite(from, offset);
        }
        contentDB.close();
        if (loadedArticles == null) {
            isNoMoreToLoad = true;
            return false;
        }

        offset += RSSURLDB.recordPerPage;
        if (offset > totalNumber) {
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
        return isNoMoreToLoad = false;
    }
}
