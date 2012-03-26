package com.goal98.flipdroid.model;

import com.goal98.flipdroid.db.RSSURLDB;

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
    private String from;
    private int inDaysFrom;
    private int inDaysTo;

    public AllLocalArticleSource(RSSURLDB contentDB) {
        this(contentDB, null, -1, -1);
    }

    public AllLocalArticleSource(RSSURLDB contentDB, String from, int inDaysFrom, int inDaysTo) {
        this.contentDB = contentDB;
        this.from = from;
        this.inDaysFrom = inDaysFrom;
        this.inDaysTo = inDaysTo;
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
                totalNumber = contentDB.countByStatus(RSSURLDB.STATUS_NEW, inDaysFrom, inDaysTo);
            }

            loadedArticles = contentDB.findAllByStatus(RSSURLDB.STATUS_NEW, offset, inDaysFrom, inDaysTo);
        } else {
            if (totalNumber == 0) {
                totalNumber = contentDB.countByStatus(RSSURLDB.STATUS_NEW, from, inDaysFrom, inDaysTo);
            }
            loadedArticles = contentDB.findAllByStatus(RSSURLDB.STATUS_NEW, from, offset, inDaysFrom, inDaysTo);
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
