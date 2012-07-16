package com.goal98.girl.view;

import com.goal98.girl.model.Article;

import java.util.ArrayList;
import java.util.List;

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
    private List<Article> articles;
    private int current;

    private ArticleHolder() {
        current = 0;
        articles = new ArrayList<Article>();
    }

    public void setArticle(Article article) {
        this.article = article;
        current = articles.indexOf(article);
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

    public void setArticles(List<Article> articles) {
        this.articles = articles;
    }

    public Article getNextArticle(){
        if(articles.size() <= current+1){
            return null;
        }
        Article article = articles.get(current + 1);
        this.setArticle(article);
        return article;
    }
}


