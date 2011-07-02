package com.goal98.flipdroid.model;


import java.util.Date;
import java.util.List;

public interface ArticleSource {

    public Date lastModified();

    public List<Article> getArticleList();

    public boolean loadMore();

    boolean isNoMoreToLoad();

    boolean getForceMagzine();
}
