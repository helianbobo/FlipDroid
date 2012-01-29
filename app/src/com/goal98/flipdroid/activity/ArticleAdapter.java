package com.goal98.flipdroid.activity;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.goal98.flipdroid.model.Article;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: janexie
 * Date: 12-1-26
 * Time: 下午8:05
 * To change this template use File | Settings | File Templates.
 */
public class ArticleAdapter extends PaginationLoaderAdapter {
    protected Context context;

    public ArticleAdapter(Activity activity, ListView listView, int progressDrawableResourceId, int layoutId, PaginationLoaderService loaderService, List<Article> videoDetailInfos, int nodataview) {
        super(activity, listView, layoutId, progressDrawableResourceId, videoDetailInfos, nodataview, loaderService);
        this.context = activity;
    }

    public ArticleAdapter(Activity activity, ListView listView, int progressDrawableResourceId, int layoutId, PaginationLoaderService loaderService, int nodataview) {
        this(activity, listView, progressDrawableResourceId, layoutId, loaderService, new ArrayList(), nodataview);
    }

    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
    }
}
