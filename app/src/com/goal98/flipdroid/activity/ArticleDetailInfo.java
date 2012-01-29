package com.goal98.flipdroid.activity;

import com.goal98.flipdroid.model.Article;

/**
 * Created by IntelliJ IDEA.
 * User: janexie
 * Date: 12-1-26
 * Time: 下午8:29
 * To change this template use File | Settings | File Templates.
 */
public class ArticleDetailInfo extends DetailInfo{
    private Article article;

    public ArticleDetailInfo(Article article){
        this.article = article;
    }

    public Article getArticle() {
        return article;
    }
}
