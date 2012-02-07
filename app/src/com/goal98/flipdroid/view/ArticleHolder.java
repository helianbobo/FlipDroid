package com.goal98.flipdroid.view;

import com.goal98.flipdroid.model.Article;

/**
 * Created by IntelliJ IDEA.
 * User: jleo
 * Date: 12-2-7
 * Time: 下午11:23
 * To change this template use File | Settings | File Templates.
 */
public class ArticleHolder {
    private Article article;
    private static ArticleHolder holder;

    private ArticleHolder() {

    }

    public void setArticle(Article article) {
        this.article = article;
    }


    public static synchronized ArticleHolder getInstance() {
        if (holder == null) {
            holder = new ArticleHolder();
        }
        return holder;
    }

    public Article get() {

        return article;
    }
}


