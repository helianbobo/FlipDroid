package com.goal98.flipdroid.model;

import java.util.LinkedList;
import java.util.List;

public class Page {

    private List<Article> articleList;

    public Page() {
        this.articleList = new LinkedList<Article>();
    }

    public List<Article> getArticleList() {
        return articleList;
    }


    public void addArticle(Article article){
        articleList.add(article);
    }
}
