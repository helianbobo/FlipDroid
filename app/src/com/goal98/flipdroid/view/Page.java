package com.goal98.flipdroid.view;

import com.goal98.flipdroid.activity.PageActivity;
import com.goal98.flipdroid.model.Article;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class Page {
    protected int heightSum;

    protected WeiboPageView weiboPageView;

    protected List<Article> articleList;

    public Page(PageActivity activity) {
        this.articleList = new LinkedList<Article>();

        weiboPageView = new WeiboPageView(activity);
    }

    public List<Article> getArticleList() {
        return articleList;
    }

    public boolean addArticle(final Article article) {
        articleList.add(article);
        return true;
    }

    //must be called from UI thread
    public WeiboPageView getWeiboPageView() {
        weiboPageView.setPage(this);
        return weiboPageView;
    }
}
