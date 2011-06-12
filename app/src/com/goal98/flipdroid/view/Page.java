package com.goal98.flipdroid.view;

import com.goal98.flipdroid.activity.PageActivity;
import com.goal98.flipdroid.model.Article;

import java.util.LinkedList;
import java.util.List;

public class Page {
    private int heightSum;

    private WeiboPageView weiboPageView;

    private List<Article> articleList;
    private PageActivity activity;

    public Page(PageActivity activity) {
        this.articleList = new LinkedList<Article>();

        weiboPageView = new WeiboPageView(activity);
        this.activity = activity;
    }

    public List<Article> getArticleViewList() {
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
