package com.goal98.flipdroid.view;

import com.goal98.flipdroid.activity.FlipdroidApplications;
import com.goal98.flipdroid.activity.PageActivity;
import com.goal98.flipdroid.model.Article;
import com.goal98.flipdroid.util.DeviceInfo;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class Page {
    protected int heightSum;

    protected WeiboPageView weiboPageView;

    protected List<Article> articleList;
    private PageActivity activity;
    protected DeviceInfo deviceInfo;


    public DeviceInfo getDeviceInfoFromApplicationContext(){
        FlipdroidApplications fa = (FlipdroidApplications) activity.getApplicationContext();
        return fa.getDeviceInfo();
    }

    public Page(PageActivity activity) {
        this.activity = activity;
        this.deviceInfo = getDeviceInfoFromApplicationContext();
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
