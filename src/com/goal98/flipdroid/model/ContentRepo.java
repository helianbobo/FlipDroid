package com.goal98.flipdroid.model;


import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class ContentRepo {

    private ArticleSource articleSource;
    private Date articleSourceLastModified;
    private PagingStretagy pagingStretagy;

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

    public PagingStretagy getPagingStretagy() {
        return pagingStretagy;
    }

    public void setPagingStretagy(PagingStretagy pagingStretagy) {
        this.pagingStretagy = pagingStretagy;
    }

    public Page getPage(int pageNo){

        if(articleSourceLastModified == null || articleSource.lastModified().after(articleSourceLastModified)){
            pageList = pagingStretagy.doPaging(articleSource.getArticleList());
            articleSourceLastModified = articleSource.lastModified();
        }

        return pageList.get(pageNo);

    }

}
