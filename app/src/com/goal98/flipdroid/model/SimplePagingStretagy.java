package com.goal98.flipdroid.model;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class SimplePagingStretagy implements PagingStretagy {

    private int articlePerpage = 5;

    public int getArticlePerpage() {
        return articlePerpage;
    }

    public void setArticlePerpage(int articlePerpage) {
        this.articlePerpage = articlePerpage;
    }

    public List<Page> doPaging(List<Article> articleList) {
        List<Page> result = new LinkedList<Page>();
        int numberOfPages = (int) Math.floor(articleList.size() / articlePerpage) + 1;

        Iterator<Article> iterator = articleList.iterator();

        for (int i = 0; i < numberOfPages; i++) {
            Page page = new Page();

            int j = 0;
            while (iterator.hasNext() && j < articlePerpage) {
                Article article = iterator.next();
                page.addArticle(article);
                j++;
            }

            result.add(page);

        }

        return result;
    }
}
