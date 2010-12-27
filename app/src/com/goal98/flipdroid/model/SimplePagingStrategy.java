package com.goal98.flipdroid.model;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class SimplePagingStrategy implements PagingStrategy {

    private int articlePerPage = 5;

    public SimplePagingStrategy() {
    }

    public SimplePagingStrategy(int articlePerPage) {
        this.articlePerPage = articlePerPage;
    }

    public int getArticlePerPage() {
        return articlePerPage;
    }

    public void setArticlePerPage(int articlePerPage) {
        this.articlePerPage = articlePerPage;
    }

    public List<Page> doPaging(List<Article> articleList) {
        List<Page> result = new LinkedList<Page>();
        int numberOfPages = (int) Math.floor(articleList.size() / articlePerPage)
                + (articleList.size() % articlePerPage == 0?0:1);

        Iterator<Article> iterator = articleList.iterator();

        for (int i = 0; i < numberOfPages; i++) {
            Page page = new Page();

            int j = 0;
            while (iterator.hasNext() && j < articlePerPage) {
                Article article = iterator.next();
                page.addArticle(article);
                j++;
            }

            result.add(page);

        }

        return result;
    }
}
