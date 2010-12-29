package com.goal98.flipdroid.model;


import android.util.Log;
import com.goal98.flipdroid.exception.NoMorePageException;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class ContentRepo {

    private ArticleSource articleSource;
    private Date articleSourceLastModified;
    private PagingStrategy pagingStrategy;

    private List<Page> pageList;

    public ContentRepo() {
        pageList = new LinkedList<Page>();
    }

    public ArticleSource getArticleSource() {
        return articleSource;
    }

    public void setArticleSource(ArticleSource articleSource) {
        this.articleSource = articleSource;
    }

    public PagingStrategy getPagingStrategy() {
        return pagingStrategy;
    }

    public void setPagingStrategy(PagingStrategy pagingStrategy) {
        this.pagingStrategy = pagingStrategy;
    }

    public Page getPage(int pageNo) throws NoMorePageException {

        if (pageNo >= pageList.size()) {
            throw new NoMorePageException();
        }

        return pageList.get(pageNo);

    }

    public void refresh() {

        articleSource.loadMore();

        if (articleSourceLastModified == null || articleSource.lastModified().after(articleSourceLastModified)) {
            pageList = pagingStrategy.doPaging(articleSource.getArticleList());
            articleSourceLastModified = articleSource.lastModified();
        }

    }

}
