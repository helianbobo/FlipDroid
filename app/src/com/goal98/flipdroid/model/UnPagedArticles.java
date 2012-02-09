package com.goal98.flipdroid.model;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: lsha6086
 * Date: 3/24/11
 * Time: 1:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class UnPagedArticles{
    List<Article> articleList;

    private int pagedTo = 0;

    public UnPagedArticles(List<Article> articleList) {
        this.articleList = articleList;
    }


    public void setArticles(List<Article> articleList) {
        this.articleList = articleList;
    }

    public List<Article> getArticleList() {
        return articleList;
    }

    public int getPagedTo() {
        return pagedTo;
    }

    public void setPagedTo(int pagedTo) {
        this.pagedTo = pagedTo;
    }

    public void clear() {
        articleList.clear();
    }
}
