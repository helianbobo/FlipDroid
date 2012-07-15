package com.goal98.flipdroid2.activity;

import com.goal98.flipdroid2.model.Article;

/**
 * Created by IntelliJ IDEA.
 * User: janexie
 * Date: 12-1-26
 * Time: 下午8:29
 * To change this template use File | Settings | File Templates.
 */
public class ArticleDetailInfo extends DetailInfo implements Comparable {
    private Article article;

    public ArticleDetailInfo(Article article) {
        this.article = article;
    }

    public Article getArticle() {
        return article;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ArticleDetailInfo that = (ArticleDetailInfo) o;

        if (article != null ? !article.equals(that.article) : that.article != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return article != null ? article.hashCode() : 0;
    }

    @Override
    public int compareTo(Object o) {
        return article.compareTo(((ArticleDetailInfo) o).getArticle());
    }
}
