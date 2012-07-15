package com.goal98.girl.view;

import android.app.Activity;
import com.goal98.girl.model.Article;
import com.srz.androidtools.util.DeviceInfo;

import java.util.LinkedList;
import java.util.List;

public class Page {
    protected int heightSum;

//    protected ThumbnailViewContainer thumbnailViewContainer;

    protected List<Article> articleList;
    protected Activity activity;
    protected DeviceInfo deviceInfo;


    public DeviceInfo getDeviceInfoFromApplicationContext(){
        return DeviceInfo.getInstance(activity);
    }

    public Page(Activity activity) {
        this.activity = activity;
        this.deviceInfo = getDeviceInfoFromApplicationContext();
        this.articleList = new LinkedList<Article>();
//        thumbnailViewContainer = new ThumbnailViewContainer((PageActivity) activity);
    }

    public List<Article> getArticleList() {
        return articleList;
    }

    public boolean addArticle(final Article article) {
        articleList.add(article);
        return true;
    }
}
